#include "FSRBridge.h"
#include "InteropBridge.h"
#include "InteropImage.h"
#include <ffx_fsr2.h>
#include <vulkan/vulkan.h>
#include <iostream>
#include <stdexcept>

// External Vulkan handles (assumed initialized)
extern VkDevice device;
extern VkPhysicalDevice physicalDevice;
extern VkCommandBuffer commandBuffer;
extern VkQueue graphicsQueue;

// FSR2 context
static FfxFsr2Context fsrContext;
static FfxInterface ffxInterface = {};
static float renderScale = 0.5f;
static bool fsrInitialized = false;

// Output image (shared with OpenGL)
static VkImage vkOutputImage = VK_NULL_HANDLE;

// Utility: throw Java exception
void throwJava(JNIEnv* env, const char* msg) {
    jclass exClass = env->FindClass("com/fattie/compatlayer/native/UpscalerException");
    if (exClass) env->ThrowNew(exClass, msg);
}

JNIEXPORT void JNICALL Java_com_fattie_compatlayer_native_FSRBridge_setRenderScale
  (JNIEnv*, jclass, jfloat scale) {
    renderScale = scale;
}

JNIEXPORT void JNICALL Java_com_fattie_compatlayer_native_FSRBridge_reinitializeFSR
  (JNIEnv* env, jclass, jint renderW, jint renderH, jint displayW, jint displayH) {
    if (fsrInitialized) {
        ffxFsr2ContextDestroy(&fsrContext);
        fsrInitialized = false;
    }

    // Initialize FFX interface (assumes Vulkan backend)
    ffxInterface = ffxGetInterfaceVK(device, physicalDevice, graphicsQueue, commandBuffer);

    FfxFsr2ContextDescription desc = {};
    desc.device = &ffxInterface;
    desc.maxRenderSize.width = renderW;
    desc.maxRenderSize.height = renderH;
    desc.displaySize.width = displayW;
    desc.displaySize.height = displayH;
    desc.flags = FFX_FSR2_ENABLE_HIGH_DYNAMIC_RANGE;

    FfxErrorCode err = ffxFsr2ContextCreate(&fsrContext, &desc);
    if (err != FFX_OK) {
        throwJava(env, "Failed to initialize FSR2 context");
        return;
    }

    // Create shared output image
    vkOutputImage = createSharedOutputImage(device, physicalDevice, displayW, displayH, VK_FORMAT_R8G8B8A8_UNORM);

    fsrInitialized = true;
    std::cout << "[FSRBridge] Initialized FSR2 with " << renderW << "x" << renderH << " → " << displayW << "x" << displayH << std::endl;
}

JNIEXPORT void JNICALL Java_com_fattie_compatlayer_native_FSRBridge_invoke
  (JNIEnv* env, jclass, jint colorTex, jint depthTex, jint velocityTex, jint width, jint height) {
    if (!fsrInitialized) {
        throwJava(env, "FSR2 not initialized");
        return;
    }

    try {
        // Import OpenGL textures into Vulkan
        VkImage vkColor = importGLTextureFD(colorTex, device, physicalDevice, width, height, VK_FORMAT_R8G8B8A8_UNORM);
        VkImage vkDepth = importGLTextureFD(depthTex, device, physicalDevice, width, height, VK_FORMAT_D32_SFLOAT);
        VkImage vkMotion = importGLTextureFD(velocityTex, device, physicalDevice, width, height, VK_FORMAT_R16G16_SFLOAT);

        FfxFsr2DispatchDescription dispatch = {};
        dispatch.commandList = commandBuffer;
        dispatch.color = { vkColor, VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL };
        dispatch.depth = { vkDepth, VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL };
        dispatch.motionVectors = { vkMotion, VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL };
        dispatch.output = { vkOutputImage, VK_IMAGE_LAYOUT_GENERAL };
        dispatch.renderSize.width = width;
        dispatch.renderSize.height = height;
        dispatch.cameraNear = 0.1f;
        dispatch.cameraFar = 1000.0f;
        dispatch.cameraFovAngleVertical = 1.0472f; // ~60 degrees
        dispatch.frameTimeDelta = 1.0f / 60.0f;
        dispatch.preExposure = 1.0f;
        dispatch.reset = false;
        dispatch.enableSharpening = true;
        dispatch.sharpness = 0.2f;

        FfxErrorCode err = ffxFsr2ContextDispatch(&fsrContext, &dispatch);
        if (err != FFX_OK) {
            throwJava(env, "FSR2 dispatch failed");
        }

    } catch (const std::exception& e) {
        throwJava(env, e.what());
    }
}

JNIEXPORT void JNICALL Java_com_fattie_compatlayer_native_FSRBridge_shutdown
  (JNIEnv*, jclass) {
    if (fsrInitialized) {
        ffxFsr2ContextDestroy(&fsrContext);
        fsrInitialized = false;
        std::cout << "[FSRBridge] FSR2 context destroyed." << std::endl;
    }
    destroySharedOutputImage(device);
}