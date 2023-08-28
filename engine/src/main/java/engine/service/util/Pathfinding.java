package engine.service.util;

import engine.object.generation.World;
import org.joml.Vector2d;
import org.joml.Vector2i;

import java.util.*;

public class Pathfinding {

    private static final int MAX_OPEN_TILES = 80;
    private static final int MAX_CHECKED_TILES = 80;

    public List<Vector2i> getPath(World world, Vector2d start, Vector2d target) {
        Node startNode = new Node((int) Math.round(start.x()), (int) Math.round(start.y()));
        if (!world.isWalkable((int) Math.round(start.x()), (int) Math.round(start.y()))) {
            return Collections.emptyList();
        }
        if (!world.isWalkable((int) Math.round(target.x()), (int) Math.round(target.y()))) {
            return Collections.emptyList();
        }
        startNode.setG(0);
        startNode.setH(0);
        LinkedList<Node> openList = new LinkedList<>();
        openList.add(startNode);
        LinkedList<Node> closedList = new LinkedList<>();
        Node targetNode = getFinalNode(openList, closedList, world, new Vector2i((int) Math.round(target.x()), (int) Math.round(target.y())));
        if (targetNode == null) {
            return Collections.emptyList();
        }
        return getPathFromNode(targetNode);
    }

    private List<Vector2i> getPathFromNode(Node targetNode) {
        List<Vector2i> path = new ArrayList<>();
        return addPositionToPath(targetNode, path);
    }

    private List<Vector2i> addPositionToPath(Node targetNode, List<Vector2i> path) {
        if (targetNode.getParent() != null) {
            addPositionToPath(targetNode.getParent(), path);
            path.add(new Vector2i(targetNode.getX(), targetNode.getY()));
        }
        return path;
    }

    private Node getFinalNode(LinkedList<Node> openList, LinkedList<Node> closedList, World world, Vector2i target) {
        while (!openList.isEmpty()) {
            Node q = openList.stream().min(Comparator.comparingDouble(Node::getF)).get();
            openList.remove(q);
            closedList.add(q);
            if (q.getX() == target.x() && q.getY() == target.y()) {
                return q;
            }
            if (openList.size() > MAX_OPEN_TILES || closedList.size() > MAX_CHECKED_TILES) {
                return null;
            }
            LinkedList<Node> successors = generateSuccessors(q, world);
            for (Node successor : successors) {
                if (successor.getX() == target.x() && successor.getY() == target.y()) {
                    return successor;
                }
                successor.setG(q.g + (int) getManhattanDistance(new Vector2i(q.getX(), q.getY()), successor));
                successor.setH((int) getManhattanDistance(target, successor));
                Optional<Node> optionalExisting = openList.stream().filter(node -> node.getX() == successor.getX() && node.getY() == successor.getY()).findFirst();
                if (!optionalExisting.isPresent()) {
                    openList.add(successor);
                } else {
                    Node existing = optionalExisting.get();
                    if (existing.getF() > successor.getF()) {
                        openList.remove(existing);
                        openList.add(successor);
                    }
                }
            }
        }
        return null;
    }

    private double getManhattanDistance(Vector2i target, Node successor) {
        return Math.abs(target.x - successor.getX()) +
                Math.abs(target.y - successor.getY());
    }

    private LinkedList<Node> generateSuccessors(Node q, World world) {
        LinkedList<Node> successors = new LinkedList<>();
        boolean top = false;
        boolean right = false;
        boolean bottom = false;
        boolean left = false;
        if (world.isWalkable(q.getX(), q.getY() + 1)) {
            top = true;
            successors.add(new Node(q.getX(), q.getY() + 1, q));
        }
        if (world.isWalkable(q.getX() - 1, q.getY())) {
            left = true;
            successors.add(new Node(q.getX() - 1, q.getY(), q));
        }
        if (world.isWalkable(q.getX() + 1, q.getY())) {
            right = true;
            successors.add(new Node(q.getX() + 1, q.getY(), q));
        }
        if (world.isWalkable(q.getX(), q.getY() - 1)) {
            bottom = true;
            successors.add(new Node(q.getX(), q.getY() - 1, q));
        }
        if (top && right) {
            if (world.isWalkable(q.getX() + 1, q.getY() + 1)) {
                successors.add(new Node(q.getX() + 1, q.getY() + 1, q));
            }
        }
        if (right && bottom) {
            if (world.isWalkable(q.getX() + 1, q.getY() - 1)) {
                successors.add(new Node(q.getX() + 1, q.getY() - 1, q));
            }
        }
        if (bottom && left) {
            if (world.isWalkable(q.getX() - 1, q.getY() - 1)) {
                successors.add(new Node(q.getX() - 1, q.getY() - 1, q));
            }
        }
        if (left && top) {
            if (world.isWalkable(q.getX() - 1, q.getY() + 1)) {
                successors.add(new Node(q.getX() - 1, q.getY() + 1, q));
            }
        }
        return successors;
    }

    private static class Node {
        int x;
        int y;
        int g;
        int h;
        Node parent;

        public Node(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public Node(int x, int y, Node parent) {
            this.x = x;
            this.y = y;
            this.parent = parent;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        public int getG() {
            return g;
        }

        public void setG(int g) {
            this.g = g;
        }

        public int getH() {
            return h;
        }

        public void setH(int h) {
            this.h = h;
        }

        public int getF() {
            return getG() + getH();
        }

        public Node getParent() {
            return parent;
        }

        public void setParent(Node parent) {
            this.parent = parent;
        }
    }
}
