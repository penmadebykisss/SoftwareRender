package RenderingModes.shaders;

import RenderingModes.core.RenderContext;
import RenderingModes.data.*;

public class GradientShader extends BaseShader {
    private final Color[] vertexColors;

    public GradientShader(Color c0, Color c1, Color c2) {
        this.vertexColors = new Color[]{c0, c1, c2};
    }

    @Override
    protected Color computePixel(
            float[][] vertexData,
            BarycentricCoords bc,
            int pixelX, int pixelY,
            float interpolatedDepth,
            RenderContext context
    ) {
        // Интерполируем цвета вершин
        float r = bc.interpolate(
                vertexColors[0].getR(),
                vertexColors[1].getR(),
                vertexColors[2].getR()
        );
        float g = bc.interpolate(
                vertexColors[0].getG(),
                vertexColors[1].getG(),
                vertexColors[2].getG()
        );
        float b = bc.interpolate(
                vertexColors[0].getB(),
                vertexColors[1].getB(),
                vertexColors[2].getB()
        );
        float a = bc.interpolate(
                vertexColors[0].getA(),
                vertexColors[1].getA(),
                vertexColors[2].getA()
        );

        return new Color(r, g, b, a);
    }
}