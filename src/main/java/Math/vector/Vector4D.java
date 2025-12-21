package Math.vector;

public final class Vector4D extends AbstractVector<Vector4D> {

    public Vector4D(float x, float y, float z, float w) {
        super(new float[]{x, y, z, w});
    }

    public Vector4D(Vector3D vector, float w) {
        this(vector.getX(), vector.getY(), vector.getZ(), w);
    }

    @Override
    protected Vector4D createNew(float[] components) {
        return new Vector4D(components[0], components[1], components[2], components[3]);
    }

    public float getX() { return components[0]; }
    public float getY() { return components[1]; }
    public float getZ() { return components[2]; }
    public float getW() { return components[3]; }

    // Специфичные для 4D операции
    public Vector3D toVector3D() {
        if (Math.abs(getW()) < 1e-12f) {
            throw new ArithmeticException("Cannot project vector with w=0");
        }
        return new Vector3D(
                getX() / getW(),
                getY() / getW(),
                getZ() / getW()
        );
    }

    public float distance(Vector4D other) {
        float dx = getX() - other.getX();
        float dy = getY() - other.getY();
        float dz = getZ() - other.getZ();
        float dw = getW() - other.getW();
        return (float) Math.sqrt(dx * dx + dy * dy + dz * dz + dw * dw);
    }

    @Override
    public String toString() {
        return String.format("Vector4D(%.3f, %.3f, %.3f, %.3f)", getX(), getY(), getZ(), getW());
    }
}