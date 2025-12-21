package RenderingModes.test;

import RenderingModes.core.CameraAdapter;
import RenderingModes.render_engine.RenderEngine;
import Math.cam.Camera;
import Math.vector.Vector3D;

public class SimpleCameraTest {

    public static void main(String[] args) {
        System.out.println("=== Camera Integration Test ===\n");

        try {
            // Пробуем создать тестовую камеру
            testCameraCreation();

            // Тестируем RenderEngine с камерой
            testRenderEngineWithCamera();

            System.out.println("\n✅ Camera integration successful!");

        } catch (Exception e) {
            System.err.println("\n❌ Camera integration error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void testCameraCreation() throws Exception {
        System.out.println("1. Testing Camera class...");

        // Создаем векторы для камеры
        Vector3D position = new Vector3D(0, 0, 5);
        Vector3D target = new Vector3D(0, 0, 0);
        Vector3D up = new Vector3D(0, 1, 0);

        // Параметры камеры
        float fov = 60.0f;
        float aspect = 16.0f / 9.0f;
        float near = 0.1f;
        float far = 100.0f;

        // Создаем камеру
        Camera camera = new Camera(position, target, fov, aspect, near, far);

        System.out.println("   Camera created: " + camera.getClass().getName());
        System.out.println("   Position: " + camera.getPosition());
        System.out.println("   Target: " + camera.getTarget());

        // Тестируем CameraAdapter
        float[] camPos = CameraAdapter.getCameraPosition(camera);
        System.out.println("   CameraAdapter position: [" +
                camPos[0] + ", " + camPos[1] + ", " + camPos[2] + "]");

        System.out.println("   ✓ Camera test passed");
    }

    private static void testRenderEngineWithCamera() throws Exception {
        System.out.println("2. Testing RenderEngine with Camera...");

        // Создаем RenderEngine
        RenderEngine engine = new RenderEngine(800, 600);

        // Создаем камеру
        Vector3D position = new Vector3D(0, 0, 5);
        Vector3D target = new Vector3D(0, 0, 0);
        Camera camera = new Camera(position, target, 60, 16.0f/9.0f, 0.1f, 100);

        // Устанавливаем камеру
        engine.setCamera(camera);

        // Рендерим тестовую сцену
        engine.renderTestScene();

        System.out.println("   View matrix: " +
                (engine.getViewMatrix() != null ? "OK" : "NULL"));
        System.out.println("   Projection matrix: " +
                (engine.getProjectionMatrix() != null ? "OK" : "NULL"));

        System.out.println("   ✓ RenderEngine with Camera test passed");
    }
}