package RenderingModes;

public class ZBuffer {
    private final int width;
    private final int height;
    private final double[] data;

    public ZBuffer(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Width and height must be positive");
        }

        this.width = width;
        this.height = height;
        this.data = new double[width * height];
        clear();
    }

    public void clear() {
        final double emptyDepth = Double.POSITIVE_INFINITY;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                data[index(x, y)] = emptyDepth;
            }
        }
    }

    private int index(int x, int y) {
        return y * width + x;
    }

    public boolean testAndSet(int x, int y, double depth) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return false;
        }

        int idx = index(x, y);
        if (depth < data[idx]) {
            data[idx] = depth;
            return true;
        }
        return false;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public double getDepth(int x, int y) {
        return data[index(x, y)];
    }
}