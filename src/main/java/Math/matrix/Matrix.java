package Math.matrix;

import Math.vector.Vector;

public interface Matrix<T extends Matrix<T, V>, V extends Vector<V>> {

    T add(T other);
    T subtract(T other);
    T multiply(float scalar);
    T multiply(T other);
    V multiply(V vector);
    T transpose();

    float determinant();
    T inverse();
    V solveLinearSystem(V vector);

    int getRows();
    int getCols();
    float get(int row, int col);
    /*
    static <T extends Matrix<T, V>, V extends Vector<V>> T identity(Class<T> clazz) {
        if (clazz == Matrix3x3.class) {
            return (T) Matrix3x3.identity();
        } else if (clazz == Matrix4x4.class) {
            return (T) Matrix4x4.identity();
        }
        throw new IllegalArgumentException("Unsupported matrix type");
    }

    static <T extends Matrix<T, V>, V extends Vector<V>> T zero(Class<T> clazz) {
        if (clazz == Matrix3x3.class) {
            return (T) Matrix3x3.zero();
        } else if (clazz == Matrix4x4.class) {
            return (T) Matrix4x4.zero();
        }
        throw new IllegalArgumentException("Unsupported matrix type");
    }
*/
    boolean equals(Object obj);
    String toString();
}