package Interface;

import Interface.model.Model;
import Interface.model.ModelManager;
import Interface.model.Polygon;
import Math.cam.Camera;
import Math.matrix.Matrix4x4;
import Math.vector.Vector3D;
import Math.vector.Vector4D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.List;

/**
 * Класс для отрисовки 3D моделей на Canvas с использованием камеры
 */
public class SceneRenderer {
    private Canvas canvas;
    private GraphicsContext gc;

    // Настройки отрисовки
    private boolean drawWireframe = true;
    private boolean drawFilled = false;
    private Color wireframeColor = Color.WHITE;
    private Color fillColor = Color.GRAY;
    private Color backgroundColor = Color.rgb(43, 43, 43);

    public SceneRenderer(Canvas canvas) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
    }

    /**
     * Очищает сцену
     */
    public void clear() {
        gc.setFill(backgroundColor);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    /**
     * Отрисовывает все модели
     */
    public void renderScene(ModelManager modelManager, CameraManager cameraManager) {
        clear();

        if (modelManager.isEmpty()) {
            drawNoModelMessage();
            return;
        }

        // Получаем активную камеру
        CameraManager.CameraEntry cameraEntry = cameraManager.getActiveCamera();
        if (cameraEntry == null) {
            return;
        }

        Camera camera = cameraEntry.getCamera();

        // Обновляем aspect ratio камеры
        float aspect = (float) canvas.getWidth() / (float) canvas.getHeight();
        camera.setAspectRatio(aspect);

        // Получаем комбинированную матрицу вид-проекция
        Matrix4x4 viewProjection = camera.getViewProjectionMatrix();

        // Отрисовываем все модели
        for (ModelManager.ModelEntry modelEntry : modelManager.getAllModels()) {
            renderModel(modelEntry.getModel(), viewProjection);
        }
    }

    /**
     * Отрисовывает одну модель с использованием матрицы вид-проекция
     */
    private void renderModel(Model model, Matrix4x4 viewProjection) {
        if (model.getVertices().isEmpty()) {
            return;
        }

        double centerX = canvas.getWidth() / 2;
        double centerY = canvas.getHeight() / 2;

        // Отрисовываем полигоны
        for (Polygon polygon : model.getPolygons()) {
            List<Integer> vertexIndices = polygon.getVertexIndices();

            if (vertexIndices.size() < 3) {
                continue;
            }

            // Проецируем вершины полигона на экран
            double[] xPoints = new double[vertexIndices.size()];
            double[] yPoints = new double[vertexIndices.size()];
            boolean allVisible = true;

            for (int i = 0; i < vertexIndices.size(); i++) {
                int vertexIndex = vertexIndices.get(i);
                if (vertexIndex >= 0 && vertexIndex < model.getVertices().size()) {
                    Vector3D vertex = model.getVertices().get(vertexIndex);

                    // Применяем матрицу вид-проекция
                    Vector3D projected = transformVertex(vertex, viewProjection);

                    // Проверяем, находится ли вершина в видимой области
                    // После перспективного деления координаты должны быть в диапазоне [-1, 1]
                    // Z должно быть в диапазоне [0, 1] для правильного отсечения
                    if (Math.abs(projected.getX()) > 1.5f ||
                            Math.abs(projected.getY()) > 1.5f ||
                            projected.getZ() < 0.0f ||
                            projected.getZ() > 1.0f) {
                        allVisible = false;
                        break;
                    }

                    // Преобразуем нормализованные координаты [-1, 1] в экранные
                    xPoints[i] = centerX + (projected.getX() * centerX);
                    yPoints[i] = centerY - (projected.getY() * centerY);
                }
            }

            // Рисуем полигон только если все вершины видимы
            if (allVisible) {
                // Рисуем заливку (если включено)
                if (drawFilled) {
                    gc.setFill(fillColor);
                    gc.fillPolygon(xPoints, yPoints, vertexIndices.size());
                }

                // Рисуем каркас (если включено)
                if (drawWireframe) {
                    gc.setStroke(wireframeColor);
                    gc.setLineWidth(1.0);
                    gc.strokePolygon(xPoints, yPoints, vertexIndices.size());
                }
            }
        }

        // Если нет полигонов, рисуем точки вершин
        if (model.getPolygons().isEmpty()) {
            gc.setFill(Color.RED);
            for (Vector3D vertex : model.getVertices()) {
                Vector3D projected = transformVertex(vertex, viewProjection);

                if (Math.abs(projected.getX()) <= 1.5f &&
                        Math.abs(projected.getY()) <= 1.5f &&
                        projected.getZ() >= 0.0f &&
                        projected.getZ() <= 1.0f) {
                    double x = centerX + (projected.getX() * centerX);
                    double y = centerY - (projected.getY() * centerY);
                    gc.fillOval(x - 2, y - 2, 4, 4);
                }
            }
        }
    }

    /**
     * Трансформирует вершину через матрицу вид-проекция с перспективным делением
     * Использует встроенный метод Matrix4x4.multiply(Vector3D)
     */
    private Vector3D transformVertex(Vector3D vertex, Matrix4x4 viewProjection) {
        // Matrix4x4.multiply(Vector3D) автоматически:
        // 1. Преобразует в однородные координаты (x, y, z, 1)
        // 2. Умножает на матрицу
        // 3. Выполняет перспективное деление через Vector4D.toVector3D()
        return viewProjection.multiply(vertex);
    }

    /**
     * Рисует сообщение об отсутствии модели
     */
    private void drawNoModelMessage() {
        gc.setFill(Color.rgb(136, 136, 136));
        gc.setFont(javafx.scene.text.Font.font("Arial", 18));
        String message = "Сцена 3D\n(Загрузите модель для отображения)";

        double x = canvas.getWidth() / 2 - 150;
        double y = canvas.getHeight() / 2;

        for (String line : message.split("\n")) {
            gc.fillText(line, x, y);
            y += 25;
        }
    }

    // Геттеры и сеттеры для настроек отрисовки

    public void setDrawWireframe(boolean drawWireframe) {
        this.drawWireframe = drawWireframe;
    }

    public void setDrawFilled(boolean drawFilled) {
        this.drawFilled = drawFilled;
    }

    public void setWireframeColor(Color color) {
        this.wireframeColor = color;
    }

    public void setFillColor(Color color) {
        this.fillColor = color;
    }

    public void setBackgroundColor(Color color) {
        this.backgroundColor = color;
    }

    public boolean isDrawWireframe() {
        return drawWireframe;
    }

    public boolean isDrawFilled() {
        return drawFilled;
    }
}