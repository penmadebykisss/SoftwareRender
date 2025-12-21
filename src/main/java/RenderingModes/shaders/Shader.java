package RenderingModes.shaders;

import RenderingModes.core.RenderContext;
import RenderingModes.data.*;

public interface Shader {
    Color shade(
            float[][] vertexData,     // [3][n] где n >= 3 (x,y,z, u,v, nx,ny,nz, r,g,b,a...)
            BarycentricCoords bc,
            int pixelX, int pixelY,
            float interpolatedDepth,
            RenderContext context
    );
}