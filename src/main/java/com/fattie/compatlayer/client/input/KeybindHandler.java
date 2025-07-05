package com.fattie.compatlayer.client.input;

import com.fattie.compatlayer.config.CompatConfig;
import com.fattie.compatlayer.config.DLSSSettings;
import com.fattie.compatlayer.nativebridge.DLSSBridge;
import com.fattie.compatlayer.nativebridge.FSRBridge;
import com.fattie.compatlayer.nativebridge.DSRScaler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

public class KeybindHandler {
    private static final String[] UPSCALERS = { "DLSS", "FSR", "DSR", "OFF" };
    private static int currentIndex = 0;
    private static boolean hudEnabled = true;

    public static final KeyBinding toggleUpscaler = new KeyBinding("key.compatlayer.toggle_upscaler", Keyboard.KEY_U, "CompatLayer");
    public static final KeyBinding toggleHUD = new KeyBinding("key.compatlayer.toggle_hud", Keyboard.KEY_H, "CompatLayer");

    public static void register() {
        ClientRegistry.registerKeyBinding(toggleUpscaler);
        ClientRegistry.registerKeyBinding(toggleHUD);
    }

    public static boolean isHUDEnabled() {
        return hudEnabled;
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        Minecraft mc = Minecraft.getMinecraft();

        if (toggleUpscaler.isPressed()) {
            currentIndex = (currentIndex + 1) % UPSCALERS.length;
            String selected = UPSCALERS[currentIndex];
            CompatConfig.setActiveUpscaler(selected);
            mc.ingameGUI.setOverlayMessage("Upscaler: " + selected, false);

            try {
                int renderW = (int) (mc.displayWidth * CompatConfig.dlssSettings.getRenderScale());
                int renderH = (int) (mc.displayHeight * CompatConfig.dlssSettings.getRenderScale());
                int displayW = mc.displayWidth;
                int displayH = mc.displayHeight;

                switch (selected) {
                    case "DLSS":
                        if (CompatConfig.dlssSettings.isEnabled()) {
                            DLSSSettings s = CompatConfig.dlssSettings;
                            DLSSBridge.setQualityMode(s.getQualityMode().name());
                            DLSSBridge.setRenderScale(s.getRenderScale());
                            DLSSBridge.reinitializeDLSS(renderW, renderH, displayW, displayH);
                        }
                        break;
                    case "FSR":
                        FSRBridge.setRenderScale(CompatConfig.dlssSettings.getRenderScale());
                        FSRBridge.reinitializeFSR(renderW, renderH, displayW, displayH);
                        break;
                    case "DSR":
                        DSRScaler.setScaleFactor(CompatConfig.dlssSettings.getRenderScale());
                        break;
                    case "OFF":
                        // No-op
                        break;
                }
            } catch (Exception e) {
                System.err.println("[CompatLayer] Upscaler switch failed: " + e.getMessage());
            }
        }

        if (toggleHUD.isPressed()) {
            hudEnabled = !hudEnabled;
            mc.ingameGUI.setOverlayMessage("HUD: " + (hudEnabled ? "Enabled" : "Disabled"), false);
        }
    }
}