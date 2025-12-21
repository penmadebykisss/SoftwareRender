package RenderingModes.triangulation;

import Interface.model.Model;
import Interface.model.Polygon;
import Interface.math.Vector3f;
import Interface.math.Vector2f;
import java.util.ArrayList;
import java.util.List;

public class TriangulationFacade {

    public static Model ensureTriangulated(Model model) {
        if (isTriangulated(model)) {
            return model;
        }
        return triangulate(model);
    }

    public static boolean checkIfTriangulated(Model model) {
        return isTriangulated(model);
    }

    public static Model forceTriangulate(Model model) {
        return triangulate(model);
    }

    private static boolean isTriangulated(Model model) {
        for (Polygon polygon : model.getPolygons()) {
            if (polygon.getVertexIndices().size() != 3) {
                return false;
            }
        }
        return true;
    }

    private static Model triangulate(Model originalModel) {
        Model triangulatedModel = new Model();

        // 1. Вершины
        triangulatedModel.setVertices(new ArrayList<>(originalModel.getVertices()));

        // 2. Текстурные координаты - это ArrayList<Vector2f>
        triangulatedModel.setTextureVertices(new ArrayList<>(originalModel.getTextureVertices()));

        // 3. Нормали
        triangulatedModel.setNormals(new ArrayList<>(originalModel.getNormals()));

        // 4. Триангулируем полигоны
        List<Polygon> triangulatedPolygons = new ArrayList<>();

        for (Polygon polygon : originalModel.getPolygons()) {
            triangulatePolygon(polygon, triangulatedPolygons);
        }

        triangulatedModel.setPolygons(new ArrayList<>(triangulatedPolygons));
        return triangulatedModel;
    }

    private static void triangulatePolygon(Polygon polygon, List<Polygon> result) {
        List<Integer> vertexIndices = polygon.getVertexIndices();
        List<Integer> textureIndices = polygon.getTextureVertexIndices();
        List<Integer> normalIndices = polygon.getNormalIndices();

        int vertexCount = vertexIndices.size();

        // Если уже треугольник
        if (vertexCount == 3) {
            result.add(createTrianglePolygon(vertexIndices, textureIndices, normalIndices));
            return;
        }

        // Веерная триангуляция
        for (int i = 1; i < vertexCount - 1; i++) {
            List<Integer> triVertices = new ArrayList<>();
            triVertices.add(vertexIndices.get(0));
            triVertices.add(vertexIndices.get(i));
            triVertices.add(vertexIndices.get(i + 1));

            List<Integer> triTextures = null;
            if (textureIndices != null && !textureIndices.isEmpty() && textureIndices.size() == vertexCount) {
                triTextures = new ArrayList<>();
                triTextures.add(textureIndices.get(0));
                triTextures.add(textureIndices.get(i));
                triTextures.add(textureIndices.get(i + 1));
            }

            List<Integer> triNormals = null;
            if (normalIndices != null && !normalIndices.isEmpty() && normalIndices.size() == vertexCount) {
                triNormals = new ArrayList<>();
                triNormals.add(normalIndices.get(0));
                triNormals.add(normalIndices.get(i));
                triNormals.add(normalIndices.get(i + 1));
            }

            result.add(createTrianglePolygon(triVertices, triTextures, triNormals));
        }
    }

    private static Polygon createTrianglePolygon(
            List<Integer> vertexIndices,
            List<Integer> textureIndices,
            List<Integer> normalIndices) {

        Polygon triangle = new Polygon();
        triangle.setVertexIndices(new ArrayList<>(vertexIndices));

        if (textureIndices != null && !textureIndices.isEmpty()) {
            triangle.setTextureVertexIndices(new ArrayList<>(textureIndices));
        }

        if (normalIndices != null && !normalIndices.isEmpty()) {
            triangle.setNormalIndices(new ArrayList<>(normalIndices));
        }

        return triangle;
    }
}