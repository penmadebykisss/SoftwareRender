package RenderingModes.rasterization;

import RenderingModes.core.RenderContext;
import RenderingModes.data.*;
import RenderingModes.shaders.Shader;

public class TriangleRasterizer {

    public void rasterize(Triangle triangle, RenderContext context, Shader shader) {
        if (triangle == null || context == null || shader == null) return;

        Vertex[] vertices = triangle.getVertices();

        // Преобразуем вершины в массив для шейдера
        float[][] vertexData = prepareVertexData(vertices);

        // Здесь будет алгоритм растеризации
        // Пока просто выводим информацию
        System.out.println("Rasterizing triangle: " + triangle);
        System.out.println("Using shader: " + shader.getClass().getSimpleName());

        // Тестовая точка в центре треугольника
        BarycentricCoords bc = new BarycentricCoords(0.33f, 0.33f, 0.34f);
        Color pixelColor = shader.shade(vertexData, bc, 0, 0, 0.5f, context);

        if (pixelColor != null) {
            System.out.println("Test pixel color: " + pixelColor);
        }
    }

    private float[][] prepareVertexData(Vertex[] vertices) {
        float[][] data = new float[3][11]; // x,y,z, u,v, nx,ny,nz, r,g,b,a

        for (int i = 0; i < 3; i++) {
            Vertex v = vertices[i];

            // Позиция
            data[i][0] = v.getX();
            data[i][1] = v.getY();
            data[i][2] = v.getZ();

            // UV-координаты
            data[i][3] = v.getU();
            data[i][4] = v.getV();

            // Нормаль
            data[i][5] = v.getNx();
            data[i][6] = v.getNy();
            data[i][7] = v.getNz();

            // Цвет
            Color c = v.getColor();
            data[i][8] = c.getR();
            data[i][9] = c.getG();
            data[i][10] = c.getB();
            // data[i][11] = c.getA(); // если нужно альфа
        }

        return data;
    }
}