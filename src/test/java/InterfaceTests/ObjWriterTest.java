package Interface.objwriter;

import Math.vector.Vector2D;
import Math.vector.Vector3D;
import Interface.model.Model;
import Interface.model.Polygon;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ObjWriterTest {

    @TempDir
    Path tempDir;

    @Test
    void testModelToStringSimpleModel() {
        Model model = new Model();
        model.getVertices().add(new Vector3D(1.0f, 2.0f, 3.0f));
        model.getVertices().add(new Vector3D(4.0f, 5.0f, 6.0f));
        model.getVertices().add(new Vector3D(7.0f, 8.0f, 9.0f));

        Polygon polygon = new Polygon();
        polygon.setVertexIndices(List.of(0, 1, 2));
        model.getPolygons().add(polygon);

        String result = ObjWriter.modelToString(model);

        String expected = """
                # Exported by Lapin Nikita ObjWriter
                v 1 2 3
                v 4 5 6
                v 7 8 9
                
                f 1 2 3
                """;

        assertEquals(normalizeLineEndings(expected), normalizeLineEndings(result));
    }

    @Test
    void testModelToStringWithComment() {
        Model model = new Model();
        model.getVertices().add(new Vector3D(1.0f, 0.0f, 0.0f));

        String result = ObjWriter.modelToString(model, "Custom Comment");

        assertTrue(result.startsWith("# Custom Comment"));
        assertTrue(result.contains("v 1 0 0"));
    }

    @Test
    void testModelToStringWithTextureVertices() {
        Model model = new Model();
        model.getVertices().add(new Vector3D(0.0f, 0.0f, 0.0f));
        model.getVertices().add(new Vector3D(1.0f, 0.0f, 0.0f));
        model.getVertices().add(new Vector3D(0.0f, 1.0f, 0.0f));

        model.getTextureVertices().add(new Vector2D(0.0f, 0.0f));
        model.getTextureVertices().add(new Vector2D(1.0f, 0.0f));
        model.getTextureVertices().add(new Vector2D(0.0f, 1.0f));

        Polygon polygon = new Polygon();
        polygon.setVertexIndices(List.of(0, 1, 2));
        polygon.setTextureVertexIndices(List.of(0, 1, 2));
        model.getPolygons().add(polygon);

        String result = ObjWriter.modelToString(model);

        assertTrue(result.contains("vt 0 0"));
        assertTrue(result.contains("vt 1 0"));
        assertTrue(result.contains("vt 0 1"));
        assertTrue(result.contains("f 1/1 2/2 3/3"));
    }

    @Test
    void testModelToStringWithNormals() {
        Model model = new Model();
        model.getVertices().add(new Vector3D(0.0f, 0.0f, 0.0f));
        model.getVertices().add(new Vector3D(1.0f, 0.0f, 0.0f));
        model.getVertices().add(new Vector3D(0.0f, 1.0f, 0.0f));

        model.getNormals().add(new Vector3D(0.0f, 0.0f, 1.0f));
        model.getNormals().add(new Vector3D(0.0f, 0.0f, 1.0f));
        model.getNormals().add(new Vector3D(0.0f, 0.0f, 1.0f));

        Polygon polygon = new Polygon();
        polygon.setVertexIndices(List.of(0, 1, 2));
        polygon.setNormalIndices(List.of(0, 1, 2));
        model.getPolygons().add(polygon);

        String result = ObjWriter.modelToString(model);

        assertTrue(result.contains("vn 0 0 1"));
        assertTrue(result.contains("f 1//1 2//2 3//3"));
    }

    @Test
    void testModelToStringWithAllAttributes() {
        Model model = new Model();
        model.getVertices().add(new Vector3D(0.0f, 0.0f, 0.0f));
        model.getVertices().add(new Vector3D(1.0f, 0.0f, 0.0f));
        model.getVertices().add(new Vector3D(0.0f, 1.0f, 0.0f));

        model.getTextureVertices().add(new Vector2D(0.0f, 0.0f));
        model.getTextureVertices().add(new Vector2D(1.0f, 0.0f));
        model.getTextureVertices().add(new Vector2D(0.0f, 1.0f));

        model.getNormals().add(new Vector3D(0.0f, 0.0f, 1.0f));
        model.getNormals().add(new Vector3D(0.0f, 0.0f, 1.0f));
        model.getNormals().add(new Vector3D(0.0f, 0.0f, 1.0f));

        Polygon polygon = new Polygon();
        polygon.setVertexIndices(List.of(0, 1, 2));
        polygon.setTextureVertexIndices(List.of(0, 1, 2));
        polygon.setNormalIndices(List.of(0, 1, 2));
        model.getPolygons().add(polygon);

        String result = ObjWriter.modelToString(model);

        assertTrue(result.contains("v 0 0 0"));
        assertTrue(result.contains("vt 0 0"));
        assertTrue(result.contains("vn 0 0 1"));
        assertTrue(result.contains("f 1/1/1 2/2/2 3/3/3"));
    }

    @Test
    void testModelToStringQuadPolygon() {
        Model model = new Model();
        model.getVertices().add(new Vector3D(0.0f, 0.0f, 0.0f));
        model.getVertices().add(new Vector3D(1.0f, 0.0f, 0.0f));
        model.getVertices().add(new Vector3D(1.0f, 1.0f, 0.0f));
        model.getVertices().add(new Vector3D(0.0f, 1.0f, 0.0f));

        Polygon polygon = new Polygon();
        polygon.setVertexIndices(List.of(0, 1, 2, 3));
        model.getPolygons().add(polygon);

        String result = ObjWriter.modelToString(model);

        assertTrue(result.contains("f 1 2 3 4"));
    }

    @Test
    void testFormatFloatCompact() {
        assertEquals("1", ObjWriter.formatFloatCompact(1.0f));
        assertEquals("1.5", ObjWriter.formatFloatCompact(1.5f));
        assertEquals("1.5", ObjWriter.formatFloatCompact(1.500000f));
        assertEquals("0", ObjWriter.formatFloatCompact(0.0f));
        assertEquals("-1.5", ObjWriter.formatFloatCompact(-1.5f));
        assertEquals("0.000001", ObjWriter.formatFloatCompact(0.000001f));
    }

    @Test
    void testFormatFloatCompactWithNaN() {
        assertThrows(ObjWriterException.class, () -> ObjWriter.formatFloatCompact(Float.NaN));
    }

    @Test
    void testFormatFloatCompactWithInfinity() {
        assertThrows(ObjWriterException.class, () -> ObjWriter.formatFloatCompact(Float.POSITIVE_INFINITY));
        assertThrows(ObjWriterException.class, () -> ObjWriter.formatFloatCompact(Float.NEGATIVE_INFINITY));
    }

    @Test
    void testWriteToFile(@TempDir Path tempDir) throws IOException {
        Model model = new Model();
        model.getVertices().add(new Vector3D(1.0f, 2.0f, 3.0f));

        Polygon polygon = new Polygon();
        polygon.setVertexIndices(List.of(0));
        model.getPolygons().add(polygon);

        Path filePath = tempDir.resolve("test.obj");
        ObjWriter.write(model, filePath.toString());

        assertTrue(Files.exists(filePath));
        String content = Files.readString(filePath);
        assertTrue(content.contains("v 1 2 3"));
    }

    @Test
    void testValidateVertex() {
        // Valid vertex
        ObjWriter.validateVertex(new Vector3D(1.0f, 2.0f, 3.0f), 0);

        // Null vertex
        assertThrows(ObjWriterException.class, () ->
                ObjWriter.validateVertex(null, 0));

        // Vertex with NaN
        assertThrows(ObjWriterException.class, () ->
                ObjWriter.validateVertex(new Vector3D(Float.NaN, 2.0f, 3.0f), 0));

        // Vertex with Infinity
        assertThrows(ObjWriterException.class, () ->
                ObjWriter.validateVertex(new Vector3D(Float.POSITIVE_INFINITY, 2.0f, 3.0f), 0));
    }

    @Test
    void testValidateTextureVertex() {
        // Valid texture vertex
        ObjWriter.validateTextureVertex(new Vector2D(0.5f, 0.5f), 0);

        // Null texture vertex
        assertThrows(ObjWriterException.class, () ->
                ObjWriter.validateTextureVertex(null, 0));
    }

    @Test
    void testValidateNormal() {
        // Valid normal
        ObjWriter.validateNormal(new Vector3D(0.0f, 0.0f, 1.0f), 0);

        // Null normal
        assertThrows(ObjWriterException.class, () ->
                ObjWriter.validateNormal(null, 0));
    }

    @Test
    void testValidatePolygon() {
        // Valid polygon
        Polygon polygon = new Polygon();
        polygon.setVertexIndices(List.of(0, 1, 2));

        ObjWriter.validatePolygon(polygon, 0, 3, 0, 0);

        // Null polygon
        assertThrows(ObjWriterException.class, () ->
                ObjWriter.validatePolygon(null, 0, 3, 0, 0));

        // Polygon with null vertex indices
        Polygon poly2 = new Polygon();
        assertThrows(ObjWriterException.class, () ->
                ObjWriter.validatePolygon(poly2, 0, 3, 0, 0));

        // Polygon with empty vertex indices
        Polygon poly3 = new Polygon();
        poly3.setVertexIndices(new ArrayList<>());
        assertThrows(ObjWriterException.class, () ->
                ObjWriter.validatePolygon(poly3, 0, 3, 0, 0));

        // Polygon with less than 3 vertices
        Polygon poly4 = new Polygon();
        poly4.setVertexIndices(List.of(0, 1));
        assertThrows(ObjWriterException.class, () ->
                ObjWriter.validatePolygon(poly4, 0, 3, 0, 0));

        // Polygon with invalid vertex index
        Polygon poly5 = new Polygon();
        poly5.setVertexIndices(List.of(0, 1, 5)); // 5 is out of bounds
        assertThrows(ObjWriterException.class, () ->
                ObjWriter.validatePolygon(poly5, 0, 3, 0, 0));
    }

    @Test
    void testValidatePolygonWithTextureVertices() {
        Polygon polygon = new Polygon();
        polygon.setVertexIndices(List.of(0, 1, 2));
        polygon.setTextureVertexIndices(List.of(0, 1, 2));

        // Valid case
        ObjWriter.validatePolygon(polygon, 0, 3, 3, 0);

        // Mismatched counts
        Polygon poly2 = new Polygon();
        poly2.setVertexIndices(List.of(0, 1, 2));
        poly2.setTextureVertexIndices(List.of(0, 1)); // Only 2 instead of 3
        assertThrows(ObjWriterException.class, () ->
                ObjWriter.validatePolygon(poly2, 0, 3, 3, 0));

        // Invalid texture vertex index
        Polygon poly3 = new Polygon();
        poly3.setVertexIndices(List.of(0, 1, 2));
        poly3.setTextureVertexIndices(List.of(0, 1, 5)); // 5 is out of bounds
        assertThrows(ObjWriterException.class, () ->
                ObjWriter.validatePolygon(poly3, 0, 3, 3, 0));
    }

    @Test
    void testValidatePolygonWithNormals() {
        Polygon polygon = new Polygon();
        polygon.setVertexIndices(List.of(0, 1, 2));
        polygon.setNormalIndices(List.of(0, 1, 2));

        // Valid case
        ObjWriter.validatePolygon(polygon, 0, 3, 0, 3);

        // Invalid normal index
        Polygon poly2 = new Polygon();
        poly2.setVertexIndices(List.of(0, 1, 2));
        poly2.setNormalIndices(List.of(0, 1, 5)); // 5 is out of bounds
        assertThrows(ObjWriterException.class, () ->
                ObjWriter.validatePolygon(poly2, 0, 3, 0, 3));
    }

    @Test
    void testModelToStringNullModel() {
        assertThrows(ObjWriterException.class, () -> ObjWriter.modelToString(null));
    }

    @Test
    void testModelToStringWithNullElements() {
        Model model = new Model();
        model.getVertices().add(null);

        assertThrows(ObjWriterException.class, () -> ObjWriter.modelToString(model));
    }

    @Test
    void testRoundTrip() {
        // Создаем модель
        String originalObj = """
                # Test model
                v 1.0 2.0 3.0
                v 4.0 5.0 6.0
                v 7.0 8.0 9.0
                vt 0.0 0.0
                vt 1.0 0.0
                vt 0.0 1.0
                vn 0.0 0.0 1.0
                vn 0.0 1.0 0.0
                vn 1.0 0.0 0.0
                f 1/1/1 2/2/2 3/3/3
                """;

        // Читаем модель
        Model model = ObjReader.read(originalObj);

        // Записываем модель обратно
        String writtenObj = ObjWriter.modelToString(model, "Test model");

        // Проверяем, что ключевые элементы присутствуют
        assertTrue(writtenObj.contains("v 1 2 3"));
        assertTrue(writtenObj.contains("vt 0 0"));
        assertTrue(writtenObj.contains("vn 0 0 1"));
        assertTrue(writtenObj.contains("f 1/1/1 2/2/2 3/3/3"));
    }

    @Test
    void testModelToStringWithEmptyCollections() {
        Model model = new Model();
        model.getVertices().add(new Vector3D(1.0f, 2.0f, 3.0f));

        Polygon polygon = new Polygon();
        polygon.setVertexIndices(List.of(0));
        model.getPolygons().add(polygon);

        // Добавляем пустые коллекции
        model.getTextureVertices().clear();
        model.getNormals().clear();

        String result = ObjWriter.modelToString(model);

        // Проверяем, что пустые коллекции не вызывают ошибок
        assertTrue(result.contains("v 1 2 3"));
        assertFalse(result.contains("vt"));
        assertFalse(result.contains("vn"));
    }

    private String normalizeLineEndings(String text) {
        return text.replace("\r\n", "\n").replace("\r", "\n");
    }
}