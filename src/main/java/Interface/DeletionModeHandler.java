package Interface;

import Interface.model.Model;
import Interface.model.ModelDeletionManager;
import Math.matrix.*;
import Math.vector.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;

import java.util.HashSet;
import java.util.Set;

/**
 * Обработчик режима удаления элементов модели
 */
public class DeletionModeHandler {

    public enum DeletionMode {
        NONE,
        VERTEX,
        POLYGON
    }

    private DeletionMode currentMode = DeletionMode.NONE;
    private ModelDeletionManager deletionManager;

    // Выбранные элементы для удаления
    private Set<Integer> selectedVertices = new HashSet<>();
    private Set<Integer> selectedPolygons = new HashSet<>();

    // Для визуализации
    private int hoveredVertex = -1;
    private int hoveredPolygon = -1;

    // Параметры проекции (копируются из SceneRenderer)
    private double centerX;
    private double centerY;
    private Matrix4x4 viewProjection;

    private Runnable onSelectionChanged;

    public DeletionModeHandler() {
        this.deletionManager = new ModelDeletionManager();
    }

    /**
     * Устанавливает режим удаления
     */
    public void setMode(DeletionMode mode) {
        this.currentMode = mode;
        clearSelection();
    }

    /**
     * Возвращает текущий режим
     */
    public DeletionMode getMode() {
        return currentMode;
    }

    /**
     * Проверяет, активен ли режим удаления
     */
    public boolean isActive() {
        return currentMode != DeletionMode.NONE;
    }

    /**
     * Устанавливает callback для уведомления об изменении выбора
     */
    public void setOnSelectionChanged(Runnable callback) {
        this.onSelectionChanged = callback;
    }

    /**
     * Обновляет параметры проекции (включая матрицу вид-проекция)
     */
    public void updateProjection(double centerX, double centerY, Matrix4x4 viewProjection) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.viewProjection = viewProjection;
    }

    /**
     * Обрабатывает клик мыши
     */
    public void handleMouseClick(Model model, double x, double y, boolean isShiftPressed) {
        if (currentMode == DeletionMode.NONE || model == null || viewProjection == null) {
            return;
        }

        if (currentMode == DeletionMode.VERTEX) {
            int vertexIndex = deletionManager.findNearestVertex(model, x, y, centerX, centerY, viewProjection);
            if (vertexIndex != -1) {
                if (isShiftPressed) {
                    // Shift - добавляем/убираем из выбора
                    if (selectedVertices.contains(vertexIndex)) {
                        selectedVertices.remove(vertexIndex);
                    } else {
                        selectedVertices.add(vertexIndex);
                    }
                } else {
                    // Без Shift - выбираем только эту вершину
                    selectedVertices.clear();
                    selectedVertices.add(vertexIndex);
                }
                notifySelectionChanged();
            }
        } else if (currentMode == DeletionMode.POLYGON) {
            int polygonIndex = deletionManager.findPolygonAtPoint(model, x, y, centerX, centerY, viewProjection);
            if (polygonIndex != -1) {
                if (isShiftPressed) {
                    if (selectedPolygons.contains(polygonIndex)) {
                        selectedPolygons.remove(polygonIndex);
                    } else {
                        selectedPolygons.add(polygonIndex);
                    }
                } else {
                    selectedPolygons.clear();
                    selectedPolygons.add(polygonIndex);
                }
                notifySelectionChanged();
            }
        }
    }

    /**
     * Обрабатывает движение мыши (для подсветки)
     */
    public void handleMouseMove(Model model, double x, double y) {
        if (currentMode == DeletionMode.NONE || model == null || viewProjection == null) {
            hoveredVertex = -1;
            hoveredPolygon = -1;
            return;
        }

        if (currentMode == DeletionMode.VERTEX) {
            hoveredVertex = deletionManager.findNearestVertex(model, x, y, centerX, centerY, viewProjection);
            hoveredPolygon = -1;
        } else if (currentMode == DeletionMode.POLYGON) {
            hoveredPolygon = deletionManager.findPolygonAtPoint(model, x, y, centerX, centerY, viewProjection);
            hoveredVertex = -1;
        }
    }

    /**
     * Удаляет выбранные элементы
     */
    public boolean deleteSelected(Model model) {
        if (model == null) {
            return false;
        }

        boolean deleted = false;

        if (currentMode == DeletionMode.VERTEX && !selectedVertices.isEmpty()) {
            deletionManager.deleteVertices(model, selectedVertices);
            selectedVertices.clear();
            deleted = true;
        } else if (currentMode == DeletionMode.POLYGON && !selectedPolygons.isEmpty()) {
            deletionManager.deletePolygons(model, selectedPolygons);
            selectedPolygons.clear();
            deleted = true;
        }

        if (deleted) {
            notifySelectionChanged();
        }

        return deleted;
    }

    /**
     * Очищает выбор
     */
    public void clearSelection() {
        selectedVertices.clear();
        selectedPolygons.clear();
        hoveredVertex = -1;
        hoveredPolygon = -1;
        notifySelectionChanged();
    }

    /**
     * Выбирает все элементы текущего типа
     */
    public void selectAll(Model model) {
        if (model == null || currentMode == DeletionMode.NONE) {
            return;
        }

        if (currentMode == DeletionMode.VERTEX) {
            selectedVertices.clear();
            for (int i = 0; i < model.getVertices().size(); i++) {
                selectedVertices.add(i);
            }
        } else if (currentMode == DeletionMode.POLYGON) {
            selectedPolygons.clear();
            for (int i = 0; i < model.getPolygons().size(); i++) {
                selectedPolygons.add(i);
            }
        }

        notifySelectionChanged();
    }

    /**
     * Отрисовывает подсветку выбранных и наведённых элементов
     */
    public void renderSelection(GraphicsContext gc, Model model) {
        if (currentMode == DeletionMode.NONE || model == null || viewProjection == null) {
            return;
        }

        if (currentMode == DeletionMode.VERTEX) {
            // Рисуем выбранные вершины
            gc.setFill(Color.RED);
            for (Integer vertexIndex : selectedVertices) {
                if (vertexIndex >= 0 && vertexIndex < model.getVertices().size()) {
                    var vertex = model.getVertices().get(vertexIndex);

                    // Применяем матрицу вид-проекция
                    Vector3D projected = viewProjection.multiply(vertex);

                    // Проверяем видимость
                    if (Math.abs(projected.getX()) > 1.5f ||
                            Math.abs(projected.getY()) > 1.5f ||
                            projected.getZ() < 0.0f ||
                            projected.getZ() > 1.0f) {
                        continue;
                    }

                    double x = centerX + (projected.getX() * centerX);
                    double y = centerY - (projected.getY() * centerY);
                    gc.fillOval(x - 5, y - 5, 10, 10);
                }
            }

            // Рисуем наведённую вершину
            if (hoveredVertex != -1 && hoveredVertex < model.getVertices().size()) {
                var vertex = model.getVertices().get(hoveredVertex);

                // Применяем матрицу вид-проекция
                Vector3D projected = viewProjection.multiply(vertex);

                // Проверяем видимость
                if (Math.abs(projected.getX()) <= 1.5f &&
                        Math.abs(projected.getY()) <= 1.5f &&
                        projected.getZ() >= 0.0f &&
                        projected.getZ() <= 1.0f) {

                    gc.setStroke(Color.YELLOW);
                    gc.setLineWidth(2.0);
                    double x = centerX + (projected.getX() * centerX);
                    double y = centerY - (projected.getY() * centerY);
                    gc.strokeOval(x - 7, y - 7, 14, 14);
                }
            }
        } else if (currentMode == DeletionMode.POLYGON) {
            // Рисуем выбранные полигоны
            gc.setFill(Color.rgb(255, 0, 0, 0.3));
            for (Integer polygonIndex : selectedPolygons) {
                renderPolygonHighlight(gc, model, polygonIndex, Color.rgb(255, 0, 0, 0.3));
            }

            // Рисуем наведённый полигон
            if (hoveredPolygon != -1) {
                renderPolygonHighlight(gc, model, hoveredPolygon, Color.rgb(255, 255, 0, 0.3));
            }
        }
    }

    /**
     * Отрисовывает подсветку полигона
     */
    private void renderPolygonHighlight(GraphicsContext gc, Model model, int polygonIndex, Color color) {
        if (polygonIndex < 0 || polygonIndex >= model.getPolygons().size()) {
            return;
        }

        var polygon = model.getPolygons().get(polygonIndex);
        var vertexIndices = polygon.getVertexIndices();

        if (vertexIndices.size() < 3) {
            return;
        }

        double[] xPoints = new double[vertexIndices.size()];
        double[] yPoints = new double[vertexIndices.size()];
        boolean allVisible = true;

        for (int i = 0; i < vertexIndices.size(); i++) {
            int vertexIndex = vertexIndices.get(i);
            if (vertexIndex >= 0 && vertexIndex < model.getVertices().size()) {
                var vertex = model.getVertices().get(vertexIndex);

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

                xPoints[i] = centerX + (projected.getX() * centerX);
                yPoints[i] = centerY - (projected.getY() * centerY);
            }
        }

        if (allVisible) {
            gc.setFill(color);
            gc.fillPolygon(xPoints, yPoints, vertexIndices.size());
        }
    }

    /**
     * Возвращает количество выбранных элементов
     */
    public int getSelectionCount() {
        if (currentMode == DeletionMode.VERTEX) {
            return selectedVertices.size();
        } else if (currentMode == DeletionMode.POLYGON) {
            return selectedPolygons.size();
        }
        return 0;
    }

    /**
     * Уведомляет об изменении выбора
     */
    private void notifySelectionChanged() {
        if (onSelectionChanged != null) {
            onSelectionChanged.run();
        }
    }
}