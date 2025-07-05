package com.fattie.compatlayer.nativebridge;

/**
 * Java interface to the native FSR2 bridge.
 * Communicates with Vulkan + FidelityFX backend via JNI.
 */
public class FSRBridge {
    static {
        System.loadLibrary("dlss_bridge"); // Same native lib as DLSSBridge
    }

    /**
     * Submits a frame to FSR2 for upscaling.
     *
     * @param colorTex     OpenGL texture ID (color input)
     * @param depthTex     OpenGL texture ID (depth input)
     * @param velocityTex  OpenGL texture ID (motion vectors)
     * @param width        Render resolution width
     * @param height       Render resolution height
     * @throws UpscalerException if FSR2 dispatch fails
     */
    public static native void invoke(int colorTex, int depthTex, int velocityTex, int width, int height) throws UpscalerException;

    /**
     * Sets the render scale (e.g. 0.5 for 50% resolution).
     */
    public static native void setRenderScale(float scale);

    /**
     * Reinitializes FSR2 with new input/output resolution.
     *
     * @param renderWidth   Input resolution width
     * @param renderHeight  Input resolution height
     * @param displayWidth  Output resolution width
     * @param displayHeight Output resolution height
     */
    public static native void reinitializeFSR(int renderWidth, int renderHeight, int displayWidth, int displayHeight);

    /**
     * Shuts down FSR2 and releases Vulkan resources.
     */
    public static native void shutdown();
}