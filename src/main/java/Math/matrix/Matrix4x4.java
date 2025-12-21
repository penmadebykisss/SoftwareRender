package Math.matrix;

import Math.vector.Vector3D;
import Math.vector.Vector4D;

public final class Matrix4x4 extends AbstractMatrix<Matrix4x4, Vector4D> {

    public Matrix4x4(float[][] data) {
        super(data, 4, 4);
    }

    @Override
    protected Matrix4x4 createNew(float[][] data) {
        return new Matrix4x4(data);
    }

    public static Matrix4x4 identity() {
        return new Matrix4x4(new float[][]{
                {1, 0, 0, 0},
                {0, 1, 0, 0},
                {0, 0, 1, 0},
                {0, 0, 0, 1}
        });
    }

    public static Matrix4x4 zero() {
        return new Matrix4x4(new float[4][4]);
    }

    public static Matrix4x4 translation(float x, float y, float z) {
        return new Matrix4x4(new float[][]{
                {1, 0, 0, x},
                {0, 1, 0, y},
                {0, 0, 1, z},
                {0, 0, 0, 1}
        });
    }

    @Override
    public Vector4D multiply(Vector4D vector) {
        float x = data[0][0] * vector.getX() + data[0][1] * vector.getY() +
                data[0][2] * vector.getZ() + data[0][3] * vector.getW();
        float y = data[1][0] * vector.getX() + data[1][1] * vector.getY() +
                data[1][2] * vector.getZ() + data[1][3] * vector.getW();
        float z = data[2][0] * vector.getX() + data[2][1] * vector.getY() +
                data[2][2] * vector.getZ() + data[2][3] * vector.getW();
        float w = data[3][0] * vector.getX() + data[3][1] * vector.getY() +
                data[3][2] * vector.getZ() + data[3][3] * vector.getW();
        return new Vector4D(x, y, z, w);
    }

    public Vector3D multiply(Vector3D vector) {
        Vector4D homogeneous = new Vector4D(vector, 1.0f);
        Vector4D result = multiply(homogeneous);
        return result.toVector3D();
    }

    @Override
    public float determinant() {
        float det = 0;
        for (int j = 0; j < 4; j++) {
            det += data[0][j] * cofactor(0, j);
        }
        return det;
    }

    @Override
    public Matrix4x4 inverse() {
        float det = determinant();
        if (Math.abs(det) < 1e-12f) {
            throw new ArithmeticException("Matrix is singular, cannot invert");
        }

        float[][] result = new float[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                result[j][i] = cofactor(i, j) / det;
            }
        }
        return new Matrix4x4(result);
    }

    @Override
    public Vector4D solveLinearSystem(Vector4D vector) {
        return solveGauss(vector);
    }

    private Vector4D solveGauss(Vector4D b) {
        float[][] augmented = new float[4][5];

        for (int i = 0; i < 4; i++) {
            System.arraycopy(data[i], 0, augmented[i], 0, 4);
            switch(i) {
                case 0: augmented[i][4] = b.getX(); break;
                case 1: augmented[i][4] = b.getY(); break;
                case 2: augmented[i][4] = b.getZ(); break;
                case 3: augmented[i][4] = b.getW(); break;
            }
        }

        for (int i = 0; i < 4; i++) {
            int maxRow = i;
            for (int k = i + 1; k < 4; k++) {
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

            for (int k = i + 1; k < 4; k++) {
                float factor = augmented[k][i] / augmented[i][i];
                for (int j = i; j < 5; j++) {
                    augmented[k][j] -= factor * augmented[i][j];
                }
            }
        }

        float[] solution = new float[4];
        for (int i = 3; i >= 0; i--) {
            solution[i] = augmented[i][4];
            for (int j = i + 1; j < 4; j++) {
                solution[i] -= augmented[i][j] * solution[j];
            }
            solution[i] /= augmented[i][i];
        }

        return new Vector4D(solution[0], solution[1], solution[2], solution[3]);
    }

    private float minor(int row, int col) {
        float[][] minorMatrix = new float[3][3];
        int minorRow = 0;

        for (int i = 0; i < 4; i++) {
            if (i == row) continue;
            int minorCol = 0;
            for (int j = 0; j < 4; j++) {
                if (j == col) continue;
                minorMatrix[minorRow][minorCol] = data[i][j];
                minorCol++;
            }
            minorRow++;
        }

        Matrix3x3 minor = new Matrix3x3(minorMatrix);
        return minor.determinant();
    }

    private float cofactor(int row, int col) {
        float minor = minor(row, col);
        return ((row + col) % 2 == 0) ? minor : -minor;
    }
}