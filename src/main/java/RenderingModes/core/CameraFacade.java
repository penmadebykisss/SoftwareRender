package RenderingModes.core;

import Math.cam.Camera;
import Math.matrix.Matrix4x4;
import Math.vector.Vector3D;

/**
 * Фасад для работы с камерой из Math.cam
 */
public class CameraFacade {
    private final Camera mathCamera;
    private Matrix4x4 cachedViewMatrix;
    private Matrix4x4 cachedProjectionMatrix;

    public CameraFacade(Camera mathCamera) {
        this.mathCamera = mathCamera;
        updateCache();
    }

    public CameraFacade(Vector3D position, Vector3D target, float fov,
                        float aspectRatio, float nearPlane, float farPlane) {
        this.mathCamera = new Camera(position, target, fov, aspectRatio, nearPlane, farPlane);
        updateCache();
    }

    /**
     * Обновляем кэш матриц
     */
    private void updateCache() {
        try {
            // Используем рефлексию для доступа к package-private методам
            java.lang.reflect.Method getViewMethod = Camera.class.getDeclaredMethod("getViewMatrix");
            getViewMethod.setAccessible(true);
            cachedViewMatrix = (Matrix4x4) getViewMethod.invoke(mathCamera);

            java.lang.reflect.Method getProjectionMethod = Camera.class.getDeclaredMethod("getProjectionMatrix");
            getProjectionMethod.setAccessible(true);
            cachedProjectionMatrix = (Matrix4x4) getProjectionMethod.invoke(mathCamera);
        } catch (Exception e) {
            System.err.println("Error accessing camera matrices: " + e.getMessage());
            // Создаем матрицы по умолчанию
            cachedViewMatrix = Matrix4x4.identity();
            cachedProjectionMatrix = Matrix4x4.identity();
        }
    }

    // Публичные методы для получения матриц
    public Matrix4x4 getViewMatrix() {
        return cachedViewMatrix;
    }

    public Matrix4x4 getProjectionMatrix() {
        return cachedProjectionMatrix;
    }

    // Делегируем публичные методы исходной камере
    public Vector3D getPosition() {
        return mathCamera.getPosition();
    }

    public Vector3D getTarget() {
        return mathCamera.getTarget();
    }

    public void setPosition(Vector3D position) {
        mathCamera.setPosition(position);
        updateCache();
    }

    public void setTarget(Vector3D target) {
        mathCamera.setTarget(target);
        updateCache();
    }

    public void setAspectRatio(float aspectRatio) {
        mathCamera.setAspectRatio(aspectRatio);
        updateCache();
    }

    public Camera getMathCamera() {
        return mathCamera;
    }
}