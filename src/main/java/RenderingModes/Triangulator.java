package RenderingModes;

import Interface.model.Model;
import Interface.model.Polygon;

import java.util.ArrayList;
import java.util.List;

public class Triangulator {
    public static void triangulate(Model model) {
        List<Polygon> originalPolygons = model.getPolygons();
        List<Polygon> triangulated = new ArrayList<>(originalPolygons.size());

        for (Polygon polygon : originalPolygons) {
            ArrayList<Integer> v = polygon.getVertexIndices();
            ArrayList<Integer> t = polygon.getTextureVertexIndices();
            ArrayList<Integer> n = polygon.getNormalIndices();

            int vertexCount = v.size();
            if (vertexCount <= 3) {
                triangulated.add(polygon);
                continue;
            }

            // Разбиение fan-методом: (0, i, i+1).
            for (int i = 1; i < vertexCount - 1; ++i) {
                Polygon triangle = new Polygon();

                // Вершины
                ArrayList<Integer> triV = new ArrayList<>(3);
                triV.add(v.get(0));
                triV.add(v.get(i));
                triV.add(v.get(i + 1));
                triangle.setVertexIndices(triV);

                // Текстуры
                if (t != null && !t.isEmpty()) {
                    ArrayList<Integer> triT = new ArrayList<>(3);
                    triT.add(t.get(0));
                    triT.add(t.get(i));
                    triT.add(t.get(i + 1));
                    triangle.setTextureVertexIndices(triT);
                }

                // Нормали (переносим существующие, если они есть)
                if (n != null && !n.isEmpty()) {
                    ArrayList<Integer> triN = new ArrayList<>(3);
                    triN.add(n.get(0));
                    triN.add(n.get(i));
                    triN.add(n.get(i + 1));
                    triangle.setNormalIndices(triN);
                } else {
                    triangle.setNormalIndices(new ArrayList<>());
                }

                triangulated.add(triangle);
            }
        }

        model.setPolygons(new ArrayList<>(triangulated));
    }
}