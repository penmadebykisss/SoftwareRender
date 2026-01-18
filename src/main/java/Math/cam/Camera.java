package Math.cam;

import Math.matrix.Matrix4x4;
import Math.vector.Vector3D;

public class Camera {
    private Vector3D position;
    private Vector3D target;
    private Vector3D up;

    public Camera(Vector3D position, Vector3D target) {
        this.position = position;
        this.target = target;
        this.up = new Vector3D(0, 1, 0);
    }


    public Vector3D getPosition() { return position; }
    public void setPosition(Vector3D position) { this.position = position; }

    public Vector3D getTarget() { return target; }
    public void setTarget(Vector3D target) { this.target = target; }


    public Matrix4x4 getViewMatrix() {
        Vector3D zAxis = position.subtract(target).normalize();
        Vector3D xAxis = up.cross(zAxis).normalize();
        Vector3D yAxis = zAxis.cross(xAxis);

        float[][] viewData = {
                {xAxis.getX(), xAxis.getY(), xAxis.getZ(), -xAxis.dot(position)},
                {yAxis.getX(), yAxis.getY(), yAxis.getZ(), -yAxis.dot(position)},
                {zAxis.getX(), zAxis.getY(), zAxis.getZ(), -zAxis.dot(position)},
                {0, 0, 0, 1}
        };
        return new Matrix4x4(viewData);
    }
}
