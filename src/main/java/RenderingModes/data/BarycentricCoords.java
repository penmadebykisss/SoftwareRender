package RenderingModes.data;

public class BarycentricCoords {
    public final float alpha, beta, gamma;

    public BarycentricCoords(float alpha, float beta, float gamma) {
        this.alpha = alpha;
        this.beta = beta;
        this.gamma = gamma;
    }

    public boolean isInside() {
        final float EPSILON = -0.0001f;
        return alpha >= EPSILON && beta >= EPSILON && gamma >= EPSILON;
    }

    public float interpolate(float v0, float v1, float v2) {
        return alpha * v0 + beta * v1 + gamma * v2;
    }

    public float[] interpolate(float[] v0, float[] v1, float[] v2) {
        if (v0 == null || v1 == null || v2 == null) return null;
        int size = Math.min(v0.length, Math.min(v1.length, v2.length));
        float[] result = new float[size];
        for (int i = 0; i < size; i++) {
            result[i] = interpolate(v0[i], v1[i], v2[i]);
        }
        return result;
    }

    @Override
    public String toString() {
        return String.format("Barycentric(%.3f, %.3f, %.3f)", alpha, beta, gamma);
    }
}