package engine.service.util;

import org.joml.Vector2d;

public class VectorUtil {

    public static Vector2d rotateVector(Vector2d vector, double n) {
        double rx = (vector.x() * Math.cos(n)) - (vector.y() * Math.sin(n));
        double ry = (vector.x() * Math.sin(n)) + (vector.y() * Math.cos(n));
        vector.x = rx;
        vector.y = ry;
        return vector;
    }
}
