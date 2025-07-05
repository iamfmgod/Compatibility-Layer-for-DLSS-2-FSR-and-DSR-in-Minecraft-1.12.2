package com.fattie.compatlayer.nativebridge;

/**
 * Java interface to the native DLSS bridge.
 * Communicates with Vulkan + NGX backend via JNI.
 */
public class DLSSBridge {
    static {
        System.loadLibrary("dlss_bridge");
    }

    public static native void invoke(int colorTex, int depthTex, int velocityTex, int width, int height) throws UpscalerException;

    public static native void setQualityMode(String mode);

    public static native void setRenderScale(float scale);

    public static native void reinitializeDLSS(int renderWidth, int renderHeight, int displayWidth, int displayHeight);

    public static native void shutdown();
}