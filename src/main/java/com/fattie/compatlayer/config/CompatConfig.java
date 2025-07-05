package com.fattie.compatlayer.config;

import net.minecraftforge.common.config.Configuration;

import java.io.File;

public class CompatConfig {
    public static DLSSSettings dlssSettings = new DLSSSettings();
    private static String activeUpscaler = "DLSS";

    private static Configuration config;

    public static void load(File configFile) {
        config = new Configuration(configFile);
        config.load();

        dlssSettings.setEnabled(config.getBoolean("Enable DLSS", "DLSS", true, "Enable NVIDIA DLSS 2"));
        dlssSettings.setRenderScale((float) config.getFloat("Render Scale", "DLSS", 0.5f, 0.25f, 1.0f, "DLSS render resolution scale"));
        String mode = config.getString("Quality Mode", "DLSS", "BALANCED", "DLSS quality mode");

        try {
            dlssSettings.setQualityMode(DLSSSettings.QualityMode.valueOf(mode.toUpperCase()));
        } catch (IllegalArgumentException e) {
            dlssSettings.setQualityMode(DLSSSettings.QualityMode.BALANCED);
        }

        activeUpscaler = config.getString("Active Upscaler", "General", "DLSS", "Current upscaler mode");

        if (config.hasChanged()) config.save();
    }

    public static void save() {
        if (config == null) return;

        config.get("DLSS", "Enable DLSS", true).set(dlssSettings.isEnabled());
        config.get("DLSS", "Render Scale", 0.5f).set(dlssSettings.getRenderScale());
        config.get("DLSS", "Quality Mode", "BALANCED").set(dlssSettings.getQualityMode().name());

        config.get("General", "Active Upscaler", "DLSS").set(activeUpscaler);

        config.save();
    }

    public static String getActiveUpscaler() {
        return activeUpscaler;
    }

    public static void setActiveUpscaler(String mode) {
        activeUpscaler = mode;
    }
}