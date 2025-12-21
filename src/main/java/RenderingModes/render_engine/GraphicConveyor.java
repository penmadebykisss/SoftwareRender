package RenderingModes.render_engine;

import Math.vector.Vector3D;
import Math.matrix.Matrix4x4;
import java.lang.reflect.Field;

public class GraphicConveyor {

    public static Matrix4x4 rotateScaleTranslate() {
        float[][] matrix = new float[][]{
                {1, 0, 0, 0},
                {0, 1, 0, 0},
                {0, 0, 1, 0},
                {0, 0, 0, 1}
        };
        return new Matrix4x4(matrix);
    }

    public static Matrix4x4 lookAt(Vector3D eye, Vector3D target) {
        return lookAt(eye, target, new Vector3D(0, 1, 0));
    }

    public static Matrix4x4 lookAt(Vector3D eye, Vector3D target, Vector3D up) {
        Vector3D resultZ = target.subtract(eye).normalize();
        Vector3D resultX = up.cross(resultZ).normalize();
        Vector3D resultY = resultZ.cross(resultX);

        // Создаем матрицу в точности как в старой версии (row-major)
        float[][] matrix = new float[4][4];

        // Row 0
        matrix[0][0] = resultX.getX();
        matrix[0][1] = resultY.getX();
        matrix[0][2] = resultZ.getX();
        matrix[0][3] = 0;

        // Row 1
        matrix[1][0] = resultX.getY();
        matrix[1][1] = resultY.getY();
        matrix[1][2] = resultZ.getY();
        matrix[1][3] = 0;

        // Row 2
        matrix[2][0] = resultX.getZ();
        matrix[2][1] = resultY.getZ();
        matrix[2][2] = resultZ.getZ();
        matrix[2][3] = 0;

        // Row 3
        matrix[3][0] = -resultX.dot(eye);
        matrix[3][1] = -resultY.dot(eye);
        matrix[3][2] = -resultZ.dot(eye);
        matrix[3][3] = 1;

        return new Matrix4x4(matrix);
    }

    public static Matrix4x4 perspective(
            final float fov,
            final float aspectRatio,
            final float nearPlane,
            final float farPlane) {

        float tangentMinusOnDegree = (float) (1.0F / (Math.tan(Math.toRadians(fov) * 0.5F)));
        float[][] matrix = new float[4][4];

        // Инициализируем нулями
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                matrix[i][j] = 0;
            }
        }

        // Заполняем как в старой версии
        matrix[0][0] = tangentMinusOnDegree / aspectRatio;  // m00
        matrix[1][1] = tangentMinusOnDegree;                // m11
        matrix[2][2] = (farPlane + nearPlane) / (farPlane - nearPlane);  // m22
        matrix[2][3] = 1.0F;                                // m23
        matrix[3][2] = 2 * (nearPlane * farPlane) / (nearPlane - farPlane);  // m32

        return new Matrix4x4(matrix);
    }

    /**
     * Умножение матрицы 4x4 на вектор 3D (аналогично старой версии)
     */
    public static Vector3D multiplyMatrix4ByVector3(final Matrix4x4 matrix, final Vector3D vertex) {
        // Получаем доступ к данным матрицы через рефлексию
        float[][] m = getMatrixData(matrix);

        final float x = (vertex.getX() * m[0][0]) + (vertex.getY() * m[1][0]) +
                (vertex.getZ() * m[2][0]) + m[3][0];
        final float y = (vertex.getX() * m[0][1]) + (vertex.getY() * m[1][1]) +
                (vertex.getZ() * m[2][1]) + m[3][1];
        final float z = (vertex.getX() * m[0][2]) + (vertex.getY() * m[1][2]) +
                (vertex.getZ() * m[2][2]) + m[3][2];
        final float w = (vertex.getX() * m[0][3]) + (vertex.getY() * m[1][3]) +
                (vertex.getZ() * m[2][3]) + m[3][3];

        if (Math.abs(w) > 1e-6) {
            return new Vector3D(x / w, y / w, z / w);
        }
        return new Vector3D(x, y, z);
    }

    /**
     * Конвертация 3D вершины в 2D экранные координаты (аналогично старой версии)
     */
    public static float[] vertexToScreenPoint(final Vector3D vertex, final int width, final int height) {
        // Преобразование из NDC [-1, 1] в экранные координаты
        float screenX = vertex.getX() * width + width / 2.0f;
        float screenY = -vertex.getY() * height + height / 2.0f;  // Y инвертирован
        return new float[]{screenX, screenY};
    }

    /**
     * Вспомогательный метод для доступа к данным матрицы
     */
    private static float[][] getMatrixData(Matrix4x4 matrix) {
        try {
            Field dataField = matrix.getClass().getSuperclass().getDeclaredField("data");
            dataField.setAccessible(true);
            return (float[][]) dataField.get(matrix);
        } catch (Exception e) {
            System.err.println("Error accessing matrix data: " + e.getMessage());
            return new float[][]{
                    {1, 0, 0, 0},
                    {0, 1, 0, 0},
                    {0, 0, 1, 0},
                    {0, 0, 0, 1}
            };
        }
    }
}