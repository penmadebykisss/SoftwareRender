package Math.matrix;

import Math.vector.Vector3D;

public class CustomMatrix extends AbstractMatrix<CustomMatrix, Vector3D> {

    public CustomMatrix(float[][] data) {
        super(data, 3, 3);
    }

    @Override
    protected CustomMatrix createNew(float[][] data) {
        return new CustomMatrix(data);
    }

    @Override
    public Vector3D multiply(Vector3D vector) {
        float x = data[0][0] * vector.getX() + data[0][1] * vector.getY() + data[0][2] * vector.getZ();
        float y = data[1][0] * vector.getX() + data[1][1] * vector.getY() + data[1][2] * vector.getZ();
        float z = data[2][0] * vector.getX() + data[2][1] * vector.getY() + data[2][2] * vector.getZ();
        return new Vector3D(x, y, z);
    }

    @Override
    public float determinant() {
        // Базовая реализация определителя 3x3
        float a = data[0][0], b = data[0][1], c = data[0][2];
        float d = data[1][0], e = data[1][1], f = data[1][2];
        float g = data[2][0], h = data[2][1], i = data[2][2];

        return a * (e * i - f * h) - b * (d * i - f * g) + c * (d * h - e * g);
    }

    @Override
    public CustomMatrix inverse() {
        float det = determinant();
        if (Math.abs(det) < 1e-12f) {
            throw new ArithmeticException("Matrix is singular, cannot invert");
        }

        float a = data[0][0], b = data[0][1], c = data[0][2];
        float d = data[1][0], e = data[1][1], f = data[1][2];
        float g = data[2][0], h = data[2][1], i = data[2][2];

        float invDet = 1.0f / det;
        float[][] result = {
                {(e * i - f * h) * invDet, (c * h - b * i) * invDet, (b * f - c * e) * invDet},
                {(f * g - d * i) * invDet, (a * i - c * g) * invDet, (c * d - a * f) * invDet},
                {(d * h - e * g) * invDet, (b * g - a * h) * invDet, (a * e - b * d) * invDet}
        };

        return new CustomMatrix(result);
    }

    @Override
    public Vector3D solveLinearSystem(Vector3D vector) {
        CustomMatrix inverse = inverse();
        return inverse.multiply(vector);
    }

    public CustomMatrix customOperation() {
        float[][] result = new float[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                result[i][j] = data[i][j] * 2; // Пример операции
            }
        }
        return new CustomMatrix(result);
    }
}