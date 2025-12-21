package RenderingModes.core;

import Interface.math.Vector2f;
import Interface.model.Model;
import Interface.model.Polygon;
import Interface.math.Vector3f;
import RenderingModes.data.Vertex;
import RenderingModes.data.Triangle;
import RenderingModes.data.Color;
import RenderingModes.triangulation.TriangulationFacade;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class ModelAdapter implements IRenderableModel {
    private final Model originalModel;
    private final List<Triangle> triangles;
    private boolean normalsCalculated = false;

    public ModelAdapter(Model model) {
        this.originalModel = model;
        this.triangles = new ArrayList<>();

        if (model != null) {
            loadModel(model);
            // Автоматически пересчитываем нормали (требование задания)
            recalculateNormals();
        } else {
            createTestCube();
            recalculateNormals();
        }
    }

    private void loadModel(Model model) {
        System.out.println("Loading model from Interface.model.Model");

        // 1. Триангулируем модель
        Model triangulated = TriangulationFacade.ensureTriangulated(model);

        // 2. Получаем данные из модели
        List<Vector3f> vertices = triangulated.getVertices();
        ArrayList<Vector2f> textureVertices = triangulated.getTextureVertices(); // ArrayList<Vector2f>!
        List<Vector3f> normals = triangulated.getNormals();

        // 3. Конвертируем полигоны в треугольники
        for (Polygon polygon : triangulated.getPolygons()) {
            List<Integer> vertexIndices = polygon.getVertexIndices();
            if (vertexIndices.size() != 3) {
                System.err.println("Warning: Non-triangle polygon found with " + vertexIndices.size() + " vertices");
                continue;
            }

            // Для каждой вершины треугольника
            Vertex[] triangleVertices = new Vertex[3];
            for (int i = 0; i < 3; i++) {
                int vIdx = vertexIndices.get(i);
                Vector3f pos = vertices.get(vIdx);

                // Текстурные координаты (если есть)
                float u = 0, v = 0;
                List<Integer> texIndices = polygon.getTextureVertexIndices();
                if (texIndices != null && i < texIndices.size()) {
                    int tIdx = texIndices.get(i);
                    if (tIdx < textureVertices.size()) {
                        Vector2f tex = textureVertices.get(tIdx); // Vector2f!
                        u = tex.getX();
                        v = tex.getY();
                    }
                }

                // Нормали из файла (если есть)
                float nx = 0, ny = 0, nz = 0;
                List<Integer> normIndices = polygon.getNormalIndices();
                if (normIndices != null && i < normIndices.size()) {
                    int nIdx = normIndices.get(i);
                    if (nIdx < normals.size()) {
                        Vector3f norm = normals.get(nIdx);
                        nx = norm.getX();
                        ny = norm.getY();
                        nz = norm.getZ();
                    }
                }

                // Создаем вершину
                Vertex vertex = new Vertex(
                        pos.getX(), pos.getY(), pos.getZ(),
                        u, v,
                        nx, ny, nz,
                        Color.WHITE
                );

                triangleVertices[i] = vertex;
            }

            triangles.add(new Triangle(triangleVertices[0], triangleVertices[1], triangleVertices[2]));
        }

        System.out.println("Model loaded: " + triangles.size() + " triangles");
        System.out.println("Has UVs: " + (textureVertices.size() > 0));
        System.out.println("Has normals in file: " + (normals.size() > 0));
    }

    @Override
    public void recalculateNormals() {
        System.out.println("Recalculating normals for " + triangles.size() + " triangles");

        if (triangles.isEmpty()) {
            normalsCalculated = false;
            return;
        }

        try {
            // 1. Вычисляем сглаженные нормали
            Map<Integer, float[]> smoothedNormals = NormalCalculator.calculateSmoothNormals(triangles);

            // 2. Создаем новые треугольники с обновленными вершинами
            List<Triangle> updatedTriangles = new ArrayList<>();
            Map<Integer, Vertex> vertexCache = new HashMap<>();

            for (Triangle triangle : triangles) {
                Vertex[] oldVertices = triangle.getVertices();
                Vertex[] newVertices = new Vertex[3];

                for (int i = 0; i < 3; i++) {
                    Vertex oldVertex = oldVertices[i];
                    int vertexId = oldVertex.getId();

                    // Проверяем кэш
                    if (vertexCache.containsKey(vertexId)) {
                        newVertices[i] = vertexCache.get(vertexId);
                    } else {
                        float[] newNormal = smoothedNormals.get(vertexId);

                        Vertex newVertex;
                        if (newNormal != null &&
                                (Math.abs(newNormal[0]) > 0.001f ||
                                        Math.abs(newNormal[1]) > 0.001f ||
                                        Math.abs(newNormal[2]) > 0.001f)) {

                            // Создаем вершину с новой нормалью
                            newVertex = new Vertex(
                                    oldVertex.getX(), oldVertex.getY(), oldVertex.getZ(),
                                    oldVertex.getU(), oldVertex.getV(),
                                    newNormal[0], newNormal[1], newNormal[2],
                                    oldVertex.getColor()
                            );
                        } else {
                            // Используем существующую нормаль или face normal
                            newVertex = oldVertex;
                        }

                        vertexCache.put(vertexId, newVertex);
                        newVertices[i] = newVertex;
                    }
                }

                updatedTriangles.add(new Triangle(newVertices[0], newVertices[1], newVertices[2]));
            }

            // 3. Заменяем старые треугольники
            triangles.clear();
            triangles.addAll(updatedTriangles);

            normalsCalculated = true;
            System.out.println("Normals recalculated successfully");

        } catch (Exception e) {
            System.err.println("Error recalculating normals: " + e.getMessage());
            e.printStackTrace();
            normalsCalculated = false;
        }
    }

    @Override
    public boolean hasValidNormals() {
        return normalsCalculated;
    }

    public void forceRecalculateNormals() {
        normalsCalculated = false;
        recalculateNormals();
    }

    // Остальные методы без изменений
    private void createTestCube() {
        System.out.println("Creating test cube");

        // Вершины куба с цветами для визуализации
        Vertex[] vertices = {
                new Vertex(-1, -1, -1, 0, 0, 0, 0, -1, Color.RED),
                new Vertex(1, -1, -1, 1, 0, 0, 0, -1, Color.GREEN),
                new Vertex(1, 1, -1, 1, 1, 0, 0, -1, Color.BLUE),
                new Vertex(-1, 1, -1, 0, 1, 0, 0, -1, Color.YELLOW),
                new Vertex(-1, -1, 1, 0, 0, 0, 0, 1, Color.CYAN),
                new Vertex(1, -1, 1, 1, 0, 0, 0, 1, Color.MAGENTA),
                new Vertex(1, 1, 1, 1, 1, 0, 0, 1, Color.WHITE),
                new Vertex(-1, 1, 1, 0, 1, 0, 0, 1, Color.GRAY)
        };

        // Грани куба
        int[][] faces = {
                {0, 1, 2}, {0, 2, 3},
                {4, 5, 6}, {4, 6, 7},
                {0, 1, 5}, {0, 5, 4},
                {2, 3, 7}, {2, 7, 6},
                {0, 3, 7}, {0, 7, 4},
                {1, 2, 6}, {1, 6, 5}
        };

        for (int[] face : faces) {
            triangles.add(new Triangle(vertices[face[0]], vertices[face[1]], vertices[face[2]]));
        }

        System.out.println("Test cube created: " + triangles.size() + " triangles");
    }

    @Override
    public List<Triangle> getTriangles() {
        return triangles;
    }

    @Override
    public int getTriangleCount() {
        return triangles.size();
    }

    public Model getOriginalModel() {
        return originalModel;
    }
}