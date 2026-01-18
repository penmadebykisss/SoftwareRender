package Interface.model;

import Math.affine.AffineTransformations;
import Math.matrix.Matrix4x4;
import Math.vector.Vector3D;

public class ModelTransformer {
    private final AffineTransformations transformations;

    public ModelTransformer() {
        this.transformations = new AffineTransformations();
    }

    public void translate(Model model, float tx, float ty, float tz) {
        Matrix4x4 translationMatrix = transformations.translate(tx, ty, tz);
        applyTransformation(model, translationMatrix);
    }

    public void rotate(Model model, float rx, float ry, float rz) {
        Matrix4x4 rotationX = transformations.rotateX(rx);
        Matrix4x4 rotationY = transformations.rotateY(ry);
        Matrix4x4 rotationZ = transformations.rotateZ(rz);

        Matrix4x4 combinedRotation = rotationZ.multiply(rotationY).multiply(rotationX);
        applyTransformation(model, combinedRotation);
    }

    public void scale(Model model, float sx, float sy, float sz) {
        Matrix4x4 scaleMatrix = transformations.scale(sx, sy, sz);
        applyTransformation(model, scaleMatrix);
    }

    private void applyTransformation(Model model, Matrix4x4 transformationMatrix) {
        for (int i = 0; i < model.getVertices().size(); i++) {
            Vector3D vertex = model.getVertices().get(i);
            Vector3D transformedVertex = transformationMatrix.multiply(vertex);
            model.getVertices().set(i, transformedVertex);
        }

        if (model.getNormals() != null) {
            for (int i = 0; i < model.getNormals().size(); i++) {
                Vector3D normal = model.getNormals().get(i);
                Vector3D transformedNormal = transformationMatrix.multiply(normal);
                try {
                    transformedNormal = transformedNormal.normalize();
                } catch (ArithmeticException e) {
                    // Если нормаль стала нулевой, оставляем исходную
                }
                model.getNormals().set(i, transformedNormal);
            }
        }
    }

    public Vector3D getModelCenter(Model model) {
        if (model.getVertices().isEmpty()) {
            return new Vector3D(0, 0, 0);
        }

        float sumX = 0, sumY = 0, sumZ = 0;
        for (Vector3D vertex : model.getVertices()) {
            sumX += vertex.getX();
            sumY += vertex.getY();
            sumZ += vertex.getZ();
        }

        int count = model.getVertices().size();
        return new Vector3D(sumX / count, sumY / count, sumZ / count);
    }

    public void centerModel(Model model) {
        Vector3D center = getModelCenter(model);
        translate(model, -center.getX(), -center.getY(), -center.getZ());
    }
}