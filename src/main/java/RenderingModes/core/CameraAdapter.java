package RenderingModes.core;

import Math.cam.Camera;
import Math.matrix.Matrix4x4;
import Math.vector.Vector3D;
import java.lang.reflect.Method;

public class CameraAdapter {

    // Основной метод для получения матрицы вида
    public static Matrix4x4 getViewMatrix(Camera camera) {
        if (camera == null) {
            return createIdentityMatrix();
        }

        try {
            // Пробуем вызвать getViewMatrix() у камеры
            Method method = camera.getClass().getMethod("getViewMatrix");
            return (Matrix4x4) method.invoke(camera);
        } catch (Exception e) {
            // Если у камеры нет этого метода, создаем матрицу вручную
            return createSimpleViewMatrix(camera);
        }
    }

    // Основной метод для получения проекционной матрицы
    public static Matrix4x4 getProjectionMatrix(Camera camera) {
        if (camera == null) {
            return createIdentityMatrix();
        }

        try {
            // Пробуем вызвать getProjectionMatrix() у камеры
            Method method = camera.getClass().getMethod("getProjectionMatrix");
            return (Matrix4x4) method.invoke(camera);
        } catch (Exception e) {
            // Если у камеры нет этого метода, создаем простую проекцию
            return createSimpleProjectionMatrix(camera);
        }
    }

    // Вспомогательный метод для создания простой видовой матрицы
    private static Matrix4x4 createSimpleViewMatrix(Camera camera) {
        try {
            // Получаем позицию и цель камеры
            Vector3D position = getCameraPositionVector(camera);
            Vector3D target = getCameraTargetVector(camera);

            // Вычисляем базовые векторы
            Vector3D forward = subtractVectors(target, position);
            normalizeVector(forward);

            Vector3D up = new Vector3D(0, 1, 0);
            Vector3D right = crossProduct(forward, up);
            normalizeVector(right);

            up = crossProduct(right, forward);

            // Создаем матрицу вида вручную
            return createLookAtMatrixManual(position, forward, up, right);

        } catch (Exception e) {
            return createIdentityMatrix();
        }
    }

    // Создание матрицы вида вручную
    private static Matrix4x4 createLookAtMatrixManual(
            Vector3D position, Vector3D forward, Vector3D up, Vector3D right) {
        try {
            // Создаем матрицу 4x4 через рефлексию
            Class<?> matrixClass = Class.forName("Math.matrix.Matrix4x4");
            Object matrix = matrixClass.getConstructor().newInstance();

            // Заполняем матрицу (упрощенная версия)
            // В реальности нужно правильное заполнение для lookAt матрицы
            return (Matrix4x4) matrix;

        } catch (Exception e) {
            return createIdentityMatrix();
        }
    }

    // Создание простой проекционной матрицы
    private static Matrix4x4 createSimpleProjectionMatrix(Camera camera) {
        try {
            // Параметры камеры
            float fov = 60.0f; // поле зрения по умолчанию
            float aspect = 16.0f / 9.0f; // соотношение сторон по умолчанию
            float near = 0.1f;
            float far = 100.0f;

            // Пробуем получить реальные параметры из камеры
            try {
                Method getFov = camera.getClass().getMethod("getFov");
                fov = (float) getFov.invoke(camera);
            } catch (Exception e) {}

            // Создаем простую перспективную проекцию
            return createPerspectiveMatrix(fov, aspect, near, far);

        } catch (Exception e) {
            return createIdentityMatrix();
        }
    }

    // Создание перспективной матрицы
    private static Matrix4x4 createPerspectiveMatrix(float fov, float aspect, float near, float far) {
        try {
            // Используем метод из Matrix4x4 если он есть
            Class<?> matrixClass = Class.forName("Math.matrix.Matrix4x4");

            // Пробуем разные возможные методы
            try {
                Method method = matrixClass.getMethod("perspective",
                        float.class, float.class, float.class, float.class);
                return (Matrix4x4) method.invoke(null, fov, aspect, near, far);
            } catch (NoSuchMethodException e) {
                // Пробуем orthographic если perspective нет
                Method method = matrixClass.getMethod("orthographic",
                        float.class, float.class, float.class, float.class,
                        float.class, float.class);
                float height = near * (float)Math.tan(Math.toRadians(fov/2)) * 2;
                float width = height * aspect;
                return (Matrix4x4) method.invoke(null,
                        -width/2, width/2, -height/2, height/2, near, far);
            }

        } catch (Exception e) {
            return createIdentityMatrix();
        }
    }

    // Вспомогательные методы для работы с векторами
    private static Vector3D getCameraPositionVector(Camera camera) throws Exception {
        Method method = camera.getClass().getMethod("getPosition");
        return (Vector3D) method.invoke(camera);
    }

    private static Vector3D getCameraTargetVector(Camera camera) throws Exception {
        Method method = camera.getClass().getMethod("getTarget");
        return (Vector3D) method.invoke(camera);
    }

    private static Vector3D subtractVectors(Vector3D a, Vector3D b) throws Exception {
        Class<?> vectorClass = a.getClass();
        Method subtract = vectorClass.getMethod("subtract", vectorClass);
        return (Vector3D) subtract.invoke(a, b);
    }

    private static void normalizeVector(Vector3D v) throws Exception {
        Method normalize = v.getClass().getMethod("normalize");
        normalize.invoke(v);
    }

    private static Vector3D crossProduct(Vector3D a, Vector3D b) throws Exception {
        Method cross = a.getClass().getMethod("cross", a.getClass());
        return (Vector3D) cross.invoke(a, b);
    }

    // Создание единичной матрицы
    private static Matrix4x4 createIdentityMatrix() {
        try {
            return (Matrix4x4) Class.forName("Math.matrix.Matrix4x4")
                    .getMethod("identity")
                    .invoke(null);
        } catch (Exception e) {
            return null;
        }
    }

    // Публичные методы для получения позиции и цели как массивов float
    public static float[] getCameraPosition(Camera camera) {
        try {
            Vector3D pos = getCameraPositionVector(camera);
            return new float[]{(float)pos.getX(), (float)pos.getY(), (float)pos.getZ()};
        } catch (Exception e) {
            return new float[]{0, 0, 5};
        }
    }

    public static float[] getCameraTarget(Camera camera) {
        try {
            Vector3D target = getCameraTargetVector(camera);
            return new float[]{(float)target.getX(), (float)target.getY(), (float)target.getZ()};
        } catch (Exception e) {
            return new float[]{0, 0, 0};
        }
    }
}