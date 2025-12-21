package RenderingModes.core;

public interface IRenderableModel {
    List<Triangle> getTriangles();
    boolean isTriangulated();
    void ensureTriangulated();
    void recalculateNormals();
}
