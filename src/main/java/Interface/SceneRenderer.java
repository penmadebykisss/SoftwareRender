package Interface;

import Interface.model.Model;
import Interface.model.ModelManager;
import Math.cam.Camera;
import Math.matrix.Matrix4x4;
import RenderingModes.RenderingModes;
import RenderingModes.RenderEngine;
import RenderingModes.Lighting;
import RenderingModes.Texture;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class SceneRenderer {
    private Canvas canvas;
    private GraphicsContext gc;

    private boolean drawWireframe = true;
    private boolean drawFilled = false;
    private boolean useTextureMapping = false; // По умолчанию выключено
    private boolean useLighting = false;       // По умолчанию выключено

    private Color wireframeColor = Color.WHITE;
    private Color fillColor = Color.GRAY;
    private Color backgroundColor = Color.rgb(43, 43, 43);

    private DeletionModeHandler deletionModeHandler;

    public SceneRenderer(Canvas canvas) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
        this.deletionModeHandler = new DeletionModeHandler();
    }

    public void clear() {
        gc.setFill(backgroundColor);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    public void renderScene(ModelManager modelManager, CameraManager cameraManager) {
        clear();

        if (modelManager.isEmpty()) {
            drawNoModelMessage();
            return;
        }

        CameraManager.CameraEntry cameraEntry = cameraManager.getActiveCamera();
        if (cameraEntry == null) return;

        Camera camera = cameraEntry.getCamera();
        int width = (int) canvas.getWidth();
        int height = (int) canvas.getHeight();

        // Общее освещение сцены
        Lighting sceneLighting = new Lighting(
                camera.getPosition(),
                camera.getTarget(),
                0.3f,
                0.7f
        );

        Matrix4x4 viewProjection = camera.getProjectionMatrix().multiply(camera.getViewMatrix());
        deletionModeHandler.updateProjection(width / 2.0, height / 2.0, viewProjection);

        for (ModelManager.ModelEntry entry : modelManager.getAllModels()) {
            Model model = entry.getModel();

            RenderingModes modes = new RenderingModes();
            modes.setDrawWireframe(this.drawWireframe);
            modes.setDrawFilled(this.drawFilled);

            // ТЕПЕРЬ СВЕТ И ТЕКСТУРА ЗАВИСЯТ ОТ НАШИХ ПЕРЕМЕННЫХ
            modes.setUseLighting(this.useLighting);

            Texture texture = entry.getTexture();
            modes.setUseTexture(this.useTextureMapping && texture != null);

            // Передаем свет, только если он включен
            Lighting activeLighting = this.useLighting ? sceneLighting : null;

            Matrix4x4 modelMatrix = Matrix4x4.identity();

            RenderEngine.render(
                    gc, camera, model, width, height,
                    texture, activeLighting, fillColor, modes, modelMatrix
            );

            if (deletionModeHandler.isActive()) {
                deletionModeHandler.renderSelection(gc, model);
            }
        }
    }

    // --- Сеттеры для связи с MainWindow ---
    public void setDrawWireframe(boolean drawWireframe) { this.drawWireframe = drawWireframe; }
    public void setDrawFilled(boolean drawFilled) { this.drawFilled = drawFilled; }

    public void setUseTextureMapping(boolean useTextureMapping) {
        this.useTextureMapping = useTextureMapping;
    }

    public void setUseLighting(boolean useLighting) {
        this.useLighting = useLighting;
    }

    public void setFillColor(Color color) { this.fillColor = color; }
    public void setBackgroundColor(Color color) { this.backgroundColor = color; }
    public DeletionModeHandler getDeletionModeHandler() { return deletionModeHandler; }

    // Геттеры для того, чтобы MainWindow мог узнать текущее состояние режимов
    public boolean isDrawWireframe() {
        return drawWireframe;
    }

    public boolean isDrawFilled() {
        return drawFilled;
    }

    public boolean isUseTextureMapping() {
        return useTextureMapping;
    }

    public boolean isUseLighting() {
        return useLighting;
    }

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
}