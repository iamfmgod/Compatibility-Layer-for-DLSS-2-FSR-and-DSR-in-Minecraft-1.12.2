package com.fattie.compatlayer.nativebridge;

/**
 * Java interface to the native DSR scaler.
 * Performs OpenGL-only upsample + downsample fallback.
 */
public class DSRScaler {
    static {
        System.loadLibrary("dlss_bridge");
    }

    public static native void scale(int inputTex, int width, int height);

    public static native void setScaleFactor(float factor);
}