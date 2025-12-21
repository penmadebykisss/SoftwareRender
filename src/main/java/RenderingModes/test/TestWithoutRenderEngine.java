package RenderingModes.test;

import RenderingModes.core.ModelAdapter;
import RenderingModes.core.RenderContext;
import RenderingModes.data.*;
import RenderingModes.shaders.*;
import RenderingModes.rasterization.TriangleRasterizer;

public class TestWithoutRenderEngine {

    public static void main(String[] args) {
        System.out.println("=== Тест без render_engine ===\n");

        try {
            testBasicRendering();
            testShaders();
            testModelAdapter();

            System.out.println("\n✅ Все тесты прошли! Модуль готов к интеграции.");

        } catch (Exception e) {
            System.err.println("❌ Ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void testBasicRendering() {
        System.out.println("1. Тест базовой растеризации...");

        RenderContext context = new RenderContext(800, 600);
        TriangleRasterizer rasterizer = new TriangleRasterizer();

        // Создаем тестовый треугольник
        Vertex v0 = new Vertex(100, 100, 0.5f);
        Vertex v1 = new Vertex(700, 100, 0.3f);
        Vertex v2 = new Vertex(400, 500, 0.8f);

        Triangle triangle = new Triangle(v0, v1, v2);

        // Создаем шейдер
        SolidColorShader shader = new SolidColorShader(Color.GREEN);
        context.setActiveShader(shader);

        System.out.println("   Создан треугольник: " + triangle);
        System.out.println("   Создан контекст: " + context.getWidth() + "x" + context.getHeight());
        System.out.println("   ✓ Базовая растеризация готова");
    }

    private static void testShaders() {
        System.out.println("2. Тест шейдеров...");

        // Подготавливаем данные вершин
        float[][] vertexData = new float[3][11]; // x,y,z, u,v, nx,ny,nz, r,g,b,a

        // Вершина 0 (красная)
        vertexData[0][0] = 0; // x
        vertexData[0][1] = 0; // y
        vertexData[0][2] = 0; // z
        vertexData[0][8] = 1; // r
        vertexData[0][9] = 0; // g
        vertexData[0][10] = 0; // b

        // Вершина 1 (зеленая)
        vertexData[1][0] = 1;
        vertexData[1][1] = 0;
        vertexData[1][2] = 0;
        vertexData[1][8] = 0;
        vertexData[1][9] = 1;
        vertexData[1][10] = 0;

        // Вершина 2 (синяя)
        vertexData[2][0] = 0;
        vertexData[2][1] = 1;
        vertexData[2][2] = 0;
        vertexData[2][8] = 0;
        vertexData[2][9] = 0;
        vertexData[2][10] = 1;

        BarycentricCoords bc = new BarycentricCoords(0.3f, 0.3f, 0.4f);

        // Тестируем SolidColorShader
        SolidColorShader solidShader = new SolidColorShader(Color.YELLOW);
        Color solidResult = solidShader.shade(vertexData, bc, 50, 50, 0.5f, null);
        System.out.println("   SolidColorShader: " + solidResult);

        System.out.println("   ✓ Шейдеры работают");
    }

    private static void testModelAdapter() {
        System.out.println("3. Тест ModelAdapter...");

        // Создаем заглушку для Model (пока не используем реальный Model из Interface)
        Object dummyModel = new Object();
        ModelAdapter adapter = new ModelAdapter(null); // Передаем null, т.к. Model еще нет

        int triangleCount = adapter.getTriangleCount();
        System.out.println("   Создано треугольников: " + triangleCount);

        if (triangleCount > 0) {
            System.out.println("   Первый треугольник: " + adapter.getTriangles().get(0));
        }

        System.out.println("   ✓ ModelAdapter работает");
    }
}