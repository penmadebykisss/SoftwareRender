package RenderingModes.core;

import Math.matrix.Matrix4x4;
import Math.vector.Vector3D;
import Math.vector.Vector4D;

public class Matrix4x4Adapter {

    public static float[] multiply(Matrix4x4 matrix, float[] vector) {
        if (matrix == null || vector == null || vector.length < 3) {
            return new float[]{0, 0, 0, 1};
        }

        try {
            // Преобразуем float[] в Vector3D
            Vector3D vec3 = Vector3DAdapter.fromFloatArray(vector);
            if (vec3 == null) return new float[]{0, 0, 0, 1};

            // Создаем Vector4D из Vector3D
            Class<?> vector4DClass = Class.forName("Math.vector.Vector4D");
            Object vec4 = vector4DClass.getConstructor(
                            float.class, float.class, float.class, float.class)
                    .newInstance((float)vec3.getX(), (float)vec3.getY(),
                            (float)vec3.getZ(), 1.0f);

            // Умножаем матрицу на вектор (ищем правильный метод)
            java.lang.reflect.Method multiplyMethod = null;
            try {
                // Пробуем метод multiply(Vector4D)
                multiplyMethod = matrix.getClass().getMethod("multiply", vector4DClass);
            } catch (NoSuchMethodException e) {
                // Пробуем метод multiply(Vector3D)
                multiplyMethod = matrix.getClass().getMethod("multiply", Vector3D.class);
                vec4 = vec3; // Используем Vector3D вместо Vector4D
            }

            Vector4D result = (Vector4D) multiplyMethod.invoke(matrix, vec4);

            return new float[]{
                    (float)result.getX(),
                    (float)result.getY(),
                    (float)result.getZ(),
                    (float)result.getW()
            };
        } catch (Exception e) {
            // Если умножение не поддерживается, возвращаем исходный вектор
            if (vector.length == 3) {
                return new float[]{vector[0], vector[1], vector[2], 1.0f};
            }
            return vector;
        }
    }

    public static Matrix4x4 createTranslation(float x, float y, float z) {
        try {
            return (Matrix4x4) Class.forName("Math.matrix.Matrix4x4")
                    .getMethod("translation", float.class, float.class, float.class)
                    .invoke(null, x, y, z);
        } catch (Exception e) {
            return getIdentityMatrix();
        }
    }

    public static Matrix4x4 getIdentityMatrix() {
        try {
            return (Matrix4x4) Class.forName("Math.matrix.Matrix4x4")
                    .getMethod("identity")
                    .invoke(null);
        } catch (Exception e) {
            return null;
        }
    }
}