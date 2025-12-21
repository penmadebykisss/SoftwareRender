package RenderingModes.data;

public class Color {
    private final float r, g, b, a;

    public Color(float r, float g, float b, float a) {
        this.r = clamp(r);
        this.g = clamp(g);
        this.b = clamp(b);
        this.a = clamp(a);
    }

    public Color(float r, float g, float b) {
        this(r, g, b, 1.0f);
    }

    private float clamp(float value) {
        return Math.max(0.0f, Math.min(1.0f, value));
    }

    public Color multiply(float factor) {
        return new Color(r * factor, g * factor, b * factor, a);
    }

    public Color add(Color other) {
        return new Color(r + other.r, g + other.g, b + other.b, a + other.a);
    }

    public Color blend(Color other, float alpha) {
        float invAlpha = 1.0f - alpha;
        return new Color(
                r * invAlpha + other.r * alpha,
                g * invAlpha + other.g * alpha,
                b * invAlpha + other.b * alpha,
                a * invAlpha + other.a * alpha
        );
    }

    public float getR() { return r; }
    public float getG() { return g; }
    public float getB() { return b; }
    public float getA() { return a; }

    public int toRGB() {
        return ((int)(r * 255) << 16) | ((int)(g * 255) << 8) | (int)(b * 255);
    }

    @Override
    public String toString() {
        return String.format("Color(%.2f, %.2f, %.2f, %.2f)", r, g, b, a);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Color)) return false;
        Color other = (Color) obj;
        return Math.abs(r - other.r) < 0.001f &&
                Math.abs(g - other.g) < 0.001f &&
                Math.abs(b - other.b) < 0.001f &&
                Math.abs(a - other.a) < 0.001f;
    }

    // Полный набор цветов
    public static final Color RED = new Color(1.0f, 0.0f, 0.0f);
    public static final Color GREEN = new Color(0.0f, 1.0f, 0.0f);
    public static final Color BLUE = new Color(0.0f, 0.0f, 1.0f);
    public static final Color WHITE = new Color(1.0f, 1.0f, 1.0f);
    public static final Color BLACK = new Color(0.0f, 0.0f, 0.0f);
    public static final Color GRAY = new Color(0.5f, 0.5f, 0.5f);
    public static final Color YELLOW = new Color(1.0f, 1.0f, 0.0f);
    public static final Color CYAN = new Color(0.0f, 1.0f, 1.0f);
    public static final Color MAGENTA = new Color(1.0f, 0.0f, 1.0f);
    public static final Color ORANGE = new Color(1.0f, 0.5f, 0.0f);
    public static final Color PURPLE = new Color(0.5f, 0.0f, 0.5f);
    public static final Color PINK = new Color(1.0f, 0.75f, 0.8f);
    public static final Color BROWN = new Color(0.6f, 0.4f, 0.2f);
}