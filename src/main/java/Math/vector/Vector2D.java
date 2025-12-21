package Math.vector;

public final class Vector2D extends AbstractVector<Vector2D> {

    public Vector2D(float x, float y) {
        super(new float[]{x, y});
    }

    @Override
    protected Vector2D createNew(float[] components) {
        return new Vector2D(components[0], components[1]);
    }

    public float getX() { return components[0]; }
    public float getY() { return components[1]; }

    public float cross(Vector2D other) {
        return getX() * other.getY() - getY() * other.getX();
    }

    public float distance(Vector2D other) {
        float dx = getX() - other.getX();
        float dy = getY() - other.getY();
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    @Override
    public String toString() {
        return String.format("Vector2D(%.3f, %.3f)", getX(), getY());
    }
}