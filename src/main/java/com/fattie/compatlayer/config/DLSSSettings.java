package com.fattie.compatlayer.config;

public class DLSSSettings {
    public enum QualityMode {
        ULTRA_PERFORMANCE, PERFORMANCE, BALANCED, QUALITY, ULTRA_QUALITY
    }

    private boolean enabled = true;
    private float renderScale = 0.5f;
    private QualityMode qualityMode = QualityMode.BALANCED;

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public float getRenderScale() { return renderScale; }
    public void setRenderScale(float renderScale) { this.renderScale = renderScale; }

    public QualityMode getQualityMode() { return qualityMode; }
    public void setQualityMode(QualityMode qualityMode) { this.qualityMode = qualityMode; }
}