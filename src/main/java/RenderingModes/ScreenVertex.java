package RenderingModes;

import Math.vector.Vector2D;
import Math.vector.Vector3D;

public class ScreenVertex {
    private final float x;
    private final float y;
    private final float z;
    private final float invW;
    private final Vector2D textureCoords;
    private final Vector3D normal;
    private final Vector3D worldPosition;
    private final Float lightingIntensity;

    public ScreenVertex(float x, float y, float z) {
        this(x, y, z, 1.0f, null, null, null, null);
    }

    public ScreenVertex(float x, float y, float z, float invW,
                        Vector2D textureCoords, Vector3D normal,
                        Vector3D worldPosition, Float lightingIntensity) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.invW = invW;
        this.textureCoords = textureCoords;
        this.normal = normal;
        this.worldPosition = worldPosition;
        this.lightingIntensity = lightingIntensity;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    public float getInvW() {
        return invW;
    }


    public Vector2D getTextureCoords() {
        return textureCoords;
    }

    public Vector3D getNormal() {
        return normal;
    }

    public boolean hasTextureCoords() {
        return textureCoords != null;
    }

    public boolean hasNormal() {
        return normal != null;
    }

    public Vector3D getWorldPosition() {
        return worldPosition;
    }

    public boolean hasWorldPosition() {
        return worldPosition != null;
    }

    public Float getLightingIntensity() {
        return lightingIntensity;
    }

    public boolean hasLightingIntensity() {
        return lightingIntensity != null;
    }
}


