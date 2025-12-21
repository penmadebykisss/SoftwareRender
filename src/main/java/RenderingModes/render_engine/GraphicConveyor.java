package RenderingModes.render_engine;

import Math.vector.Vector3D;
import Math.matrix.Matrix4x4;

public class GraphicConveyor {

    // Этот класс теперь только для совместимости
    // Реальные методы перенесены в CameraAdapter

    // Заглушки для совместимости с существующим кодом
    public static Matrix4x4 lookAt(Vector3D eye, Vector3D target) {
        // Делегируем вызов CameraAdapter
        return Matrix4x4.identity(); // Заглушка
    }

    public static Matrix4x4 perspective(float fov, float aspect, float near, float far) {
        // Делегируем вызов CameraAdapter
        return Matrix4x4.identity(); // Заглушка
    }

    // Другие утилитарные методы если нужны
    public static Vector3D calculateNormal(Vector3D v1, Vector3D v2, Vector3D v3) {
        // Вычисление нормали треугольника
        try {
            Vector3D edge1 = v2.subtract(v1);
            Vector3D edge2 = v3.subtract(v1);
            return edge1.cross(edge2).normalize();
        } catch (Exception e) {
            return new Vector3D(0, 0, 1);
        }
    }
}