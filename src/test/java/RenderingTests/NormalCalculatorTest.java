package RenderingTests;

import RenderingModes.NormalCalculator;
import Interface.model.Model;
import Interface.model.Polygon;
import Math.vector.Vector3D;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;

class NormalCalculatorTest {

    @Test
    void testSimpleTriangleNormal() {
        Model model = new Model();

        // Треугольник в плоскости XY
        model.getVertices().add(new Vector3D(0, 0, 0)); // 0
        model.getVertices().add(new Vector3D(1, 0, 0)); // 1
        model.getVertices().add(new Vector3D(0, 1, 0)); // 2

        Polygon tri = new Polygon();
        tri.setVertexIndices(new ArrayList<>(Arrays.asList(0, 1, 2)));
        model.getPolygons().add(tri);

        NormalCalculator.recalculateNormals(model);

        // Должна появиться нормаль (0, 0, 1)
        assertFalse(model.getNormals().isEmpty());
        Vector3D n = model.getNormals().get(0);

        assertEquals(0f, n.getX(), 1e-6);
        assertEquals(0f, n.getY(), 1e-6);
        assertEquals(1f, n.getZ(), 1e-6);
    }

    @Test
    void testAveragedNormalsBetweenTwoPlanes() {
        Model model = new Model();

        // Создаем две грани под углом 90 градусов (как уголок)
        // Грань 1 (плоскость XY): (0,0,0), (1,0,0), (0,1,0) -> нормаль (0,0,1)
        // Грань 2 (плоскость XZ): (0,0,0), (0,0,1), (1,0,0) -> нормаль (0,1,0)
        model.getVertices().add(new Vector3D(0, 0, 0)); // Индекс 0 - общая вершина
        model.getVertices().add(new Vector3D(1, 0, 0)); // Индекс 1 - общая
        model.getVertices().add(new Vector3D(0, 1, 0)); // Индекс 2
        model.getVertices().add(new Vector3D(0, 0, 1)); // Индекс 3

        Polygon tri1 = new Polygon();
        tri1.setVertexIndices(new ArrayList<>(Arrays.asList(0, 1, 2)));

        Polygon tri2 = new Polygon();
        tri2.setVertexIndices(new ArrayList<>(Arrays.asList(0, 3, 1)));

        model.getPolygons().add(tri1);
        model.getPolygons().add(tri2);

        NormalCalculator.recalculateNormals(model);

        // Нормаль в общей вершине (0) должна быть направлена под 45 градусов: (0, 0.707, 0.707)
        Vector3D n0 = model.getNormals().get(0);

        // Проверяем, что нормаль "усреднилась" и имеет единичную длину
        assertEquals(1.0f, n0.length(), 1e-6);
        assertTrue(n0.getY() > 0 && n0.getZ() > 0, "Normal should be averaged between two planes");
        assertEquals(n0.getY(), n0.getZ(), 1e-6);
    }

    @Test
    void testNormalDirectionByVertexOrder() {
        Model model = new Model();
        model.getVertices().add(new Vector3D(0, 0, 0));
        model.getVertices().add(new Vector3D(1, 0, 0));
        model.getVertices().add(new Vector3D(0, 1, 0));

        // Порядок против часовой стрелки
        Polygon triCCW = new Polygon();
        triCCW.setVertexIndices(new ArrayList<>(Arrays.asList(0, 1, 2)));
        model.getPolygons().add(triCCW);

        NormalCalculator.recalculateNormals(model);
        float zCCW = model.getNormals().get(0).getZ();

        // Очищаем и создаем такой же треугольник, но с порядком ПО часовой
        model.getNormals().clear();
        model.getPolygons().clear();
        Polygon triCW = new Polygon();
        triCW.setVertexIndices(new ArrayList<>(Arrays.asList(0, 2, 1)));
        model.getPolygons().add(triCW);

        NormalCalculator.recalculateNormals(model);
        float zCW = model.getNormals().get(0).getZ();

        assertEquals(zCCW, -zCW, 1e-6, "Normal should flip when vertex order is reversed");
    }
}
