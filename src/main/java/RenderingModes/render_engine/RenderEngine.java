package RenderingModes.render_engine;

import RenderingModes.core.RenderContext;
import RenderingModes.core.CameraAdapter;
import RenderingModes.core.ModelAdapter;
import RenderingModes.data.*;
import RenderingModes.rasterization.TriangleRasterizer;
import RenderingModes.shaders.SolidColorShader;
import Math.cam.Camera;
import Math.matrix.Matrix4x4;

import java.util.List;

public class RenderEngine {
    private RenderContext context;
    private TriangleRasterizer rasterizer;
    private Camera camera;
    private Matrix4x4 viewMatrix;
    private Matrix4x4 projectionMatrix;

    public RenderEngine(int width, int height) {
        this.context = new RenderContext(width, height);
        this.rasterizer = new TriangleRasterizer();
        this.context.setActiveShader(new SolidColorShader(Color.GRAY));

        // Инициализируем матрицы
        this.viewMatrix = Matrix4x4.identity();
        this.projectionMatrix = Matrix4x4.identity();
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
        updateMatrices();
    }

    private void updateMatrices() {
        if (camera != null) {
            // Получаем матрицы через адаптер
            this.viewMatrix = CameraAdapter.getViewMatrix(camera);
            this.projectionMatrix = CameraAdapter.getProjectionMatrix(camera);

            System.out.println("Camera matrices updated");
            System.out.println("View matrix: " + (viewMatrix != null ? "OK" : "NULL"));
            System.out.println("Projection matrix: " + (projectionMatrix != null ? "OK" : "NULL"));
        }
    }

    public void renderModel(Object model) {
        if (model == null) {
            renderTestScene();
            return;
        }

        context.clear();

        ModelAdapter modelAdapter = new ModelAdapter(model);
        List<Triangle> triangles = modelAdapter.getTriangles();

        System.out.println("Rendering model with " + triangles.size() + " triangles");
        System.out.println("Using view matrix: " + viewMatrix);

        // Применяем матрицы преобразования к треугольникам
        List<Triangle> transformedTriangles = transformTriangles(triangles);

        for (Triangle triangle : transformedTriangles) {
            rasterizer.rasterize(triangle, context, context.getActiveShader());
        }
    }

    private List<Triangle> transformTriangles(List<Triangle> triangles) {
        // Здесь будет преобразование вершин с помощью матриц
        // Пока возвращаем как есть
        return triangles;
    }

    public void renderTestScene() {
        context.clear();

        ModelAdapter testAdapter = new ModelAdapter(null);
        List<Triangle> triangles = testAdapter.getTriangles();

        System.out.println("Rendering test scene with " + triangles.size() + " triangles");

        int count = 0;
        for (Triangle triangle : triangles) {
            rasterizer.rasterize(triangle, context, context.getActiveShader());
            count++;
            if (count >= 3) break;
        }
    }

    // Геттеры для матриц (могут понадобиться)
    public Matrix4x4 getViewMatrix() {
        return viewMatrix;
    }

    public Matrix4x4 getProjectionMatrix() {
        return projectionMatrix;
    }

    public RenderContext getContext() {
        return context;
    }

    public void setShader(Object shader) {
        if (shader instanceof RenderingModes.shaders.Shader) {
            context.setActiveShader((RenderingModes.shaders.Shader) shader);
        }
    }

    // Метод для применения матрицы к вершине
    public float[] transformVertex(float[] vertex) {
        if (vertex == null || vertex.length < 3) {
            return new float[]{0, 0, 0, 1};
        }

        // Применяем view и projection матрицы
        // Пока заглушка
        return new float[]{vertex[0], vertex[1], vertex[2], 1.0f};
    }
}