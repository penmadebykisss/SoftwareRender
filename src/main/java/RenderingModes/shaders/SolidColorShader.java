package RenderingModes.shaders;

import RenderingModes.core.RenderContext;
import RenderingModes.data.*;

public class SolidColorShader extends BaseShader {
    private final Color color;

    public SolidColorShader(Color color) {
        this.color = color;
    }

    @Override
    protected Color computePixel(
            float[][] vertexData,
            BarycentricCoords bc,
            int pixelX, int pixelY,
            float interpolatedDepth,
            RenderContext context
    ) {
        return color;
    }
}