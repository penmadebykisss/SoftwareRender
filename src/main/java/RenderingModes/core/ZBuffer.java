package RenderingModes.core;

public class ZBuffer {
    private final float[][] buffer;
    private final int width;
    private final int height;

    public ZBuffer(int width, int height) {
        this.width = width;
        this.height = height;
        this.buffer = new float[height][width];
        clear();
    }

    public void clear() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                buffer[y][x] = Float.MAX_VALUE;
            }
        }
    }

    public boolean testAndSet(int x, int y, float depth) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return false;
        }

        if (depth < buffer[y][x]) {
            buffer[y][x] = depth;
            return true;
        }
        return false;
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
}