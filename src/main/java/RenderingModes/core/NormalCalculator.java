package RenderingModes.core;

import RenderingModes.data.*;
import java.util.*;

public class NormalCalculator {

    // Вычисление нормали для грани треугольника
    public static float[] calculateFaceNormal(float[] v0, float[] v1, float[] v2) {
        // Векторы сторон
        float[] u = {v1[0] - v0[0], v1[1] - v0[1], v1[2] - v0[2]};
        float[] v = {v2[0] - v0[0], v2[1] - v0[1], v2[2] - v0[2]};

        // Векторное произведение
        float[] normal = {
                u[1] * v[2] - u[2] * v[1],
                u[2] * v[0] - u[0] * v[2],
                u[0] * v[1] - u[1] * v[0]
        };

        // Нормализация
        float length = (float)Math.sqrt(normal[0]*normal[0] + normal[1]*normal[1] + normal[2]*normal[2]);
        if (length > 0.0001f) {
            normal[0] /= length;
            normal[1] /= length;
            normal[2] /= length;
        }

        return normal;
    }

    // Сглаживание нормалей по Гуро
    public static Map<Integer, float[]> calculateSmoothNormals(List<Triangle> triangles) {
        Map<Integer, List<float[]>> vertexToNormals = new HashMap<>();
        Map<Integer, float[]> result = new HashMap<>();

        // Собираем все нормали граней для каждой вершины
        for (Triangle triangle : triangles) {
            float[] faceNormal = triangle.calculateNormal();
            Vertex[] vertices = triangle.getVertices();

            for (int i = 0; i < 3; i++) {
                int vertexId = System.identityHashCode(vertices[i]);
                vertexToNormals.computeIfAbsent(vertexId, k -> new ArrayList<>())
                        .add(faceNormal);
            }
        }

        // Усредняем нормали для каждой вершины
        for (Map.Entry<Integer, List<float[]>> entry : vertexToNormals.entrySet()) {
            float[] avgNormal = {0, 0, 0};
            for (float[] normal : entry.getValue()) {
                avgNormal[0] += normal[0];
                avgNormal[1] += normal[1];
                avgNormal[2] += normal[2];
            }

            int count = entry.getValue().size();
            avgNormal[0] /= count;
            avgNormal[1] /= count;
            avgNormal[2] /= count;

            // Нормализуем результат
            float length = (float)Math.sqrt(avgNormal[0]*avgNormal[0] +
                    avgNormal[1]*avgNormal[1] +
                    avgNormal[2]*avgNormal[2]);
            if (length > 0.0001f) {
                avgNormal[0] /= length;
                avgNormal[1] /= length;
                avgNormal[2] /= length;
            }

            result.put(entry.getKey(), avgNormal);
        }

        return result;
    }
}