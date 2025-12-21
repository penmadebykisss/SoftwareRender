package RenderingModes.core;

public class ModelAdapter implements IRenderableModel {
    private final Interface.model.Model originalModel;
    private List<Triangle> triangles;
    private boolean normalsCalculated = false;

    public ModelAdapter(Interface.model.Model model) {
        this.originalModel = model;
        this.triangles = convertToTriangles(model);
    }

    private List<Triangle> convertToTriangles(Model model) {
        // Используем AdaptedTriangulator для триангуляции
        Model triangulated = TriangulationFacade.ensureTriangulated(model);
        return convertPolygonsToTriangles(triangulated.getPolygons());
    }
}