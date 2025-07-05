package com.fattie.compatlayer.nativebridge;

import java.io.File;

/**
 * Loads the native shared library from the /mods/natives directory.
 */
public class NativeLoader {
    public static void load() {
        String os = System.getProperty("os.name").toLowerCase();
        String libName = os.contains("win") ? "dlss_bridge.dll" : "libdlss_bridge.so";

        File nativeDir = new File("mods/natives");
        File libFile = new File(nativeDir, libName);

        if (!libFile.exists()) {
            throw new RuntimeException("Native library not found: " + libFile.getAbsolutePath());
        }

        System.load(libFile.getAbsolutePath());
        System.out.println("[CompatLayer] Loaded native library: " + libFile.getName());
    }
}