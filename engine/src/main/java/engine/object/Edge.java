package engine.object;

import org.joml.Vector2d;

import java.util.Objects;

public class Edge {
    private final Vector2d a;
    private final Vector2d b;

    public Edge(Vector2d a, Vector2d b) {
        this.a = a;
        this.b = b;
    }

    public Vector2d getA() {
        return a;
    }

    public Vector2d getB() {
        return b;
    }

    public Edge addVector(Vector2d vector2d) {
        this.a.add(vector2d);
        this.b.add(vector2d);
        return this;
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