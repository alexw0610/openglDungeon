package engine.service;

import engine.enums.TextureKey;
import engine.handler.NavHandler;
import engine.object.Edge;
import engine.object.Room;
import engine.object.TileMap;
import engine.service.util.MinimumSpanningTree;
import engine.service.util.Triangulator;
import org.joml.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class DungeonGenerator {

    private static final int MAP_SIZE = 50;
    private static final int MAX_ROOM_SIDE_LENGTH = 12;
    private static final int MAX_ROOM_RATIO = 2;
    private static final int MAX_ROOM_SIZE = 128;
    private static final int MIN_ROOM_SIZE = 20;
    private static final int MAX_ROOM_AMOUNT = 32;
    private static final int CORRIDOR_SIZE = 2;

    public static List<Room> generate(double seed) {
        Random random = new Random((long) seed);
        List<Room> rooms = generateRooms(random);
        List<Room> mainRooms = getBiggestRoomsNotIntersecting(rooms, 8);
        List<Edge> paths = Triangulator.triangulateVectorField(mainRooms.stream().map(room -> new Vector2d(room.getRoomPosition())).collect(Collectors.toList()), MAP_SIZE);
        paths = MinimumSpanningTree.getMinimumSpanningTreeEdges(paths);
        List<Room> corridors = generateCorridors(paths, random);
        rooms.removeAll(mainRooms);
        List<Room> sideRooms = getSideRooms(rooms, paths, 4);
        TileMap tileMap = new TileMap(MAP_SIZE);
        mainRooms.forEach(tileMap::addRoom);
        sideRooms.forEach(tileMap::addRoom);
        corridors.forEach(tileMap::addRoom);
        tileMap.initMap(random);
        NavHandler.getInstance().setNavMap(tileMap.getNavMap());
        return mainRooms;
    }

    private static List<Room> getSideRooms(List<Room> rooms, List<Edge> paths, int amount) {
        List<Room> sideRooms = new ArrayList<>();
        for (Room room : rooms) {
            for (Edge edge : paths) {
                if (Intersectiond.intersectLineCircle(
                        edge.getA().x(), edge.getA().y(),
                        edge.getB().x(), edge.getB().y(),
                        room.getRoomPosition().x(),
                        room.getRoomPosition().y(),
                        room.getHeight() < room.getWidth() ? (room.getWidth() / 2) : (room.getHeight() / 2),
                        new Vector3d())) {
                    sideRooms.add(room);
                    if (sideRooms.size() >= amount) {
                        return sideRooms;
                    }
                }
            }
        }
        return sideRooms;
    }

    private static List<Room> generateRooms(Random random) {
        List<Room> rooms = new ArrayList<>();
        while (rooms.size() < DungeonGenerator.MAX_ROOM_AMOUNT) {
            Room room;
            if (random.nextFloat() < 0.1) {
                room = new Room((short) (random.nextFloat() * MAX_ROOM_SIDE_LENGTH),
                        (short) (random.nextFloat() * MAX_ROOM_SIDE_LENGTH),
                        new Vector2i((int) (random.nextFloat() * MAP_SIZE) + 1, (int) (random.nextFloat() * MAP_SIZE) + 1),
                        TextureKey.FLOOR_PURPLE_GREY_PLATES
                );
            } else {
                room = new Room((short) (random.nextFloat() * MAX_ROOM_SIDE_LENGTH),
                        (short) (random.nextFloat() * MAX_ROOM_SIDE_LENGTH),
                        new Vector2i((int) (random.nextFloat() * MAP_SIZE) + 1, (int) (random.nextFloat() * MAP_SIZE) + 1),
                        TextureKey.FLOOR_RED_PLATES_DEBRIS
                );
            }
            if (isValidRoom(room)) {
                rooms.add(room);
            }
        }
        return rooms;
    }

    private static List<Room> generateCorridors(List<Edge> paths, Random random) {
        List<Room> corridors = new ArrayList<>();
        for (Edge path : paths) {
            if (random.nextFloat() < 0.5) {
                int startX = Vector2dToVector2i(getLeftVertex(path)).x();
                int endX = Vector2dToVector2i(getRightVertex(path)).x();
                int fixedY = Vector2dToVector2i(getLeftVertex(path)).y();
                Room x = new Room(endX - startX, CORRIDOR_SIZE, new Vector2i(startX + ((endX - startX) / 2), fixedY), TextureKey.FLOOR_RED_PLATES_DEBRIS);
                corridors.add(x);
                int startY = Vector2dToVector2i(getBottomVertex(path)).y();
                int endY = Vector2dToVector2i(getTopVertex(path)).y();
                int fixedX = Vector2dToVector2i(getBottomVertex(path).equals(getLeftVertex(path)) ? getTopVertex(path) : getBottomVertex(path)).x();
                Room y = new Room(CORRIDOR_SIZE, endY - startY, new Vector2i(fixedX, startY + ((endY - startY) / 2)), TextureKey.FLOOR_RED_PLATES_DEBRIS);
                corridors.add(y);
            } else {
                int startX = Vector2dToVector2i(getLeftVertex(path)).x();
                int endX = Vector2dToVector2i(getRightVertex(path)).x();
                int fixedY = Vector2dToVector2i(getRightVertex(path)).y();
                Room x = new Room(endX - startX, CORRIDOR_SIZE, new Vector2i(startX + ((endX - startX) / 2), fixedY), TextureKey.FLOOR_RED_PLATES_DEBRIS);
                corridors.add(x);
                int startY = Vector2dToVector2i(getBottomVertex(path)).y();
                int endY = Vector2dToVector2i(getTopVertex(path)).y();
                int fixedX = Vector2dToVector2i(getBottomVertex(path).equals(getLeftVertex(path)) ? getBottomVertex(path) : getTopVertex(path)).x();
                Room y = new Room(CORRIDOR_SIZE, endY - startY, new Vector2i(fixedX, startY + ((endY - startY) / 2)), TextureKey.FLOOR_RED_PLATES_DEBRIS);
                corridors.add(y);
            }
        }
        return corridors;
    }

    private static Vector2d getLeftVertex(Edge path) {
        return path.getA().x() < path.getB().x() ? path.getA() : path.getB();
    }

    private static Vector2d getRightVertex(Edge path) {
        return path.getA().x() > path.getB().x() ? path.getA() : path.getB();
    }

    private static Vector2d getBottomVertex(Edge path) {
        return path.getA().y() < path.getB().y() ? path.getA() : path.getB();
    }

    private static Vector2d getTopVertex(Edge path) {
        return path.getA().y() > path.getB().y() ? path.getA() : path.getB();
    }

    private static Vector2i Vector2dToVector2i(Vector2d from) {
        return new Vector2i((short) from.x(), (short) from.y());
    }

    private static boolean isValidRoom(Room room) {
        if (room.getRoomBottomLeft().x() <= 0
                || room.getRoomBottomLeft().y() <= 0) {
            return false;
        }
        if (room.getRoomBottomLeft().x() + room.getWidth() >= MAP_SIZE
                || room.getRoomBottomLeft().y() + room.getHeight() >= MAP_SIZE) {
            return false;
        }
        if (room.getWidth() / room.getHeight() > MAX_ROOM_RATIO) {
            return false;
        }
        double roomSize = room.getWidth() * room.getHeight();
        return !(roomSize > MAX_ROOM_SIZE) && !(roomSize < MIN_ROOM_SIZE);
    }

    private static List<Room> getBiggestRoomsNotIntersecting(List<Room> rooms, int amount) {
        List<Room> selection = new ArrayList<>();
        List<Room> sortedRooms = rooms.stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());
        int count = 0;
        while (selection.size() < amount) {
            if (count >= sortedRooms.size()) {
                break;
            }
            if (!isIntersectingAny(selection, sortedRooms.get(count))) {
                selection.add(sortedRooms.get(count));
            }
            count++;
        }
        return selection;
    }

    private static boolean isIntersectingAny(List<Room> targets, Room roomToTest) {
        for (Room target : targets) {
            boolean intersection = target.intersectsRoom(roomToTest);
            if (intersection) {
                return true;
            }
        }
        return false;
    }
}
