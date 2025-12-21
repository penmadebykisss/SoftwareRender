package RenderingModes.triangulation;

import Interface.model.Model;

/**
 * Фасад для триангуляции. Используется в рендер-пайплайне.
 */
public class TriangulationFacade {
    
    /**
     * Триангулирует модель (если еще не триангулирована)
     */
    public static Model ensureTriangulated(Model model) {
        if (AdaptedTriangulator.isTriangulated(model)) {
            return model; // Уже триангулирована
        }
        return AdaptedTriangulator.triangulate(model);
    }
    
    /**
     * Проверяет, триангулирована ли модель
     */
    public static boolean checkIfTriangulated(Model model) {
        return AdaptedTriangulator.isTriangulated(model);
    }
    
    /**
     * Принудительная триангуляция (даже если уже триангулирована)
     */
    public static Model forceTriangulate(Model model) {
        return AdaptedTriangulator.triangulate(model);
    }
}
