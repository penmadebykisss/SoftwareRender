package RenderingModes.core;

import RenderingModes.data.Triangle;
import java.util.List;

public interface IRenderableModel {
    List<Triangle> getTriangles();
    int getTriangleCount();
}