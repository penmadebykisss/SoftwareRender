package Interface.model;


import Math.vector.Vector3D;
import Math.vector.Vector2D;

import java.util.ArrayList;

public class Model {
    private ArrayList<Vector3D> vertices = new ArrayList<Vector3D>();
    private ArrayList<Vector2D> textureVertices = new ArrayList<Vector2D>();
    private ArrayList<Vector3D> normals = new ArrayList<Vector3D>();
    private ArrayList<Polygon> polygons = new ArrayList<Polygon>();

    public ArrayList<Vector3D> getVertices() {
        return vertices;
    }

    public void setVertices(ArrayList<Vector3D> vertices) {
        this.vertices = vertices;
    }

    public ArrayList<Vector2D> getTextureVertices() {
        return textureVertices;
    }

    public void setTextureVertices(ArrayList<Vector2D> textureVertices) {
        this.textureVertices = textureVertices;
    }

    public ArrayList<Vector3D> getNormals() {
        return normals;
    }

    public void setNormals(ArrayList<Vector3D> normals) {
        this.normals = normals;
    }

    public ArrayList<Polygon> getPolygons() {
        return polygons;
    }

    public void setPolygons(ArrayList<Polygon> polygons) {
        this.polygons = polygons;
    }
}