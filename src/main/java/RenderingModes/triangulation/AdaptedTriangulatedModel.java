package RenderingModes.triangulation;

import Interface.model.Model;
import Interface.model.Polygon;
import java.util.ArrayList;
import java.util.List;

/**
 * Адаптированная версия TriangulatedModel для работы с вашей структурой Model
 */
public class AdaptedTriangulatedModel {
    private final Model originalModel;
    private final Model triangulatedModel;
    
    public AdaptedTriangulatedModel(Model originalModel) {
        this.originalModel = originalModel;
        this.triangulatedModel = new Model();
        
        // Копируем вершины, текстурные координаты и нормали
        triangulatedModel.setVertices(new ArrayList<>(originalModel.getVertices()));
        triangulatedModel.setTextureVertices(new ArrayList<>(originalModel.getTextureVertices()));
        triangulatedModel.setNormals(new ArrayList<>(originalModel.getNormals()));
        
        // Триангулируем полигоны
        triangulatePolygons(originalModel.getPolygons());
    }
    
    private void triangulatePolygons(List<Polygon> originalPolygons) {
        List<Polygon> triangulatedPolygons = new ArrayList<>();
        
        for (Polygon polygon : originalPolygons) {
            triangulatePolygon(polygon, triangulatedPolygons);
        }
        
        triangulatedModel.setPolygons(new ArrayList<>(triangulatedPolygons));
    }
    
    private void triangulatePolygon(Polygon polygon, List<Polygon> result) {
        List<Integer> vertexIndices = polygon.getVertexIndices();
        List<Integer> textureIndices = polygon.getTextureVertexIndices();
        List<Integer> normalIndices = polygon.getNormalIndices();
        
        int vertexCount = vertexIndices.size();
        
        // Если уже треугольник - просто копируем
        if (vertexCount == 3) {
            result.add(createTrianglePolygon(vertexIndices, textureIndices, normalIndices));
            return;
        }
        
        // Веерная триангуляция от первой вершины
        for (int i = 1; i < vertexCount - 1; i++) {
            List<Integer> triangleVertexIndices = new ArrayList<>();
            triangleVertexIndices.add(vertexIndices.get(0));
            triangleVertexIndices.add(vertexIndices.get(i));
            triangleVertexIndices.add(vertexIndices.get(i + 1));
            
            List<Integer> triangleTextureIndices = new ArrayList<>();
            List<Integer> triangleNormalIndices = new ArrayList<>();
            
            // Текстурные координаты (если есть)
            if (textureIndices != null && !textureIndices.isEmpty() && textureIndices.size() == vertexCount) {
                triangleTextureIndices.add(textureIndices.get(0));
                triangleTextureIndices.add(textureIndices.get(i));
                triangleTextureIndices.add(textureIndices.get(i + 1));
            }
            
            // Нормали (если есть)
            if (normalIndices != null && !normalIndices.isEmpty() && normalIndices.size() == vertexCount) {
                triangleNormalIndices.add(normalIndices.get(0));
                triangleNormalIndices.add(normalIndices.get(i));
                triangleNormalIndices.add(normalIndices.get(i + 1));
            }
            
            result.add(createTrianglePolygon(triangleVertexIndices, triangleTextureIndices, triangleNormalIndices));
        }
    }
    
    private Polygon createTrianglePolygon(List<Integer> vertexIndices, 
                                         List<Integer> textureIndices, 
                                         List<Integer> normalIndices) {
        Polygon triangle = new Polygon();
        triangle.setVertexIndices(new ArrayList<>(vertexIndices));
        
        if (textureIndices != null && !textureIndices.isEmpty()) {
            triangle.setTextureVertexIndices(new ArrayList<>(textureIndices));
        }
        
        if (normalIndices != null && !normalIndices.isEmpty()) {
            triangle.setNormalIndices(new ArrayList<>(normalIndices));
        }
        
        return triangle;
    }
    
    public Model getTriangulatedModel() {
        return triangulatedModel;
    }
    
    public boolean isTriangulated() {
        for (Polygon polygon : triangulatedModel.getPolygons()) {
            if (polygon.getVertexIndices().size() != 3) {
                return false;
            }
        }
        return true;
    }
}
