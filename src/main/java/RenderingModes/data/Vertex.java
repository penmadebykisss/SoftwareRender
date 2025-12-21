package RenderingModes.data;

import java.util.Objects;

/**
 * Упрощённое представление вершины для рендеринга
 * Не зависит от внешних классов
 */
public class Vertex {
    private static int nextId = 0;
    private final int id;
    private final float x, y, z;     // Позиция
    private final float u, v;        // Текстурные координаты (опционально)
    private final float nx, ny, nz;  // Нормаль (опционально)
    private final Color color;       // Цвет вершины (опционально)

    public Vertex(float x, float y, float z) {
        this(x, y, z, 0, 0, 0, 0, 1, Color.WHITE);
    }

    public Vertex(float x, float y, float z, float u, float v,
                  float nx, float ny, float nz, Color color) {
        this.id = nextId++;
        this.x = x;
        this.y = y;
        this.z = z;
        this.u = u;
        this.v = v;
        this.nx = nx;
        this.ny = ny;
        this.nz = nz;
        this.color = color != null ? color : Color.WHITE;
    }

    // Новый геттер для ID
    public int getId() {
        return id;
    }

    // Геттеры
    public float getX() { return x; }
    public float getY() { return y; }
    public float getZ() { return z; }
    public float getU() { return u; }
    public float getV() { return v; }
    public float getNx() { return nx; }
    public float getNy() { return ny; }
    public float getNz() { return nz; }
    public Color getColor() { return color; }

    // Преобразование в массив для быстрого доступа
    public float[] toPositionArray() {
        return new float[]{x, y, z};
    }

    public float[] toUVArray() {
        return new float[]{u, v};
    }

    public float[] toNormalArray() {
        return new float[]{nx, ny, nz};
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vertex)) return false;
        Vertex vertex = (Vertex) o;
        return Float.compare(vertex.x, x) == 0 &&
                Float.compare(vertex.y, y) == 0 &&
                Float.compare(vertex.z, z) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }

    @Override
    public String toString() {
        return String.format("Vertex(id=%d, %.2f, %.2f, %.2f)", id, x, y, z);
    }
}