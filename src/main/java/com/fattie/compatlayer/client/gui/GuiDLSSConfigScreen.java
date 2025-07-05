package com.fattie.compatlayer.client.gui;

import com.fattie.compatlayer.config.CompatConfig;
import com.fattie.compatlayer.config.DLSSSettings;
import com.fattie.compatlayer.nativebridge.DLSSBridge;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.client.config.GuiSlider;

import java.util.Arrays;
import java.util.List;

public class GuiDLSSConfigScreen extends GuiScreen {
    private final GuiScreen parent;
    private GuiSlider scaleSlider;
    private GuiButton toggleButton;
    private GuiButton applyButton;
    private GuiButton resetButton;
    private GuiButton qualityButton;

    private boolean enabled;
    private float scale;
    private String quality;
    private int qualityIndex;

    private static final List<String> QUALITY_MODES = Arrays.asList(
            "ULTRA_PERFORMANCE", "PERFORMANCE", "BALANCED", "QUALITY", "ULTRA_QUALITY"
    );

    public GuiDLSSConfigScreen() {
        this.parent = Minecraft.getMinecraft().currentScreen;
        DLSSSettings settings = CompatConfig.dlssSettings;
        this.enabled = settings.isEnabled();
        this.scale = settings.getRenderScale();
        this.quality = settings.getQualityMode().name();
        this.qualityIndex = QUALITY_MODES.indexOf(quality);
        if (qualityIndex < 0) qualityIndex = 2; // default to BALANCED
    }

    @Override
    public void initGui() {
        int centerX = width / 2;
        int y = height / 4;

        toggleButton = new GuiButton(0, centerX - 100, y, 200, 20,
                I18n.format("gui.compatlayer." + (enabled ? "enabled" : "disabled")));
        qualityButton = new GuiButton(1, centerX - 100, y + 24, 200, 20,
                I18n.format("gui.compatlayer.quality") + ": " + QUALITY_MODES.get(qualityIndex));

        // Forge GuiSlider constructor:
        // GuiSlider(int id, int x, int y, int width, int height, String prefix, String suffix, double min, double max, double current, boolean showDec, boolean drawStr, ISlider par)
        scaleSlider = new GuiSlider(2, centerX - 100, y + 48, 200, 20,
                "Scale: ", "", 0.25d, 1.0d, scale, false, true, null);

        applyButton = new GuiButton(3, centerX - 100, y + 80, 98, 20, I18n.format("gui.compatlayer.apply"));
        resetButton = new GuiButton(4, centerX + 2, y + 80, 98, 20, I18n.format("gui.compatlayer.reset"));

        this.buttonList.add(toggleButton);
        this.buttonList.add(qualityButton);
        this.buttonList.add(scaleSlider);
        this.buttonList.add(applyButton);
        this.buttonList.add(resetButton);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 0: // Toggle
                enabled = !enabled;
                toggleButton.displayString = I18n.format("gui.compatlayer." + (enabled ? "enabled" : "disabled"));
                break;
            case 1: // Cycle quality
                qualityIndex = (qualityIndex + 1) % QUALITY_MODES.size();
                quality = QUALITY_MODES.get(qualityIndex);
                qualityButton.displayString = I18n.format("gui.compatlayer.quality") + ": " + quality;
                break;
            case 3: // Apply
                scale = (float) scaleSlider.getValue();
                DLSSSettings settings = CompatConfig.dlssSettings;
                settings.setEnabled(enabled);
                settings.setRenderScale(scale);
                settings.setQualityMode(DLSSSettings.QualityMode.valueOf(quality));
                CompatConfig.save();

                DLSSBridge.setRenderScale(scale);
                DLSSBridge.setQualityMode(quality);

                int renderW = (int) (Minecraft.getMinecraft().displayWidth * scale);
                int renderH = (int) (Minecraft.getMinecraft().displayHeight * scale);
                int displayW = Minecraft.getMinecraft().displayWidth;
                int displayH = Minecraft.getMinecraft().displayHeight;
                DLSSBridge.reinitializeDLSS(renderW, renderH, displayW, displayH);
                break;
            case 4: // Reset
                enabled = true;
                scale = 0.5f;
                qualityIndex = 2;
                quality = QUALITY_MODES.get(qualityIndex);
                initGui();
                break;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        drawCenteredString(fontRenderer, I18n.format("gui.compatlayer.title"), width / 2, 20, 0xFFFFFF);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws java.io.IOException {
        if (keyCode == 1) { // ESC
            this.mc.displayGuiScreen(parent);
        } else {
            super.keyTyped(typedChar, keyCode);
        }
    }
}