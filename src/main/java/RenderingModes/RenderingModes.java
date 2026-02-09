package RenderingModes;

public class RenderingModes {
    private boolean drawWireframe;
    private boolean drawFilled; // Добавлено поле
    private boolean useTexture;
    private boolean useLighting;

    public RenderingModes() {
        this(false, false, false, false);
    }

    public RenderingModes(boolean drawWireframe, boolean drawFilled, boolean useTexture, boolean useLighting) {
        this.drawWireframe = drawWireframe;
        this.drawFilled = drawFilled;
        this.useTexture = useTexture;
        this.useLighting = useLighting;
    }

    public boolean isDrawWireframe() {
        return drawWireframe;
    }

    public void setDrawWireframe(boolean drawWireframe) {
        this.drawWireframe = drawWireframe;
    }

    public boolean isDrawFilled() {
        return drawFilled;
    }

    public void setDrawFilled(boolean drawFilled) {
        this.drawFilled = drawFilled;
    }

    public boolean isUseTexture() {
        return useTexture;
    }

    public void setUseTexture(boolean useTexture) {
        this.useTexture = useTexture;
    }

    public boolean isUseLighting() {
        return useLighting;
    }

    public void setUseLighting(boolean useLighting) {
        this.useLighting = useLighting;
    }
}