/*
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
*/
package Math.cam;

import Math.matrix.Matrix4x4;
import Math.vector.Vector3D;

public class Camera {
    private Vector3D position;
    private Vector3D target;
    private Vector3D up;

    private float fov;         // Field of View в градусах
    private float aspectRatio; // Соотношение сторон
    private float nearPlane;   // Ближняя плоскость отсечения
    private float farPlane;    // Дальняя плоскость отсечения

    public Camera(Vector3D position, Vector3D target, float fov, float aspectRatio, float nearPlane, float farPlane) {
        this.position = position;
        this.target = target;
        this.up = new Vector3D(0, 1, 0);
        this.fov = fov;
        this.aspectRatio = aspectRatio;
        this.nearPlane = nearPlane;
        this.farPlane = farPlane;
    }

    public Camera(Vector3D position, Vector3D target) {
        this(position, target, 60.0f, 16.0f / 9.0f, 0.1f, 100.0f);
    }

    public Vector3D getPosition() {
        return position;
    }

    public void setPosition(Vector3D position) {
        this.position = position;
    }

    public Vector3D getTarget() {
        return target;
    }

    public void setTarget(Vector3D target) {
        this.target = target;
    }

    public float getFov() {
        return fov;
    }

    public void setFov(float fov) {
        this.fov = fov;
    }

    public float getAspectRatio() {
        return aspectRatio;
    }

    public void setAspectRatio(float aspectRatio) {
        this.aspectRatio = aspectRatio;
    }

    public float getNearPlane() {
        return nearPlane;
    }

    public void setNearPlane(float nearPlane) {
        this.nearPlane = nearPlane;
    }

    public float getFarPlane() {
        return farPlane;
    }

    public void setFarPlane(float farPlane) {
        this.farPlane = farPlane;
    }

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

    public Matrix4x4 getProjectionMatrix() {
        float fovRad = (float) Math.toRadians(fov);
        float tanHalfFov = (float) Math.tan(fovRad / 2.0f);

        float[][] projData = new float[4][4];

        projData[0][0] = 1.0f / (aspectRatio * tanHalfFov);
        projData[1][1] = 1.0f / tanHalfFov;
        projData[2][2] = -(farPlane + nearPlane) / (farPlane - nearPlane);
        projData[2][3] = -(2.0f * farPlane * nearPlane) / (farPlane - nearPlane);
        projData[3][2] = -1.0f;
        projData[3][3] = 0.0f;

        return new Matrix4x4(projData);
    }

    public Matrix4x4 getViewProjectionMatrix() {
        return getProjectionMatrix().multiply(getViewMatrix());
    }
}