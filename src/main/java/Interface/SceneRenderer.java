package Interface;

import Interface.model.Model;
import Interface.model.ModelManager;
import Interface.model.Polygon;
import Math.cam.Camera;
import Math.vector.Vector3D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.List;

/**
 * Класс для отрисовки 3D моделей на Canvas
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

        // Отрисовываем все модели
        for (ModelManager.ModelEntry modelEntry : modelManager.getAllModels()) {
            renderModel(modelEntry.getModel(), camera);
        }
    }

    /**
     * Отрисовывает одну модель
     */
    private void renderModel(Model model, Camera camera) {
        if (model.getVertices().isEmpty()) {
            return;
        }

        // Простая ортографическая проекция для начала
        // Позже можно добавить перспективную проекцию

        double centerX = canvas.getWidth() / 2;
        double centerY = canvas.getHeight() / 2;
        double scale = 50; // Масштаб для отображения

        // Отрисовываем полигоны
        for (Polygon polygon : model.getPolygons()) {
            List<Integer> vertexIndices = polygon.getVertexIndices();

            if (vertexIndices.size() < 3) {
                continue;
            }

            // Проецируем вершины полигона на экран
            double[] xPoints = new double[vertexIndices.size()];
            double[] yPoints = new double[vertexIndices.size()];

            for (int i = 0; i < vertexIndices.size(); i++) {
                int vertexIndex = vertexIndices.get(i);
                if (vertexIndex >= 0 && vertexIndex < model.getVertices().size()) {
                    Vector3D vertex = model.getVertices().get(vertexIndex);

                    // Простая ортографическая проекция
                    // X и Y координаты используются напрямую, Z игнорируется
                    xPoints[i] = centerX + vertex.getX() * scale;
                    yPoints[i] = centerY - vertex.getY() * scale; // Инвертируем Y для правильной ориентации
                }
            }

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

        // Если нет полигонов, рисуем точки вершин
        if (model.getPolygons().isEmpty()) {
            gc.setFill(Color.RED);
            for (Vector3D vertex : model.getVertices()) {
                double x = centerX + vertex.getX() * scale;
                double y = centerY - vertex.getY() * scale;
                gc.fillOval(x - 2, y - 2, 4, 4);
            }
        }
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