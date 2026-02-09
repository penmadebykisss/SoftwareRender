package Interface.model;

import java.util.ArrayList;

public class Polygon {

    private ArrayList<Integer> vertexIndices;
    private ArrayList<Integer> textureVertexIndices;
    private ArrayList<Integer> normalIndices;

    public Polygon() {
        vertexIndices = new ArrayList<Integer>();
        textureVertexIndices = new ArrayList<Integer>();
        normalIndices = new ArrayList<Integer>();
    }

    private void validateIndices(ArrayList<Integer> indices, String name) {
        // Разрешаем null и пустые списки.
        // Ошибка только если данные есть, но их меньше 3.
        if (indices != null && !indices.isEmpty() && indices.size() < 3) {
            throw new IllegalArgumentException(name + " must have at least 3 elements, got: " + indices.size());
        }
    }

    public void setVertexIndices(ArrayList<Integer> vertexIndices) {
        if (vertexIndices == null) {
            throw new IllegalArgumentException("Vertex indices cannot be null");
        }
        validateIndices(vertexIndices, "Vertex indices");
        this.vertexIndices = vertexIndices;
    }

    public void setTextureVertexIndices(ArrayList<Integer> textureVertexIndices) {
        validateIndices(textureVertexIndices, "Texture vertex indices");
        this.textureVertexIndices = textureVertexIndices;
    }

    public void setNormalIndices(ArrayList<Integer> normalIndices) {
        validateIndices(normalIndices, "Normal indices");
        this.normalIndices = normalIndices;
    }

    public ArrayList<Integer> getVertexIndices() {
        return vertexIndices;
    }

    public ArrayList<Integer> getTextureVertexIndices() {
        return textureVertexIndices;
    }

    public ArrayList<Integer> getNormalIndices() {
        return normalIndices;
    }
}