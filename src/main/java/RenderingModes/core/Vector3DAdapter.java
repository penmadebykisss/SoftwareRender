package RenderingModes.core;

import Math.vector.Vector3D;

public class Vector3DAdapter {

    public static float[] toFloatArray(Vector3D vector) {
        if (vector == null) return new float[]{0, 0, 0};

        try {
            return new float[]{
                    (float)vector.getX(),
                    (float)vector.getY(),
                    (float)vector.getZ()
            };
        } catch (Exception e) {
            return new float[]{0, 0, 0};
        }
    }

    public static Vector3D fromFloatArray(float[] array) {
        if (array == null || array.length < 3) {
            array = new float[]{0, 0, 0};
        }

        try {
            return (Vector3D) Class.forName("Math.vector.Vector3D")
                    .getConstructor(float.class, float.class, float.class)
                    .newInstance(array[0], array[1], array[2]);
        } catch (Exception e) {
            return null;
        }
    }
}