package RenderingModes;

import Math.matrix.Matrix4x4;
import Math.vector.Vector3D;
import javafx.scene.paint.Color;

public class Lighting {
    private Vector3D lightDirection;
    private final Color ambientColor;
    private final Color diffuseColor;
    private final float ambientIntensity;
    private final float diffuseIntensity;

    public Lighting(Vector3D cameraPosition, Vector3D cameraTarget, float ambientIntensity, float diffuseIntensity) {
        this.lightDirection = new Vector3D(0.0f, 0.0f, -1.0f);
        this.ambientIntensity = Math.max(0.0f, Math.min(1.0f, ambientIntensity));
        this.diffuseIntensity = Math.max(0.0f, Math.min(1.0f, diffuseIntensity));

        this.ambientColor = Color.WHITE;
        this.diffuseColor = Color.WHITE;
    }

    public Lighting(Vector3D cameraPosition, Vector3D cameraTarget, Color ambientColor, Color diffuseColor,
                    float ambientIntensity, float diffuseIntensity) {
        this.lightDirection = new Vector3D(0.0f, 0.0f, -1.0f);
        this.ambientIntensity = Math.max(0.0f, Math.min(1.0f, ambientIntensity));
        this.diffuseIntensity = Math.max(0.0f, Math.min(1.0f, diffuseIntensity));

        this.ambientColor = ambientColor;
        this.diffuseColor = diffuseColor;
    }

    public void update(Vector3D cameraPosition, Vector3D cameraTarget, Matrix4x4 viewMatrix) {
        this.lightDirection = new Vector3D(0.0f, 0.0f, -1.0f);
    }

    public float computeLightingIntensity(Vector3D normal, Vector3D vertexPosition, Vector3D cameraPosition) {

        Vector3D ray = vertexPosition.subtract(cameraPosition).normalize();

        Vector3D n = normal.normalize();
        float l = -n.dot(ray);

        if (l < 0.0f) {
            l = 0.0f;
        }

        return Math.max(0.0f, Math.min(1.0f, l));
    }


    public Color shadeColor(Color baseColor, float intensity) {

        double ambientR = baseColor.getRed() * ambientIntensity;
        double ambientG = baseColor.getGreen() * ambientIntensity;
        double ambientB = baseColor.getBlue() * ambientIntensity;

        double diffuseR = baseColor.getRed() * diffuseIntensity * intensity;
        double diffuseG = baseColor.getGreen() * diffuseIntensity * intensity;
        double diffuseB = baseColor.getBlue() * diffuseIntensity * intensity;

        double r = ambientR + diffuseR;
        double g = ambientG + diffuseG;
        double b = ambientB + diffuseB;

        r = Math.max(0.0, Math.min(1.0, r));
        g = Math.max(0.0, Math.min(1.0, g));
        b = Math.max(0.0, Math.min(1.0, b));

        return new Color(r, g, b, baseColor.getOpacity());
    }

    public Vector3D getLightDirection() { return lightDirection; }


}