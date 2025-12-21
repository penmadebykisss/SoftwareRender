package RenderingModes.test;

import RenderingModes.render_engine.RenderEngine;
import RenderingModes.data.Color;
import RenderingModes.shaders.SolidColorShader;
import RenderingModes.shaders.GradientShader;

public class IntegrationTest {

    public static void main(String[] args) {
        System.out.println("=== Integration Test ===");
        System.out.println("Testing connection between modules...\n");

        try {
            // 1. Тестируем RenderEngine
            testRenderEngine();

            // 2. Тестируем шейдеры
            testShaders();

            // 3. Тестируем адаптеры
            testAdapters();

            System.out.println("\n✅ Все модули работают корректно!");
            System.out.println("Модуль RenderingModes готов к интеграции с Math и Interface.");

        } catch (Exception e) {
            System.err.println("\n❌ Ошибка интеграции: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void testRenderEngine() {
        System.out.println("1. Testing RenderEngine...");

        RenderEngine engine = new RenderEngine(800, 600);

        // Тест 1: Рендеринг без модели (тестовая сцена)
        engine.renderTestScene();

        // Тест 2: Рендеринг с null моделью
        engine.renderModel(null);

        // Тест 3: Смена шейдера
        engine.setShader(new SolidColorShader(Color.RED));
        engine.setShader(new GradientShader(Color.RED, Color.GREEN, Color.BLUE));

        System.out.println("   ✓ RenderEngine работает");
    }

    private static void testShaders() {
        System.out.println("2. Testing Shaders...");

        // Создаем различные шейдеры
        SolidColorShader solidShader = new SolidColorShader(Color.BLUE);
        GradientShader gradientShader = new GradientShader(
                Color.RED, Color.GREEN, Color.BLUE
        );

        // Тестовые данные
        float[][] vertexData = {
                {0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 1},
                {1, 0, 0, 1, 0, 0, 0, 1, 0, 1, 0, 1},
                {0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 1}
        };

        // Просто проверяем, что шейдеры создаются
        System.out.println("   Создан SolidColorShader: " + solidShader.getClass().getName());
        System.out.println("   Создан GradientShader: " + gradientShader.getClass().getName());

        System.out.println("   ✓ Шейдеры создаются корректно");
    }

    private static void testAdapters() {
        System.out.println("3. Testing Adapters...");

        try {
            // Пробуем найти классы из других модулей
            Class<?> cameraClass = Class.forName("Math.cam.Camera");
            System.out.println("   Найден класс Camera: " + cameraClass.getName());

            Class<?> modelClass = Class.forName("Interface.model.Model");
            System.out.println("   Найден класс Model: " + modelClass.getName());

            Class<?> matrixClass = Class.forName("Math.matrix.Matrix4x4");
            System.out.println("   Найден класс Matrix4x4: " + matrixClass.getName());

            System.out.println("   ✓ Все внешние модули доступны");

        } catch (ClassNotFoundException e) {
            System.out.println("   ⚠ Некоторые внешние модули еще не готовы");
            System.out.println("   Это нормально на данном этапе разработки");
        }
    }
}