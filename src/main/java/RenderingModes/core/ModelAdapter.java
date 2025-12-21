package RenderingModes.core;

import Interface.model.Model;
import Interface.model.Polygon;
import Interface.math.Vector3f;
import RenderingModes.data.Vertex;
import RenderingModes.data.Triangle;
import RenderingModes.data.Color;
import RenderingModes.triangulation.TriangulationFacade;

import java.util.ArrayList;
import java.util.List;

public class ModelAdapter implements IRenderableModel {
    private final Model originalModel;
    private final List<Triangle> triangles;
    private boolean normalsCalculated = false;

    // Основной конструктор для Interface.model.Model
    public ModelAdapter(Model model) {
        this.originalModel = model;
        this.triangles = new ArrayList<>();

        if (model != null) {
            loadModel(model);
        } else {
            createTestCube();
        }
    }

    // Конструктор для совместимости (принимает Object)
    public ModelAdapter(Object model) {
        this((Model) model);
    }

    private void loadModel(Model model) {
        System.out.println("Loading model from Interface.model.Model");

        // 1. Триангулируем модель
        Model triangulated = TriangulationFacade.ensureTriangulated(model);

        // 2. Конвертируем полигоны в треугольники
        for (Polygon polygon : triangulated.getPolygons()) {
            List<Integer> vertexIndices = polygon.getVertexIndices();
            if (vertexIndices.size() != 3) {
                System.err.println("Warning: Non-triangle polygon found with " + vertexIndices.size() + " vertices");
                continue;
            }

            // Получаем вершины
            Vector3f v1 = triangulated.getVertices().get(vertexIndices.get(0));
            Vector3f v2 = triangulated.getVertices().get(vertexIndices.get(1));
            Vector3f v3 = triangulated.getVertices().get(vertexIndices.get(2));

            // Создаем наши Vertex
            Vertex vertex1 = new Vertex(v1.getX(), v1.getY(), v1.getZ());
            Vertex vertex2 = new Vertex(v2.getX(), v2.getY(), v2.getZ());
            Vertex vertex3 = new Vertex(v3.getX(), v3.getY(), v3.getZ());

            triangles.add(new Triangle(vertex1, vertex2, vertex3));
        }

        System.out.println("Model loaded: " + triangles.size() + " triangles");
    }

    private void createTestCube() {
        System.out.println("Creating test cube");

        // Вершины куба с цветами для визуализации
        Vertex[] vertices = {
                new Vertex(-1, -1, -1, 0, 0, 0, 0, -1, Color.RED),      // 0
                new Vertex(1, -1, -1, 1, 0, 0, 0, -1, Color.GREEN),     // 1
                new Vertex(1, 1, -1, 1, 1, 0, 0, -1, Color.BLUE),       // 2
                new Vertex(-1, 1, -1, 0, 1, 0, 0, -1, Color.YELLOW),    // 3
                new Vertex(-1, -1, 1, 0, 0, 0, 0, 1, Color.CYAN),       // 4
                new Vertex(1, -1, 1, 1, 0, 0, 0, 1, Color.MAGENTA),     // 5
                new Vertex(1, 1, 1, 1, 1, 0, 0, 1, Color.WHITE),        // 6
                new Vertex(-1, 1, 1, 0, 1, 0, 0, 1, Color.GRAY)         // 7
        };

        // Грани куба (12 треугольников)
        int[][] faces = {
                {0, 1, 2}, {0, 2, 3}, // задняя
                {4, 5, 6}, {4, 6, 7}, // передняя
                {0, 1, 5}, {0, 5, 4}, // нижняя
                {2, 3, 7}, {2, 7, 6}, // верхняя
                {0, 3, 7}, {0, 7, 4}, // левая
                {1, 2, 6}, {1, 6, 5}  // правая
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

    @Override
    public void recalculateNormals() {
        System.out.println("Recalculating normals for " + triangles.size() + " triangles");

        // TODO: Реализовать вычисление нормалей
        // 1. Вычислить нормали граней
        // 2. Сгладить нормали вершин

        normalsCalculated = true;
    }

    @Override
    public boolean hasValidNormals() {
        return normalsCalculated;
    }

    // Геттер для оригинальной модели
    public Model getOriginalModel() {
        return originalModel;
    }
}