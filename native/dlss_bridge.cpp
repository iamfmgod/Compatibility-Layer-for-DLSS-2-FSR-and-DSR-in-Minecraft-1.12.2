package com.fattie.compatlayer.native;

/**
 * Java interface to the native DLSS bridge.
 * Communicates with Vulkan + NGX backend via JNI.
 */
public class DLSSBridge {
    static {
        System.loadLibrary("dlss_bridge");
    }

    /**
     * Submits a frame to DLSS for upscaling.
     *
     * @param colorTex     OpenGL texture ID (color input)
     * @param depthTex     OpenGL texture ID (depth input)
     * @param velocityTex  OpenGL texture ID (motion vectors)
     * @param width        Render resolution width
     * @param height       Render resolution height
     * @throws UpscalerException if DLSS evaluation fails
     */
    public static native void invoke(int colorTex, int depthTex, int velocityTex, int width, int height) throws UpscalerException;

    /**
     * Sets the DLSS quality mode (e.g. BALANCED, QUALITY).
     */
    public static native void setQualityMode(String mode);

    /**
     * Sets the render scale (e.g. 0.5 for 50% resolution).
     */
    public static native void setRenderScale(float scale);

    /**
     * Reinitializes DLSS with new input/output resolution.
     *
     * @param renderWidth   Input resolution width
     * @param renderHeight  Input resolution height
     * @param displayWidth  Output resolution width
     * @param displayHeight Output resolution height
     */
    public static native void reinitializeDLSS(int renderWidth, int renderHeight, int displayWidth, int displayHeight);

    /**
     * Shuts down DLSS and releases Vulkan resources.
     */
    public static native void shutdown();
}