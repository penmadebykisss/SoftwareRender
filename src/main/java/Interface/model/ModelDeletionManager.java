/*
package Interface.model;

import Math.vector.Vector3D;
import Math.matrix.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ModelDeletionManager {


    public void deleteVertices(Model model, Set<Integer> verticesToDelete) {
        if (verticesToDelete.isEmpty()) {
            return;
        }

        List<Vector3D> vertices = model.getVertices();
        List<Polygon> polygons = model.getPolygons();

        // Создаём маппинг старых индексов в новые
        int[] indexMapping = new int[vertices.size()];
        int newIndex = 0;

        for (int oldIndex = 0; oldIndex < vertices.size(); oldIndex++) {
            if (verticesToDelete.contains(oldIndex)) {
                indexMapping[oldIndex] = -1; // Помечаем как удалённую
            } else {
                indexMapping[oldIndex] = newIndex++;
            }
        }

        // Удаляем вершины
        ArrayList<Vector3D> newVertices = new ArrayList<>();
        for (int i = 0; i < vertices.size(); i++) {
            if (!verticesToDelete.contains(i)) {
                newVertices.add(vertices.get(i));
            }
        }

        // Обновляем индексы в полигонах и удаляем невалидные полигоны
        ArrayList<Polygon> newPolygons = new ArrayList<>();
        for (Polygon polygon : polygons) {
            ArrayList<Integer> vertexIndices = polygon.getVertexIndices();
            ArrayList<Integer> newVertexIndices = new ArrayList<>();

            boolean validPolygon = true;
            for (Integer vertexIndex : vertexIndices) {
                if (indexMapping[vertexIndex] == -1) {
                    validPolygon = false;
                    break;
                }
                newVertexIndices.add(indexMapping[vertexIndex]);
            }

            if (validPolygon && newVertexIndices.size() >= 3) {
                Polygon newPolygon = new Polygon();
                newPolygon.setVertexIndices(newVertexIndices);

                // Обновляем текстурные координаты если есть
                if (polygon.getTextureVertexIndices() != null && !polygon.getTextureVertexIndices().isEmpty()) {
                    newPolygon.setTextureVertexIndices(polygon.getTextureVertexIndices());
                }

                // Обновляем нормали если есть
                if (polygon.getNormalIndices() != null && !polygon.getNormalIndices().isEmpty()) {
                    newPolygon.setNormalIndices(polygon.getNormalIndices());
                }

                newPolygons.add(newPolygon);
            }
        }

        model.setVertices(newVertices);
        model.setPolygons(newPolygons);
    }

    public void deletePolygons(Model model, Set<Integer> polygonsToDelete) {
        if (polygonsToDelete.isEmpty()) {
            return;
        }

        List<Polygon> polygons = model.getPolygons();
        ArrayList<Polygon> newPolygons = new ArrayList<>();

        for (int i = 0; i < polygons.size(); i++) {
            if (!polygonsToDelete.contains(i)) {
                newPolygons.add(polygons.get(i));
            }
        }

        model.setPolygons(newPolygons);

        // Опционально: удаляем неиспользуемые вершины
        // removeUnusedVertices(model);
    }

    public void removeUnusedVertices(Model model) {
        List<Vector3D> vertices = model.getVertices();
        List<Polygon> polygons = model.getPolygons();

        // Собираем индексы используемых вершин
        Set<Integer> usedVertices = new HashSet<>();
        for (Polygon polygon : polygons) {
            usedVertices.addAll(polygon.getVertexIndices());
        }

        // Создаём маппинг старых индексов в новые
        int[] indexMapping = new int[vertices.size()];
        int newIndex = 0;

        for (int oldIndex = 0; oldIndex < vertices.size(); oldIndex++) {
            if (usedVertices.contains(oldIndex)) {
                indexMapping[oldIndex] = newIndex++;
            } else {
                indexMapping[oldIndex] = -1;
            }
        }

        // Создаём новый список вершин
        ArrayList<Vector3D> newVertices = new ArrayList<>();
        for (int i = 0; i < vertices.size(); i++) {
            if (usedVertices.contains(i)) {
                newVertices.add(vertices.get(i));
            }
        }

        // Обновляем индексы в полигонах
        for (Polygon polygon : polygons) {
            ArrayList<Integer> vertexIndices = polygon.getVertexIndices();
            ArrayList<Integer> newVertexIndices = new ArrayList<>();

            for (Integer vertexIndex : vertexIndices) {
                newVertexIndices.add(indexMapping[vertexIndex]);
            }

            polygon.setVertexIndices(newVertexIndices);
        }

        model.setVertices(newVertices);
    }

    public int findNearestVertex(Model model, double screenX, double screenY,
                                 double centerX, double centerY,
                                 Matrix4x4 viewProjection) {
        List<Vector3D> vertices = model.getVertices();
        int nearestIndex = -1;
        double minDistance = Double.MAX_VALUE;
        double threshold = 15.0; // Максимальное расстояние для выбора в пикселях

        for (int i = 0; i < vertices.size(); i++) {
            Vector3D vertex = vertices.get(i);

            // Применяем матрицу вид-проекция
            Vector3D projected = viewProjection.multiply(vertex);

            // Проверяем, видима ли вершина
            if (Math.abs(projected.getX()) > 1.5f ||
                    Math.abs(projected.getY()) > 1.5f ||
                    projected.getZ() < 0.0f ||
                    projected.getZ() > 1.0f) {
                continue; // Вершина за пределами видимости
            }

            // Преобразуем в экранные координаты
            double x = centerX + (projected.getX() * centerX);
            double y = centerY - (projected.getY() * centerY);

            double dx = screenX - x;
            double dy = screenY - y;
            double distance = Math.sqrt(dx * dx + dy * dy);

            if (distance < minDistance && distance < threshold) {
                minDistance = distance;
                nearestIndex = i;
            }
        }

        return nearestIndex;
    }

    public int findPolygonAtPoint(Model model, double screenX, double screenY,
                                  double centerX, double centerY,
                                  Matrix4x4 viewProjection) {
        List<Polygon> polygons = model.getPolygons();
        List<Vector3D> vertices = model.getVertices();

        for (int i = 0; i < polygons.size(); i++) {
            Polygon polygon = polygons.get(i);
            ArrayList<Integer> vertexIndices = polygon.getVertexIndices();

            if (vertexIndices.size() < 3) {
                continue;
            }

            // Проецируем вершины полигона
            double[] xPoints = new double[vertexIndices.size()];
            double[] yPoints = new double[vertexIndices.size()];
            boolean allVisible = true;

            for (int j = 0; j < vertexIndices.size(); j++) {
                int vertexIndex = vertexIndices.get(j);
                if (vertexIndex >= 0 && vertexIndex < vertices.size()) {
                    Vector3D vertex = vertices.get(vertexIndex);

                    // Применяем матрицу вид-проекция
                    Vector3D projected = viewProjection.multiply(vertex);

                    // Проверяем видимость
                    if (Math.abs(projected.getX()) > 1.5f ||
                            Math.abs(projected.getY()) > 1.5f ||
                            projected.getZ() < 0.0f ||
                            projected.getZ() > 1.0f) {
                        allVisible = false;
                        break;
                    }

                    // Преобразуем в экранные координаты
                    xPoints[j] = centerX + (projected.getX() * centerX);
                    yPoints[j] = centerY - (projected.getY() * centerY);
                }
            }

            // Проверяем, находится ли точка внутри полигона (только если все вершины видимы)
            if (allVisible && isPointInPolygon(screenX, screenY, xPoints, yPoints)) {
                return i;
            }
        }

        return -1;
    }

    private boolean isPointInPolygon(double x, double y, double[] xPoints, double[] yPoints) {
        int n = xPoints.length;
        boolean inside = false;

        for (int i = 0, j = n - 1; i < n; j = i++) {
            if ((yPoints[i] > y) != (yPoints[j] > y) &&
                    x < (xPoints[j] - xPoints[i]) * (y - yPoints[i]) / (yPoints[j] - yPoints[i]) + xPoints[i]) {
                inside = !inside;
            }
        }

        return inside;
    }
}*/
package Interface.model;

import Math.vector.Vector3D;
import Math.matrix.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ModelDeletionManager {


    public void deleteVertices(Model model, Set<Integer> verticesToDelete) {
        if (verticesToDelete.isEmpty()) {
            return;
        }

        List<Vector3D> vertices = model.getVertices();
        List<Polygon> polygons = model.getPolygons();

        // Создаём маппинг старых индексов в новые
        int[] indexMapping = new int[vertices.size()];
        int newIndex = 0;

        for (int oldIndex = 0; oldIndex < vertices.size(); oldIndex++) {
            if (verticesToDelete.contains(oldIndex)) {
                indexMapping[oldIndex] = -1; // Помечаем как удалённую
            } else {
                indexMapping[oldIndex] = newIndex++;
            }
        }

        // Удаляем вершины
        ArrayList<Vector3D> newVertices = new ArrayList<>();
        for (int i = 0; i < vertices.size(); i++) {
            if (!verticesToDelete.contains(i)) {
                newVertices.add(vertices.get(i));
            }
        }

        // Обновляем индексы в полигонах и удаляем невалидные полигоны
        ArrayList<Polygon> newPolygons = new ArrayList<>();
        for (Polygon polygon : polygons) {
            ArrayList<Integer> vertexIndices = polygon.getVertexIndices();
            ArrayList<Integer> newVertexIndices = new ArrayList<>();

            boolean validPolygon = true;
            for (Integer vertexIndex : vertexIndices) {
                if (indexMapping[vertexIndex] == -1) {
                    validPolygon = false;
                    break;
                }
                newVertexIndices.add(indexMapping[vertexIndex]);
            }

            if (validPolygon && newVertexIndices.size() >= 3) {
                Polygon newPolygon = new Polygon();
                newPolygon.setVertexIndices(newVertexIndices);

                // Обновляем текстурные координаты если есть
                if (polygon.getTextureVertexIndices() != null && !polygon.getTextureVertexIndices().isEmpty()) {
                    newPolygon.setTextureVertexIndices(polygon.getTextureVertexIndices());
                }

                // Обновляем нормали если есть
                if (polygon.getNormalIndices() != null && !polygon.getNormalIndices().isEmpty()) {
                    newPolygon.setNormalIndices(polygon.getNormalIndices());
                }

                newPolygons.add(newPolygon);
            }
        }

        model.setVertices(newVertices);
        model.setPolygons(newPolygons);
    }

    public void deletePolygons(Model model, Set<Integer> polygonsToDelete) {
        if (polygonsToDelete.isEmpty()) {
            return;
        }

        List<Polygon> polygons = model.getPolygons();
        ArrayList<Polygon> newPolygons = new ArrayList<>();

        for (int i = 0; i < polygons.size(); i++) {
            if (!polygonsToDelete.contains(i)) {
                newPolygons.add(polygons.get(i));
            }
        }

        model.setPolygons(newPolygons);

        // Опционально: удаляем неиспользуемые вершины
        // removeUnusedVertices(model);
    }

    public void removeUnusedVertices(Model model) {
        List<Vector3D> vertices = model.getVertices();
        List<Polygon> polygons = model.getPolygons();

        // Собираем индексы используемых вершин
        Set<Integer> usedVertices = new HashSet<>();
        for (Polygon polygon : polygons) {
            usedVertices.addAll(polygon.getVertexIndices());
        }

        // Создаём маппинг старых индексов в новые
        int[] indexMapping = new int[vertices.size()];
        int newIndex = 0;

        for (int oldIndex = 0; oldIndex < vertices.size(); oldIndex++) {
            if (usedVertices.contains(oldIndex)) {
                indexMapping[oldIndex] = newIndex++;
            } else {
                indexMapping[oldIndex] = -1;
            }
        }

        // Создаём новый список вершин
        ArrayList<Vector3D> newVertices = new ArrayList<>();
        for (int i = 0; i < vertices.size(); i++) {
            if (usedVertices.contains(i)) {
                newVertices.add(vertices.get(i));
            }
        }

        // Обновляем индексы в полигонах
        for (Polygon polygon : polygons) {
            ArrayList<Integer> vertexIndices = polygon.getVertexIndices();
            ArrayList<Integer> newVertexIndices = new ArrayList<>();

            for (Integer vertexIndex : vertexIndices) {
                newVertexIndices.add(indexMapping[vertexIndex]);
            }

            polygon.setVertexIndices(newVertexIndices);
        }

        model.setVertices(newVertices);
    }

    public int findNearestVertex(Model model, double screenX, double screenY,
                                 double centerX, double centerY,
                                 Matrix4x4 viewProjection) {
        List<Vector3D> vertices = model.getVertices();
        int nearestIndex = -1;
        double minDistance = Double.MAX_VALUE;
        double threshold = 15.0; // Максимальное расстояние для выбора в пикселях

        for (int i = 0; i < vertices.size(); i++) {
            Vector3D vertex = vertices.get(i);

            // Применяем матрицу вид-проекция
            Vector3D projected = viewProjection.multiply(vertex);

            // Проверяем, видима ли вершина
            if (Math.abs(projected.getX()) > 1.5f ||
                    Math.abs(projected.getY()) > 1.5f ||
                    projected.getZ() < 0.0f ||
                    projected.getZ() > 1.0f) {
                continue; // Вершина за пределами видимости
            }

            // Преобразуем в экранные координаты
            double x = centerX + (projected.getX() * centerX);
            double y = centerY - (projected.getY() * centerY);

            double dx = screenX - x;
            double dy = screenY - y;
            double distance = Math.sqrt(dx * dx + dy * dy);

            if (distance < minDistance && distance < threshold) {
                minDistance = distance;
                nearestIndex = i;
            }
        }

        return nearestIndex;
    }

    public int findPolygonAtPoint(Model model, double screenX, double screenY,
                                  double centerX, double centerY,
                                  Matrix4x4 viewProjection) {
        List<Polygon> polygons = model.getPolygons();
        List<Vector3D> vertices = model.getVertices();

        int closestPolygonIndex = -1;
        double closestDepth = Double.MAX_VALUE;

        for (int i = 0; i < polygons.size(); i++) {
            Polygon polygon = polygons.get(i);
            ArrayList<Integer> vertexIndices = polygon.getVertexIndices();

            if (vertexIndices.size() < 3) {
                continue;
            }

            // Проецируем вершины полигона
            double[] xPoints = new double[vertexIndices.size()];
            double[] yPoints = new double[vertexIndices.size()];
            double[] zDepths = new double[vertexIndices.size()];
            boolean allVisible = true;

            for (int j = 0; j < vertexIndices.size(); j++) {
                int vertexIndex = vertexIndices.get(j);
                if (vertexIndex >= 0 && vertexIndex < vertices.size()) {
                    Vector3D vertex = vertices.get(vertexIndex);

                    // Применяем матрицу вид-проекция
                    Vector3D projected = viewProjection.multiply(vertex);

                    // Проверяем видимость
                    if (Math.abs(projected.getX()) > 1.5f ||
                            Math.abs(projected.getY()) > 1.5f ||
                            projected.getZ() < 0.0f ||
                            projected.getZ() > 1.0f) {
                        allVisible = false;
                        break;
                    }

                    // Преобразуем в экранные координаты
                    xPoints[j] = centerX + (projected.getX() * centerX);
                    yPoints[j] = centerY - (projected.getY() * centerY);
                    zDepths[j] = projected.getZ();
                }
            }

            // Проверяем, находится ли точка внутри полигона (только если все вершины видимы)
            if (allVisible && isPointInPolygon(screenX, screenY, xPoints, yPoints)) {
                // Вычисляем среднюю глубину полигона
                double avgDepth = 0;
                for (double depth : zDepths) {
                    avgDepth += depth;
                }
                avgDepth /= zDepths.length;

                // Выбираем полигон с наименьшей глубиной (ближайший к камере)
                if (avgDepth < closestDepth) {
                    closestDepth = avgDepth;
                    closestPolygonIndex = i;
                }
            }
        }

        return closestPolygonIndex;
    }

    private boolean isPointInPolygon(double x, double y, double[] xPoints, double[] yPoints) {
        int n = xPoints.length;
        boolean inside = false;

        for (int i = 0, j = n - 1; i < n; j = i++) {
            if ((yPoints[i] > y) != (yPoints[j] > y) &&
                    x < (xPoints[j] - xPoints[i]) * (y - yPoints[i]) / (yPoints[j] - yPoints[i]) + xPoints[i]) {
                inside = !inside;
            }
        }

        return inside;
    }
}