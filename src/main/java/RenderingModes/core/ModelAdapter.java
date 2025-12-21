package RenderingModes.core;

import RenderingModes.data.*;
import java.util.ArrayList;
import java.util.List;

public class ModelAdapter implements IRenderableModel {
    private Object externalModel; // Будет Interface.model.Model
    private List<Triangle> triangles;

    public ModelAdapter(Object model) {
        this.externalModel = model;
        this.triangles = convertModelToTriangles(model);
    }

    private List<Triangle> convertModelToTriangles(Object model) {
        List<Triangle> result = new ArrayList<>();

        if (model == null) {
            // Возвращаем тестовый куб если модель не передана
            return createTestCube();
        }

        try {
            // Пробуем получить полигоны из модели через рефлексию
            java.lang.reflect.Method getPolygonsMethod = model.getClass().getMethod("getPolygons");
            Object polygons = getPolygonsMethod.invoke(model);

            if (polygons instanceof java.util.List) {
                java.util.List<?> polyList = (java.util.List<?>) polygons;

                // Для каждого полигона создаем треугольники
                for (Object polygonObj : polyList) {
                    // Здесь будет вызов TriangulationFacade
                    // Пока просто создаем тестовые треугольники
                    result.addAll(createTestTriangle());
                }
            }
        } catch (Exception e) {
            // Если не получается, создаем тестовую геометрию
            System.err.println("Warning: Could not convert model, using test geometry");
            result.addAll(createTestCube());
        }

        return result;
    }

    private List<Triangle> createTestCube() {
        List<Triangle> cube = new ArrayList<>();

        // Используем только существующие цвета из Color.java
        // Создаем цвета явно, если констант нет
        Color red = new Color(1, 0, 0);
        Color green = new Color(0, 1, 0);
        Color blue = new Color(0, 0, 1);
        Color yellow = new Color(1, 1, 0);
        Color cyan = new Color(0, 1, 1);    // Создаем явно
        Color magenta = new Color(1, 0, 1); // Создаем явно
        Color white = new Color(1, 1, 1);
        Color gray = new Color(0.5f, 0.5f, 0.5f);

        // 8 вершин куба
        Vertex[] vertices = {
                new Vertex(-1, -1, -1, 0, 0, 0, 0, -1, red),
                new Vertex(1, -1, -1, 1, 0, 0, 0, -1, green),
                new Vertex(1, 1, -1, 1, 1, 0, 0, -1, blue),
                new Vertex(-1, 1, -1, 0, 1, 0, 0, -1, yellow),
                new Vertex(-1, -1, 1, 0, 0, 0, 0, 1, cyan),
                new Vertex(1, -1, 1, 1, 0, 0, 0, 1, magenta),
                new Vertex(1, 1, 1, 1, 1, 0, 0, 1, white),
                new Vertex(-1, 1, 1, 0, 1, 0, 0, 1, gray)
        };

        // 12 треугольников куба
        int[][] cubeFaces = {
                {0, 1, 2}, {0, 2, 3}, // задняя
                {4, 5, 6}, {4, 6, 7}, // передняя
                {0, 1, 5}, {0, 5, 4}, // нижняя
                {2, 3, 7}, {2, 7, 6}, // верхняя
                {0, 3, 7}, {0, 7, 4}, // левая
                {1, 2, 6}, {1, 6, 5}  // правая
        };

        for (int[] face : cubeFaces) {
            cube.add(new Triangle(vertices[face[0]], vertices[face[1]], vertices[face[2]]));
        }

        return cube;
    }

    private List<Triangle> createTestTriangle() {
        List<Triangle> singleTriangle = new ArrayList<>();
        singleTriangle.add(new Triangle(
                new Vertex(-0.5f, -0.5f, 0, 0, 0, 0, 0, 1, Color.RED),
                new Vertex(0.5f, -0.5f, 0, 1, 0, 0, 0, 1, Color.GREEN),
                new Vertex(0, 0.5f, 0, 0.5f, 1, 0, 0, 1, Color.BLUE)
        ));
        return singleTriangle;
    }

    @Override
    public List<Triangle> getTriangles() {
        return triangles;
    }

    @Override
    public int getTriangleCount() {
        return triangles.size();
    }

    public Object getExternalModel() {
        return externalModel;
    }
}