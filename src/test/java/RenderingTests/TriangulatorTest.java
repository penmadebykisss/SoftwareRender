package RenderingTests;

import RenderingModes.Triangulator;
import Interface.model.Model;
import Interface.model.Polygon;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;

class TriangulatorTest {

    @Test
    void testTriangulateQuad() {
        Model model = new Model();
        Polygon quad = new Polygon();
        // Создаем квадрат (4 вершины)
        quad.setVertexIndices(new ArrayList<>(Arrays.asList(0, 1, 2, 3)));
        model.getPolygons().add(quad);

        Triangulator.triangulate(model);

        // Квадрат должен разбиться на 2 треугольника
        assertEquals(2, model.getPolygons().size(), "Quad should be split into 2 triangles");

        // Проверяем первый треугольник (0, 1, 2)
        assertEquals(3, model.getPolygons().get(0).getVertexIndices().size());
        assertEquals(0, model.getPolygons().get(0).getVertexIndices().get(0));
    }

    @Test
    void testTriangulateAlreadyTriangle() {
        Model model = new Model();
        Polygon tri = new Polygon();
        tri.setVertexIndices(new ArrayList<>(Arrays.asList(0, 1, 2)));
        model.getPolygons().add(tri);

        Triangulator.triangulate(model);

        assertEquals(1, model.getPolygons().size(), "Triangle should remain as 1 polygon");
    }

    @Test
    void testTriangulateHexagonWithTextures() {
        Model model = new Model();
        Polygon hexagon = new Polygon();

        // 6 вершин и 6 соответствующих текстурных индексов
        ArrayList<Integer> vIdx = new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4, 5));
        ArrayList<Integer> tIdx = new ArrayList<>(Arrays.asList(10, 11, 12, 13, 14, 15));

        hexagon.setVertexIndices(vIdx);
        hexagon.setTextureVertexIndices(tIdx);
        model.getPolygons().add(hexagon);

        Triangulator.triangulate(model);

        // Шестиугольник должен разбиться на 4 треугольника (n - 2)
        assertEquals(4, model.getPolygons().size(), "Hexagon should split into 4 triangles");

        // Проверяем, что текстурные индексы последнего треугольника (0, 4, 5) перенеслись верно
        Polygon lastTri = model.getPolygons().get(3);
        assertEquals(10, lastTri.getTextureVertexIndices().get(0));
        assertEquals(14, lastTri.getTextureVertexIndices().get(1));
        assertEquals(15, lastTri.getTextureVertexIndices().get(2));
    }
}