package MathTest.affine;

import Math.affine.AffineTransformations;
import Math.matrix.Matrix4x4;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

public class AffineTransformationsTests {
    private AffineTransformations transformations;

    @BeforeEach
    void setUp() {
        this.transformations = new AffineTransformations();
    }

    @Test
    void positiveValuesScaleTest(){
        float sx = 3.0f;
        float sy = 3.0f;
        float sz = 3.0f;

        Matrix4x4 res = transformations.scale(sx, sy, sz);

        assertEquals(sx, res.get(0, 0));
        assertEquals(sy, res.get(1, 1));
        assertEquals(sz, res.get(2, 2));
        assertEquals(1.0f, res.get(3, 3));
    }

    @Test
    void negativeValuesScaleTest(){
        float sx = -3.0f;
        float sy = -3.0f;
        float sz = -3.0f;

        Matrix4x4 res = transformations.scale(sx, sy, sz);

        assertEquals(sx, res.get(0, 0));
        assertEquals(sy, res.get(1, 1));
        assertEquals(sz, res.get(2, 2));
        assertEquals(1.0f, res.get(3, 3));
    }

    @Test
    void zeroValuesScaleTest(){
        float sx = 0.0f;
        float sy = 0.0f;
        float sz = 0.0f;

        Matrix4x4 res = transformations.scale(sx, sy, sz);
        assertEquals(sx, res.get(0, 0));
        assertEquals(sy, res.get(1, 1));
        assertEquals(sz, res.get(2, 2));
        assertEquals(1.0f, res.get(3, 3));
    }

    @Test
    void differentValuesScaleTest(){
        float sx = 0.0f;
        float sy = 2.0f;
        float sz = 1.5f;

        Matrix4x4 res = transformations.scale(sx, sy, sz);
        assertEquals(sx, res.get(0, 0));
        assertEquals(sy, res.get(1, 1));
        assertEquals(sz, res.get(2, 2));
        assertEquals(1.0f, res.get(3, 3));
    }

    @Test
    void rotateXTestPositiveValue(){
        float angleDegrees = 50.0f;
        float radians = (float) Math.toRadians(angleDegrees);
        float cos = (float) Math.cos(radians);
        float sin = (float) Math.sin(radians);

        Matrix4x4 res = transformations.rotateX(angleDegrees);

        assertEquals(cos, res.get(1, 1), 1e-6);
        assertEquals(-sin, res.get(1, 2), 1e-6);
        assertEquals(sin, res.get(2, 1), 1e-6);
        assertEquals(cos, res.get(2, 2), 1e-6);
        assertEquals(1.0f, res.get(3, 3));
    }

    @Test
    void rotateXTestNegativeValue(){
        float angleDegrees = -50.0f;
        float radians = (float) Math.toRadians(angleDegrees);
        float cos = (float) Math.cos(radians);
        float sin = (float) Math.sin(radians);

        Matrix4x4 res = transformations.rotateX(angleDegrees);

        assertEquals(cos, res.get(1, 1), 1e-6);
        assertEquals(-sin, res.get(1, 2), 1e-6);
        assertEquals(sin, res.get(2, 1), 1e-6);
        assertEquals(cos, res.get(2, 2), 1e-6);
        assertEquals(1.0f, res.get(3, 3));
    }

    @Test
    void rotateYTestPositiveValue(){
        float angleDegrees = 37.2f;
        float radians = (float) Math.toRadians(angleDegrees);
        float cos = (float) Math.cos(radians);
        float sin = (float) Math.sin(radians);

        Matrix4x4 res = transformations.rotateY(angleDegrees);

        assertEquals(cos, res.get(0, 0), 1e-6);
        assertEquals(sin, res.get(0, 2), 1e-6);
        assertEquals(-sin, res.get(2, 0), 1e-6);
        assertEquals(cos, res.get(2, 2), 1e-6);
        assertEquals(1.0f, res.get(3, 3));
    }

    @Test
    void rotateYTestNegativeValue(){
        float angleDegrees = -37.2f;
        float radians = (float) Math.toRadians(angleDegrees);
        float cos = (float) Math.cos(radians);
        float sin = (float) Math.sin(radians);

        Matrix4x4 res = transformations.rotateY(angleDegrees);

        assertEquals(cos, res.get(0, 0), 1e-6);
        assertEquals(sin, res.get(0, 2), 1e-6);
        assertEquals(-sin, res.get(2, 0), 1e-6);
        assertEquals(cos, res.get(2, 2), 1e-6);
        assertEquals(1.0f, res.get(3, 3));
    }

    @Test
    void rotateZTestPositiveValue(){
        float angleDegrees = 90f;
        float radians = (float) Math.toRadians(angleDegrees);
        float cos = (float) Math.cos(radians);
        float sin = (float) Math.sin(radians);

        Matrix4x4 res = transformations.rotateZ(angleDegrees);

        assertEquals(cos, res.get(0, 0), 1e-6);
        assertEquals(-sin, res.get(0, 1), 1e-6);
        assertEquals(sin, res.get(1, 0), 1e-6);
        assertEquals(cos, res.get(1, 1), 1e-6);
        assertEquals(1.0f, res.get(3, 3));
    }

    @Test
    void rotateZTestNegativeValue(){
        float angleDegrees = -90f;
        float radians = (float) Math.toRadians(angleDegrees);
        float cos = (float) Math.cos(radians);
        float sin = (float) Math.sin(radians);

        Matrix4x4 res = transformations.rotateZ(angleDegrees);

        assertEquals(cos, res.get(0, 0), 1e-6);
        assertEquals(-sin, res.get(0, 1), 1e-6);
        assertEquals(sin, res.get(1, 0), 1e-6);
        assertEquals(cos, res.get(1, 1), 1e-6);
        assertEquals(1.0f, res.get(3, 3));
    }

    @Test
    void TranslateOnlyXTest(){
        float tx = 10.25f;
        float ty = 0f;
        float tz = 0f;
        Matrix4x4 res = transformations.translate(tx, ty, tz);

        assertEquals(tx, res.get(0, 3));
        assertEquals(ty, res.get(1, 3));
        assertEquals(tz, res.get(2, 3));
    }

    @Test
    void TranslateOnlyYTest(){
        float tx = 0f;
        float ty = 2.5f;
        float tz = 0f;
        Matrix4x4 res = transformations.translate(tx, ty, tz);

        assertEquals(tx, res.get(0, 3));
        assertEquals(ty, res.get(1, 3));
        assertEquals(tz, res.get(2, 3));
    }

    @Test
    void TranslateOnlyZTest(){
        float tx = 0f;
        float ty = 0f;
        float tz = -25.43f;
        Matrix4x4 res = transformations.translate(tx, ty, tz);

        assertEquals(tx, res.get(0, 3));
        assertEquals(ty, res.get(1, 3));
        assertEquals(tz, res.get(2, 3));
    }

    @Test
    void TranslateXAndZTest(){
        float tx = 23.1f;
        float ty = 0f;
        float tz = 40f;
        Matrix4x4 res = transformations.translate(tx, ty, tz);
        assertEquals(tx, res.get(0, 3));
        assertEquals(ty, res.get(1, 3));
        assertEquals(tz, res.get(2, 3));
    }

    @Test
    void TranslateYAndZTest(){
        float tx = 0f;
        float ty = 12.111f;
        float tz = 10.3f;
        Matrix4x4 res = transformations.translate(tx, ty, tz);
        assertEquals(tx, res.get(0, 3));
        assertEquals(ty, res.get(1, 3));
        assertEquals(tz, res.get(2, 3));
    }

    @Test
    void TranslateXAndYAndZTest(){
        float tx = -2.4f;
        float ty = 30f;
        float tz = -1.4f;
        Matrix4x4 res = transformations.translate(tx, ty, tz);
        assertEquals(tx, res.get(0, 3));
        assertEquals(ty, res.get(1, 3));
        assertEquals(tz, res.get(2, 3));
    }
}