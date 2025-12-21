package Math.matrix;

import Math.vector.Vector3D;

public final class Matrix3x3 extends AbstractMatrix<Matrix3x3, Vector3D> {

    public Matrix3x3(float[][] data) {
        super(data, 3, 3);
    }

    @Override
    protected Matrix3x3 createNew(float[][] data) {
        return new Matrix3x3(data);
    }

    public static Matrix3x3 identity() {
        return new Matrix3x3(new float[][]{
                {1, 0, 0},
                {0, 1, 0},
                {0, 0, 1}
        });
    }

    public static Matrix3x3 zero() {
        return new Matrix3x3(new float[3][3]);
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
        float a = data[0][0], b = data[0][1], c = data[0][2];
        float d = data[1][0], e = data[1][1], f = data[1][2];
        float g = data[2][0], h = data[2][1], i = data[2][2];

        return a * (e * i - f * h) - b * (d * i - f * g) + c * (d * h - e * g);
    }

    @Override
    public Matrix3x3 inverse() {
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

        return new Matrix3x3(result);
    }

    @Override
    public Vector3D solveLinearSystem(Vector3D vector) {
        return solveGauss(vector);
    }

    private Vector3D solveGauss(Vector3D b) {
        float[][] augmented = new float[3][4];

        for (int i = 0; i < 3; i++) {
            System.arraycopy(data[i], 0, augmented[i], 0, 3);
            switch(i) {
                case 0: augmented[i][3] = b.getX(); break;
                case 1: augmented[i][3] = b.getY(); break;
                case 2: augmented[i][3] = b.getZ(); break;
            }
        }

        for (int i = 0; i < 3; i++) {
            int maxRow = i;
            for (int k = i + 1; k < 3; k++) {
                if (Math.abs(augmented[k][i]) > Math.abs(augmented[maxRow][i])) {
                    maxRow = k;
                }
            }

            float[] temp = augmented[i];
            augmented[i] = augmented[maxRow];
            augmented[maxRow] = temp;

            if (Math.abs(augmented[i][i]) < 1e-12f) {
                throw new ArithmeticException("Matrix is singular, cannot solve system");
            }

            for (int k = i + 1; k < 3; k++) {
                float factor = augmented[k][i] / augmented[i][i];
                for (int j = i; j < 4; j++) {
                    augmented[k][j] -= factor * augmented[i][j];
                }
            }
        }

        float[] solution = new float[3];
        for (int i = 2; i >= 0; i--) {
            solution[i] = augmented[i][3];
            for (int j = i + 1; j < 3; j++) {
                solution[i] -= augmented[i][j] * solution[j];
            }
            solution[i] /= augmented[i][i];
        }

        return new Vector3D(solution[0], solution[1], solution[2]);
    }
}