package RenderingModes;

import Interface.model.Model;
import Interface.model.Polygon;
import Math.vector.Vector3D;

import java.util.ArrayList;
import java.util.List;

public class NormalCalculator {
    public static void recalculateNormals(Model model) {
        // Проверка входных данных: наличие геометрии.
        int vertexCount = model.getVertices().size();
        if (vertexCount == 0 || model.getPolygons().isEmpty()) {
            model.getNormals().clear();
            return;
        }

        // Накопление нормалей: суммирование нормалей граней по вершинам.
        Vector3D[] normalSums = new Vector3D[vertexCount];
        for (int i = 0; i < vertexCount; ++i) {
            normalSums[i] = new Vector3D(0.0f, 0.0f, 0.0f);
        }

        for (Polygon polygon : model.getPolygons()) {
            List<Integer> vIdx = polygon.getVertexIndices();
            if (vIdx.size() < 3) {
                continue;
            }

            int i0 = vIdx.get(0);
            int i1 = vIdx.get(1);
            int i2 = vIdx.get(2);

            Vector3D p0 = model.getVertices().get(i0);
            Vector3D p1 = model.getVertices().get(i1);
            Vector3D p2 = model.getVertices().get(i2);

            Vector3D edge1 = p1.subtract(p0);
            Vector3D edge2 = p2.subtract(p0);

            Vector3D faceNormal = edge1.cross(edge2);

            // Проверка вырожденных граней: нулевая площадь.
            if (faceNormal.length() < 1e-6f) {
                continue;
            }

            faceNormal = faceNormal.normalize();

            normalSums[i0] = normalSums[i0].add(faceNormal);
            normalSums[i1] = normalSums[i1].add(faceNormal);
            normalSums[i2] = normalSums[i2].add(faceNormal);
        }

        model.getNormals().clear();

        // Нормализация суммарных нормалей: получение нормали вершины.
        for (int i = 0; i < vertexCount; ++i) {
            Vector3D sum = normalSums[i];
            if (sum.length() < 1e-6f) {

                model.getNormals().add(new Vector3D(0.0f, 0.0f, 1.0f));
            } else {

                Vector3D n = sum.normalize();
                model.getNormals().add(new Vector3D(n.getX(), n.getY(), n.getZ()));
            }
        }

        // Привязка индексов нормалей: соответствие vertexIndices -> normalIndices.
        for (Polygon polygon : model.getPolygons()) {
            List<Integer> vIdx = polygon.getVertexIndices();
            ArrayList<Integer> nIdx = new ArrayList<>(vIdx.size());
            nIdx.addAll(vIdx);
            polygon.setNormalIndices(nIdx);
        }
    }
}
