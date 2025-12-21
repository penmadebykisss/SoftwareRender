package RenderingModes.data;

import java.util.Arrays;

/**
 * Треугольник для рендеринга - основная примитивная единица
 */
public class Triangle {
    private final Vertex v0, v1, v2;
    private final int materialId; // ID материала (для текстуры/цвета)

    public Triangle(Vertex v0, Vertex v1, Vertex v2) {
        this(v0, v1, v2, 0);
    }

    public Triangle(Vertex v0, Vertex v1, Vertex v2, int materialId) {
        if (v0 == null || v1 == null || v2 == null) {
            throw new IllegalArgumentException("Triangle vertices cannot be null");
        }
        this.v0 = v0;
        this.v1 = v1;
        this.v2 = v2;
        this.materialId = materialId;
    }

    // Геттеры
    public Vertex getV0() { return v0; }
    public Vertex getV1() { return v1; }
    public Vertex getV2() { return v2; }
    public int getMaterialId() { return materialId; }

    public Vertex[] getVertices() {
        return new Vertex[]{v0, v1, v2};
    }

    // Получить массив позиций
    public float[][] getPositions() {
        return new float[][]{
                v0.toPositionArray(),
                v1.toPositionArray(),
                v2.toPositionArray()
        };
    }

    // Получить массив UV-координат
    public float[][] getUVs() {
        return new float[][]{
                v0.toUVArray(),
                v1.toUVArray(),
                v2.toUVArray()
        };
    }

    // Получить массив нормалей
    public float[][] getNormals() {
        return new float[][]{
                v0.toNormalArray(),
                v1.toNormalArray(),
                v2.toNormalArray()
        };
    }

    // Получить цвета вершин
    public Color[] getColors() {
        return new Color[]{
                v0.getColor(),
                v1.getColor(),
                v2.getColor()
        };
    }

    // Вычисление нормали треугольника
    public float[] calculateNormal() {
        float[] p0 = v0.toPositionArray();
        float[] p1 = v1.toPositionArray();
        float[] p2 = v2.toPositionArray();

        // Векторы сторон
        float[] u = {p1[0] - p0[0], p1[1] - p0[1], p1[2] - p0[2]};
        float[] v = {p2[0] - p0[0], p2[1] - p0[1], p2[2] - p0[2]};

        // Векторное произведение
        float[] normal = {
                u[1] * v[2] - u[2] * v[1],
                u[2] * v[0] - u[0] * v[2],
                u[0] * v[1] - u[1] * v[0]
        };

        // Нормализация
        float length = (float)Math.sqrt(
                normal[0]*normal[0] + normal[1]*normal[1] + normal[2]*normal[2]
        );

        if (length > 0) {
            normal[0] /= length;
            normal[1] /= length;
            normal[2] /= length;
        }

        return normal;
    }

    @Override
    public String toString() {
        return String.format("Triangle[\n  %s,\n  %s,\n  %s\n]",
                v0, v1, v2);
    }
}