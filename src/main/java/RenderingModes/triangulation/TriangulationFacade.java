package RenderingModes.triangulation;

import Interface.model.Model;
import Interface.model.Polygon;
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
        // Создаем новую модель
        Model triangulatedModel = new Model();

        // Копируем вершины, UV, нормали
        triangulatedModel.setVertices(new ArrayList<>(originalModel.getVertices()));
        triangulatedModel.setTextureVertices(new ArrayList<>(originalModel.getTextureVertices()));
        triangulatedModel.setNormals(new ArrayList<>(originalModel.getNormals()));

        // Триангулируем полигоны
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
            List<Integer> triVertices = List.of(
                    vertexIndices.get(0),
                    vertexIndices.get(i),
                    vertexIndices.get(i + 1)
            );

            List<Integer> triTextures = null;
            if (textureIndices != null && textureIndices.size() == vertexCount) {
                triTextures = List.of(
                        textureIndices.get(0),
                        textureIndices.get(i),
                        textureIndices.get(i + 1)
                );
            }

            List<Integer> triNormals = null;
            if (normalIndices != null && normalIndices.size() == vertexCount) {
                triNormals = List.of(
                        normalIndices.get(0),
                        normalIndices.get(i),
                        normalIndices.get(i + 1)
                );
            }

            result.add(createTrianglePolygon(triVertices, triTextures, triNormals));
        }
    }

    private static Polygon createTrianglePolygon(
            List<Integer> vertexIndices,
            List<Integer> textureIndices,
            List<Integer> normalIndices) {

        Polygon triangle = new Polygon();
        triangle.setVertexIndices(new ArrayList<>(vertexIndices)); // ArrayList

        if (textureIndices != null) {
            triangle.setTextureVertexIndices(new ArrayList<>(textureIndices)); // ArrayList
        }

        if (normalIndices != null) {
            triangle.setNormalIndices(new ArrayList<>(normalIndices)); // ArrayList
        }

        return triangle;
    }
}