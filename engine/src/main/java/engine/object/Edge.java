package engine.object;

import org.joml.Vector3d;

import java.util.Objects;

public class Edge {
    private final Vector3d a;
    private final Vector3d b;

    public Edge(Vector3d a, Vector3d b) {
        this.a = a;
        this.b = b;
    }

    public Vector3d getA() {
        return a;
    }

    public Vector3d getB() {
        return b;
    }

    public Mesh toMesh() {
        float[] vertices = new float[]{(float) a.x(), (float) a.y(), (float) a.z(),
                (float) b.x(), (float) b.y(), (float) b.z()};
        int[] indices = new int[]{0, 1};
        return new Mesh(vertices, indices, vertices);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge edge = (Edge) o;
        return (Objects.equals(a, edge.a) &&
                Objects.equals(b, edge.b)) || (Objects.equals(a, edge.b) &&
                Objects.equals(b, edge.a));
    }

    @Override
    public int hashCode() {
        return Objects.hash(a, b);
    }
}