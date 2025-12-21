package RenderingModes.triangulation;

import Interface.model.Model;

/**
 * Адаптированный Triangulator для работы с вашей структурой Model
 */
public class AdaptedTriangulator {
    
    public static Model triangulate(Model originalModel) {
        AdaptedTriangulatedModel adaptedModel = new AdaptedTriangulatedModel(originalModel);
        return adaptedModel.getTriangulatedModel();
    }
    
    public static boolean isTriangulated(Model model) {
        for (Interface.model.Polygon polygon : model.getPolygons()) {
            if (polygon.getVertexIndices().size() != 3) {
                return false;
            }
        }
        return true;
    }
}
