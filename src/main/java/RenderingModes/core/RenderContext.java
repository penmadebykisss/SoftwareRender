package RenderingModes.core;

import RenderingModes.data.Color;
import RenderingModes.shaders.Shader;

public class RenderContext {
    private final int width;
    private final int height;
    private final ZBuffer zBuffer;
    private Shader activeShader;
    private Color clearColor;

    public RenderContext(int width, int height) {
        this.width = width;
        this.height = height;
        this.zBuffer = new ZBuffer(width, height);
        this.clearColor = Color.BLACK;
    }

    public void clear() {
        zBuffer.clear();
    }

    public boolean testDepth(int x, int y, float depth) {
        return zBuffer.testAndSet(x, y, depth);
    }

    // Геттеры и сеттеры
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public ZBuffer getZBuffer() { return zBuffer; }
    public Shader getActiveShader() { return activeShader; }
    public void setActiveShader(Shader shader) { this.activeShader = shader; }
    public Color getClearColor() { return clearColor; }
    public void setClearColor(Color color) { this.clearColor = color; }
}