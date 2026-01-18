package Math.cam;

import Math.vector.Vector3D;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseButton;

public class CameraController {
    private float previousMouseX;
    private float previousMouseY;
    private boolean rotatingActive;
    private boolean movingActive;

    private final Camera camera;
    private Runnable onCameraChanged; // Callback для обновления сцены

    private static final float ROTATION_SENSITIVITY = 0.3f;
    private static final float MOVEMENT_SENSITIVITY = 0.005f;
    private static final float ZOOM_SENSITIVITY = 0.01f;

    public CameraController(Camera camera, Canvas canvas) {
        this.camera = camera;
        setupControlHandlers(canvas);
    }

    /**
     * Устанавливает callback, который вызывается при изменении камеры
     */
    public void setOnCameraChanged(Runnable callback) {
        this.onCameraChanged = callback;
    }

    private void setupControlHandlers(Canvas canvas) {
        canvas.setOnMousePressed(event -> {
            previousMouseX = (float) event.getX();
            previousMouseY = (float) event.getY();

            if (event.getButton() == MouseButton.PRIMARY) rotatingActive = true;
            if (event.getButton() == MouseButton.MIDDLE) movingActive = true;
        });

        canvas.setOnMouseReleased(event -> {
            rotatingActive = false;
            movingActive = false;
        });

        canvas.setOnMouseDragged(event -> {
            float currentX = (float) event.getX();
            float currentY = (float) event.getY();
            float dx = currentX - previousMouseX;
            float dy = currentY - previousMouseY;

            if (rotatingActive) {
                handleRotation(dx, -dy);
                notifyCameraChanged(); // Уведомляем об изменении
            }
            if (movingActive) {
                handleMovement(-dx, dy);
                notifyCameraChanged(); // Уведомляем об изменении
            }

            previousMouseX = currentX;
            previousMouseY = currentY;
        });

        canvas.setOnScroll(event -> {
            handleZoom((float) event.getDeltaY());
            notifyCameraChanged(); // Уведомляем об изменении
        });
    }

    /**
     * Вызывает callback для обновления сцены
     */
    private void notifyCameraChanged() {
        if (onCameraChanged != null) {
            onCameraChanged.run();
        }
    }

    private void handleRotation(float dx, float dy) {
        Vector3D pos = camera.getPosition();
        Vector3D target = camera.getTarget();
        Vector3D viewVec = pos.subtract(target);
        float radius = viewVec.length();

        viewVec = rotateVectorAroundAxis(viewVec, new Vector3D(0, 1, 0), -dx * ROTATION_SENSITIVITY);

        Vector3D right = new Vector3D(0, 1, 0).cross(viewVec).normalize();
        viewVec = rotateVectorAroundAxis(viewVec, right, dy * ROTATION_SENSITIVITY);

        camera.setPosition(target.add(viewVec.normalize().multiply(radius)));
    }

    private void handleMovement(float dx, float dy) {
        Vector3D pos = camera.getPosition();
        Vector3D target = camera.getTarget();

        Vector3D look = target.subtract(pos).normalize();
        Vector3D right = new Vector3D(0, 1, 0).cross(look).normalize();
        Vector3D up = look.cross(right).normalize();

        float factor = pos.subtract(target).length() * MOVEMENT_SENSITIVITY;

        Vector3D offset = right.multiply(-dx * factor).add(up.multiply(dy * factor));

        camera.setPosition(pos.add(offset));
        camera.setTarget(target.add(offset));
    }

    private void handleZoom(float delta) {
        Vector3D pos = camera.getPosition();
        Vector3D target = camera.getTarget();
        Vector3D dir = pos.subtract(target);

        float newDist = Math.max(0.1f, dir.length() - delta * ZOOM_SENSITIVITY);
        camera.setPosition(target.add(dir.normalize().multiply(newDist)));
    }

    private Vector3D rotateVectorAroundAxis(Vector3D vector, Vector3D axis, float angleDeg) {
        float rad = (float) Math.toRadians(angleDeg);
        float cos = (float) Math.cos(rad);
        float sin = (float) Math.sin(rad);

        Vector3D a = axis.normalize();
        return vector.multiply(cos)
                .add(a.cross(vector).multiply(sin))
                .add(a.multiply(a.dot(vector) * (1 - cos)));
    }
}