package RenderingModes;

import Interface.model.Model;
import Interface.model.Polygon;
import Math.cam.Camera;
import Math.matrix.Matrix4x4;
import Math.vector.Vector2D;
import Math.vector.Vector3D;
import Math.vector.Vector4D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class RenderEngine {

    public static void render(
            final GraphicsContext graphicsContext,
            final Camera camera,
            final Model mesh,
            final int width,
            final int height,
            final Texture texture,
            final Lighting lighting,
            final Color baseColor,
            final RenderingModes renderingModes,
            final Matrix4x4 modelMatrix) {
        ZBuffer zBuffer = new ZBuffer(width, height);

        Matrix4x4 viewMatrix = camera.getViewMatrix();
        Matrix4x4 projectionMatrix = camera.getProjectionMatrix();
        Matrix4x4 mvpMatrix = projectionMatrix.multiply(viewMatrix.multiply(modelMatrix));

        if (mesh == null) return;

        for (Polygon polygon : mesh.getPolygons()) {
            ArrayList<Integer> vIndices = polygon.getVertexIndices();
            if (vIndices.size() < 3) continue;

            ArrayList<ScreenVertex> screenVertices = new ArrayList<>();

            for (int i = 0; i < vIndices.size(); i++) {
                Vector3D modelPos = mesh.getVertices().get(vIndices.get(i));
                Vector4D clipPos = mvpMatrix.multiply(new Vector4D(modelPos, 1.0f));

                Vector3D ndcPos = clipPos.toVector3D();
                float invW = 1.0f / clipPos.getW();

                Vector2D uv = null;
                if (renderingModes.isUseTexture() && !polygon.getTextureVertexIndices().isEmpty()) {
                    uv = mesh.getTextureVertices().get(polygon.getTextureVertexIndices().get(i));
                }

                Vector3D normal = null;
                Vector3D worldPos = null;
                if (renderingModes.isUseLighting()) {
                    if (!polygon.getNormalIndices().isEmpty()) {
                        Vector3D rawNormal = mesh.getNormals().get(polygon.getNormalIndices().get(i));
                        normal = modelMatrix.multiply(rawNormal).normalize();
                    }
                    worldPos = modelMatrix.multiply(modelPos);
                }

                screenVertices.add(toScreenVertex(ndcPos, width, height, invW, uv, normal, worldPos));
            }

            // Заливаем только если включен флаг заливки
            if (renderingModes.isDrawFilled()) {
                for (int i = 1; i < screenVertices.size() - 1; i++) {
                    TriangleRasterization.fillTriangle(
                            graphicsContext,
                            zBuffer,
                            screenVertices.get(0),
                            screenVertices.get(i),
                            screenVertices.get(i+1),
                            width,
                            height,
                            texture,
                            lighting,
                            baseColor,
                            camera.getPosition(),
                            renderingModes
                    );
                }
            }

            if (renderingModes.isDrawWireframe()) {
                Color wireColor = Color.BLACK;
                for (int i = 0; i < screenVertices.size(); i++) {
                    ScreenVertex a = screenVertices.get(i);
                    ScreenVertex b = screenVertices.get((i + 1) % screenVertices.size());

                    LineRasterizer.drawLine(
                            graphicsContext,
                            zBuffer,
                            a, b,
                            width, height,
                            wireColor,
                            1.0
                    );
                }
            }
        }
    }

    private static ScreenVertex toScreenVertex(Vector3D ndc, int w, int h, float invW, Vector2D uv, Vector3D norm, Vector3D worldPos) {
        float screenX = (ndc.getX() + 1.0f) * 0.5f * (w - 1);
        float screenY = (1.0f - ndc.getY()) * 0.5f * (h - 1);
        return new ScreenVertex(screenX, screenY, ndc.getZ(), invW, uv, norm, worldPos, null);
    }
}