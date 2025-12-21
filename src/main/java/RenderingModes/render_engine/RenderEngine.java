package RenderingModes.render_engine;

import Math.matrix.Matrix4x4;
import Math.vector.Vector3D;
import Interface.model.Model;
import RenderingModes.core.CameraFacade;
import RenderingModes.core.IRenderableModel;
import RenderingModes.core.ModelAdapter;
import RenderingModes.core.RenderContext;
import RenderingModes.data.Triangle;
import RenderingModes.data.Color;
import RenderingModes.rasterization.TriangleRasterizer;
import RenderingModes.shaders.SolidColorShader;
import Math.cam.Camera;

import java.util.List;

public class RenderEngine {
    private final RenderContext context;
    private final TriangleRasterizer rasterizer;
    private CameraFacade camera;

    public RenderEngine(int width, int height) {
        this.context = new RenderContext(width, height);
        this.rasterizer = new TriangleRasterizer();
        this.context.setActiveShader(new SolidColorShader(Color.GRAY));

        // Создаем камеру по умолчанию через фасад
        this.camera = new CameraFacade(
                new Vector3D(0, 0, 5),    // position
                new Vector3D(0, 0, 0),    // target
                60.0f,                    // fov
                (float)width / height,    // aspect ratio
                0.1f,                     // near
                100.0f                    // far
        );
    }

    /**
     * Установить камеру из Math.cam.Camera
     */
    public void setCamera(Camera mathCamera) {
        if (mathCamera != null) {
            this.camera = new CameraFacade(mathCamera);
        }
    }

    /**
     * Установить камеру через фасад
     */
    public void setCamera(CameraFacade camera) {
        this.camera = camera;
    }

    /**
     * Получить текущую камеру
     */
    public CameraFacade getCamera() {
        return camera;
    }

    public void renderModel(Model model) {
        if (model == null) {
            renderTestScene();
            return;
        }

        // 1. Подготавливаем модель
        ModelAdapter adapter = new ModelAdapter(model);
        List<Triangle> triangles = adapter.getTriangles();

        System.out.println("Rendering model with " + triangles.size() + " triangles");

        // 2. Получаем матрицы из камеры (через фасад)
        Matrix4x4 viewMatrix = camera.getViewMatrix();
        Matrix4x4 projectionMatrix = camera.getProjectionMatrix();

        System.out.println("Camera position: " + camera.getPosition());
        System.out.println("Camera target: " + camera.getTarget());
        System.out.println("View matrix: " + (viewMatrix != null ? "OK" : "NULL"));
        System.out.println("Projection matrix: " + (projectionMatrix != null ? "OK" : "NULL"));

        // 3. Очищаем контекст
        context.clear();

        // 4. Рендерим каждый треугольник
        int renderedCount = 0;
        for (Triangle triangle : triangles) {
            if (renderedCount >= 50) break; // Ограничим для теста

            // Трансформируем треугольник с помощью матриц
            Triangle transformed = transformTriangle(triangle, viewMatrix, projectionMatrix);
            rasterizer.rasterize(transformed, context, context.getActiveShader());
            renderedCount++;
        }

        System.out.println("Rendered " + renderedCount + " triangles");
    }

    private Triangle transformTriangle(Triangle triangle, Matrix4x4 viewMatrix, Matrix4x4 projectionMatrix) {
        // Здесь будет трансформация вершин
        // Пока возвращаем как есть
        return triangle;
    }

    public void renderTestScene() {
        // Создаем тестовую модель
        System.out.println("Rendering test scene");

        // Тестовый код...
    }

    public RenderContext getContext() {
        return context;
    }
}