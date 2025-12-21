package RenderingModes.shaders;

import RenderingModes.core.RenderContext;
import RenderingModes.data.*;

public abstract class BaseShader implements Shader {

    @Override
    public Color shade(
            float[][] vertexData,
            BarycentricCoords bc,
            int pixelX, int pixelY,
            float interpolatedDepth,
            RenderContext context
    ) {
        if (bc == null || !bc.isInside()) {
            return null;
        }
        return computePixel(vertexData, bc, pixelX, pixelY, interpolatedDepth, context);
    }

    protected abstract Color computePixel(
            float[][] vertexData,
            BarycentricCoords bc,
            int pixelX, int pixelY,
            float interpolatedDepth,
            RenderContext context
    );

    protected float interpolateAttribute(float[][] vertexData, BarycentricCoords bc, int attribIndex) {
        if (vertexData == null || vertexData.length < 3) return 0;
        return bc.interpolate(
                vertexData[0][attribIndex],
                vertexData[1][attribIndex],
                vertexData[2][attribIndex]
        );
    }

    protected Color interpolateColor(float[][] vertexData, BarycentricCoords bc, int colorStartIndex) {
        float r = interpolateAttribute(vertexData, bc, colorStartIndex);
        float g = interpolateAttribute(vertexData, bc, colorStartIndex + 1);
        float b = interpolateAttribute(vertexData, bc, colorStartIndex + 2);
        float a = interpolateAttribute(vertexData, bc, colorStartIndex + 3);
        return new Color(r, g, b, a);
    }
}