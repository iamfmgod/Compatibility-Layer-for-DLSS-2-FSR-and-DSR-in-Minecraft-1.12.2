package com.fattie.compatlayer.client.hud;

import com.fattie.compatlayer.client.input.KeybindHandler;
import com.fattie.compatlayer.config.CompatConfig;
import com.fattie.compatlayer.util.PerformanceLogger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class UpscalerHUDOverlay {
    private long lastFrameTime = System.nanoTime();
    private float smoothedFrameTime = 0;

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Text event) {
        if (!KeybindHandler.isHUDEnabled()) return;

        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution res = new ScaledResolution(mc);

        long now = System.nanoTime();
        float deltaMs = (now - lastFrameTime) / 1_000_000f;
        lastFrameTime = now;
        smoothedFrameTime = smoothedFrameTime * 0.9f + deltaMs * 0.1f;

        String upscaler = CompatConfig.getActiveUpscaler();
        String fps = String.format("Frame Time: %.1f ms", smoothedFrameTime);
        String resInfo = String.format("Upscaler: %s", upscaler);

        mc.fontRenderer.drawStringWithShadow(resInfo, 5, 5, 0xFFFFFF);
        mc.fontRenderer.drawStringWithShadow(fps, 5, 15, 0xAAAAAA);

        PerformanceLogger.init();
        PerformanceLogger.log(smoothedFrameTime, res.getScaledWidth(), res.getScaledHeight());
    }
}