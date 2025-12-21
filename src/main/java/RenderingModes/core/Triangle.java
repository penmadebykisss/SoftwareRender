package RenderingModes.core;

public class Triangle {
    public final int[] vertexIndices = new int[3];    // v1, v2, v3
    public final int[] textureIndices = new int[3];   // vt1, vt2, vt3
    public final int[] normalIndices = new int[3];    // vn1, vn2, vn3

    private Vector3f faceNormal;  // Нормаль всей грани
    private Vector3f[] vertexNormals; // Сглаженные нормали вершин

    // Геттеры и сеттеры
}
