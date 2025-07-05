package com.fattie.compatlayer.nativebridge;

/**
 * Thrown when a native upscaler (DLSS, FSR, DSR) fails to initialize or execute.
 */
public class UpscalerException extends RuntimeException {
    public UpscalerException(String message) {
        super(message);
    }

    public UpscalerException(String message, Throwable cause) {
        super(message, cause);
    }
}