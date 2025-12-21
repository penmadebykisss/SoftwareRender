package RenderingModes.test;

import RenderingModes.data.*;
import RenderingModes.shaders.*;

public class SimpleTest {
    public static void main(String[] args) {
        System.out.println("=== Testing RenderingModes ===\n");

        // Test 1: Color
        testColor();

        // Test 2: Barycentric
        testBarycentric();

        // Test 3: Shaders
        testShaders();

        System.out.println("\n✅ All basic tests passed!");
    }

    private static void testColor() {
        System.out.println("1. Testing Color class...");
        Color red = Color.RED;
        Color halfRed = red.multiply(0.5f);
        assert halfRed.getR() == 0.5f : "Color multiplication failed";
        System.out.println("   ✓ Color test passed");
    }

    private static void testBarycentric() {
        System.out.println("2. Testing BarycentricCoords...");
        BarycentricCoords bc = new BarycentricCoords(0.3f, 0.3f, 0.4f);
        assert bc.isInside() : "Should be inside triangle";

        float result = bc.interpolate(1f, 2f, 3f);
        assert Math.abs(result - 2.1f) < 0.001f : "Interpolation failed";
        System.out.println("   ✓ Barycentric test passed");
    }

    private static void testShaders() {
        System.out.println("3. Testing Shaders...");

        // Create test vertex data: 3 vertices, each with position (x,y,z), color (r,g,b,a)
        float[][] vertexData = {
                {0, 0, 0, 1, 0, 0, 1}, // vertex 0: x,y,z, r,g,b,a
                {1, 0, 0, 0, 1, 0, 1}, // vertex 1
                {0, 1, 0, 0, 0, 1, 1}  // vertex 2
        };

        BarycentricCoords bc = new BarycentricCoords(0.3f, 0.3f, 0.4f);

        // Test SolidColorShader
        SolidColorShader solidShader = new SolidColorShader(Color.RED);
        Color solidResult = solidShader.shade(vertexData, bc, 0, 0, 0.5f, null);
        assert solidResult.equals(Color.RED) : "SolidColorShader failed";

        System.out.println("   ✓ Shaders test passed");
    }
}