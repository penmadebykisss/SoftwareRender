package InterfaceTests;

import Interface.objreader.ObjReader;
import Interface.objreader.ObjReaderException;
import Math.vector.Vector2D;
import Math.vector.Vector3D;
import Interface.model.Model;
import Interface.model.Polygon;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ObjReaderTest {

    @Test
    void testReadSimpleModel() {
        String objContent = """
                v 1.0 2.0 3.0
                v 4.0 5.0 6.0
                v 7.0 8.0 9.0
                f 1 2 3
                """;

        Model model = ObjReader.read(objContent);

        assertEquals(3, model.getVertices().size());
        assertEquals(new Vector3D(1.0f, 2.0f, 3.0f), model.getVertices().get(0));
        assertEquals(new Vector3D(4.0f, 5.0f, 6.0f), model.getVertices().get(1));
        assertEquals(new Vector3D(7.0f, 8.0f, 9.0f), model.getVertices().get(2));

        assertEquals(1, model.getPolygons().size());
        Polygon polygon = model.getPolygons().get(0);
        assertEquals(List.of(0, 1, 2), polygon.getVertexIndices());
    }

    @Test
    void testReadModelWithTextureVertices() {
        String objContent = """
                v 1.0 0.0 0.0
                v 0.0 1.0 0.0
                v 0.0 0.0 1.0
                vt 0.0 0.0
                vt 1.0 0.0
                vt 0.0 1.0
                f 1/1 2/2 3/3
                """;

        Model model = ObjReader.read(objContent);

        assertEquals(3, model.getVertices().size());
        assertEquals(3, model.getTextureVertices().size());
        assertEquals(new Vector2D(0.0f, 0.0f), model.getTextureVertices().get(0));
        assertEquals(new Vector2D(1.0f, 0.0f), model.getTextureVertices().get(1));
        assertEquals(new Vector2D(0.0f, 1.0f), model.getTextureVertices().get(2));

        Polygon polygon = model.getPolygons().get(0);
        assertEquals(List.of(0, 1, 2), polygon.getVertexIndices());
        assertEquals(List.of(0, 1, 2), polygon.getTextureVertexIndices());
    }

    @Test
    void testReadModelWithNormals() {
        String objContent = """
                v 1.0 0.0 0.0
                v 0.0 1.0 0.0
                v 0.0 0.0 1.0
                vn 0.0 0.0 1.0
                vn 0.0 1.0 0.0
                vn 1.0 0.0 0.0
                f 1//1 2//2 3//3
                """;

        Model model = ObjReader.read(objContent);

        assertEquals(3, model.getVertices().size());
        assertEquals(3, model.getNormals().size());
        assertEquals(new Vector3D(0.0f, 0.0f, 1.0f), model.getNormals().get(0));

        Polygon polygon = model.getPolygons().get(0);
        assertEquals(List.of(0, 1, 2), polygon.getVertexIndices());
        assertEquals(List.of(0, 1, 2), polygon.getNormalIndices());
    }

    @Test
    void testReadModelWithAllAttributes() {
        String objContent = """
                v 0.0 0.0 0.0
                v 1.0 0.0 0.0
                v 0.0 1.0 0.0
                vt 0.0 0.0
                vt 1.0 0.0
                vt 0.0 1.0
                vn 0.0 0.0 1.0
                vn 0.0 0.0 1.0
                vn 0.0 0.0 1.0
                f 1/1/1 2/2/2 3/3/3
                """;

        Model model = ObjReader.read(objContent);

        assertEquals(3, model.getVertices().size());
        assertEquals(3, model.getTextureVertices().size());
        assertEquals(3, model.getNormals().size());

        Polygon polygon = model.getPolygons().get(0);
        assertEquals(List.of(0, 1, 2), polygon.getVertexIndices());
        assertEquals(List.of(0, 1, 2), polygon.getTextureVertexIndices());
        assertEquals(List.of(0, 1, 2), polygon.getNormalIndices());
    }

    @Test
    void testReadQuadPolygon() {
        String objContent = """
                v 0.0 0.0 0.0
                v 1.0 0.0 0.0
                v 1.0 1.0 0.0
                v 0.0 1.0 0.0
                f 1 2 3 4
                """;

        Model model = ObjReader.read(objContent);

        assertEquals(4, model.getVertices().size());
        Polygon polygon = model.getPolygons().get(0);
        assertEquals(List.of(0, 1, 2, 3), polygon.getVertexIndices());
    }

    @Test
    void testIgnoreCommentsAndEmptyLines() {
        String objContent = """
                # This is a comment
                
                v 1.0 2.0 3.0
                
                # Another comment
                v 4.0 5.0 6.0
                
                f 1 2
                """;

        Model model = ObjReader.read(objContent);

        assertEquals(2, model.getVertices().size());
        assertEquals(1, model.getPolygons().size());
    }

    @Test
    void testParseVertexInvalidArguments() {
        String objContent = "v 1.0 2.0";

        assertThrows(ObjReaderException.class, () -> ObjReader.read(objContent));
    }

    @Test
    void testParseVertexInvalidNumberFormat() {
        String objContent = "v 1.0 abc 3.0";

        assertThrows(ObjReaderException.class, () -> ObjReader.read(objContent));
    }

    @Test
    void testParseTextureVertexInvalidArguments() {
        String objContent = "vt 0.5";

        assertThrows(ObjReaderException.class, () -> ObjReader.read(objContent));
    }

    @Test
    void testParseNormalInvalidArguments() {
        String objContent = "vn 0.0 1.0";

        assertThrows(ObjReaderException.class, () -> ObjReader.read(objContent));
    }

    @Test
    void testParseFaceWithInvalidFormat() {
        String objContent = """
                v 1.0 0.0 0.0
                f 1/2/3/4
                """;

        assertThrows(ObjReaderException.class, () -> ObjReader.read(objContent));
    }

    @Test
    void testParseFaceWithInvalidIndices() {
        String objContent = """
                v 1.0 0.0 0.0
                f abc
                """;

        assertThrows(ObjReaderException.class, () -> ObjReader.read(objContent));
    }

    @Test
    void testParseFaceWithTooFewVertices() {
        String objContent = """
                v 1.0 0.0 0.0
                v 0.0 1.0 0.0
                f 1 2
                """;

        assertThrows(ObjReaderException.class, () -> ObjReader.read(objContent));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "f 1/2 2/3 3/4", // Не хватает вершин
            "f 1//2 2//3",   // Не хватает вершин с нормалями
            "f 1/2/3 2/3/4"  // Не хватает вершин со всеми атрибутами
    })
    void testParseFaceWithInsufficientVertices(String faceLine) {
        String objContent = """
                v 1.0 0.0 0.0
                v 0.0 1.0 0.0
                v 0.0 0.0 1.0
                vt 0.0 0.0
                vt 1.0 0.0
                vt 0.0 1.0
                vn 0.0 0.0 1.0
                vn 0.0 1.0 0.0
                vn 1.0 0.0 0.0
                """ + faceLine;

        assertThrows(ObjReaderException.class, () -> ObjReader.read(objContent));
    }

    @Test
    void testParseFaceWithMixedFormats() {
        String objContent = """
                v 1.0 0.0 0.0
                v 0.0 1.0 0.0
                v 0.0 0.0 1.0
                vt 0.0 0.0
                vt 1.0 0.0
                vn 0.0 0.0 1.0
                f 1/1 2 3//1
                """;

        // Этот тест проверяет, что метод может обрабатывать смешанные форматы
        // В реальности это может быть недопустимо, зависит от требований
        Model model = ObjReader.read(objContent);
        assertNotNull(model);
        assertEquals(1, model.getPolygons().size());
    }

    @Test
    void testParseFaceWordEdgeCases() {
        // Тест пустого индекса текстурных координат
        String objContent1 = """
                v 1.0 0.0 0.0
                v 0.0 1.0 0.0
                v 0.0 0.0 1.0
                vn 0.0 0.0 1.0
                f 1//1 2//1 3//1
                """;

        Model model1 = ObjReader.read(objContent1);
        assertNotNull(model1);
        assertEquals(1, model1.getPolygons().size());
        assertEquals(3, model1.getPolygons().get(0).getNormalIndices().size());
    }
}