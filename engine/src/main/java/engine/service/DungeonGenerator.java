package engine.service;

import engine.handler.DungeonTemplateHandler;
import engine.handler.NavHandler;
import engine.loader.template.DungeonTemplate;
import engine.object.Edge;
import engine.object.Room;
import engine.object.TileMap;
import engine.service.util.MinimumSpanningTree;
import engine.service.util.Triangulator;
import org.joml.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DungeonGenerator {

    private static final int MAP_SIZE = 50;
    private static final int MAX_ROOM_SIDE_LENGTH = 12;
    private static final int MAX_ROOM_RATIO = 2;
    private static final int MAX_ROOM_SIZE = 128;
    private static final int MIN_ROOM_SIZE = 20;
    private static final int MAX_ROOM_AMOUNT = 32;
    private static final int CORRIDOR_SIZE = 2;

    public static List<Room> generate(double seed, String dungeonTemplate) {
        Random random = new Random((long) seed);
        List<Room> rooms = generateRooms(random, dungeonTemplate);
        List<Room> mainRooms = getBiggestRoomsNotIntersecting(rooms, 8);
        List<Edge> paths = Triangulator.triangulateVectorField(mainRooms.stream().map(room -> new Vector2d(room.getRoomPosition())).collect(Collectors.toList()), MAP_SIZE);
        paths = MinimumSpanningTree.getMinimumSpanningTreeEdges(paths);
        List<Room> corridors = generateCorridors(paths, random);
        rooms.removeAll(mainRooms);
        List<Room> sideRooms = getSideRooms(rooms, paths, mainRooms, 4);
        TileMap tileMap = new TileMap(MAP_SIZE);
        mainRooms.forEach(tileMap::addRoom);
        sideRooms.forEach(tileMap::addRoom);
        corridors.forEach(tileMap::addRoom);
        tileMap.initMap(random);
        NavHandler.getInstance().setNavMap(tileMap.getNavMap());
        return mainRooms;
    }

    private static List<Room> getSideRooms(List<Room> rooms, List<Edge> paths, List<Room> mainRooms, int amount) {
        List<Room> sideRooms = new ArrayList<>();
        for (Room room : rooms) {
            for (Edge edge : paths) {
                if (Intersectiond.intersectLineCircle(
                        edge.getA().x(), edge.getA().y(),
                        edge.getB().x(), edge.getB().y(),
                        room.getRoomPosition().x(),
                        room.getRoomPosition().y(),
                        room.getRoomHeight() < room.getRoomWidth() ? (room.getRoomWidth() / 2) : (room.getRoomHeight() / 2),
                        new Vector3d())) {
                    if (!isIntersectingAny(mainRooms, room) && !isIntersectingAny(sideRooms, room))
                        sideRooms.add(room);
                    if (sideRooms.size() >= amount) {
                        return sideRooms;
                    }
                }
            }
        }
        return sideRooms;
    }

    private static List<Room> generateRooms(Random random, String template) {
        List<Room> rooms = new ArrayList<>();
        DungeonTemplate dungeonTemplate = DungeonTemplateHandler.getInstance().getObject(template);
        while (rooms.size() < DungeonGenerator.MAX_ROOM_AMOUNT) {
            float rnd = random.nextFloat();
            String roomTemplateKey = "default_room";
            float total = 0;
            for (Map.Entry<String, Double> entry : dungeonTemplate.getDungeonRoomTemplates().entrySet()) {
                total += entry.getValue();
                if (rnd <= total) {
                    roomTemplateKey = entry.getKey();
                    break;
                }
            }
            Room room = new Room((short) (random.nextFloat() * MAX_ROOM_SIDE_LENGTH),
                    (short) (random.nextFloat() * MAX_ROOM_SIDE_LENGTH),
                    new Vector2i((int) (random.nextFloat() * MAP_SIZE) + 1, (int) (random.nextFloat() * MAP_SIZE) + 1),
                    roomTemplateKey);
            if (isValidRoom(room)) {
                rooms.add(room);
            }
        }
        return rooms;
    }

    //TODO: improve
    private static List<Room> generateCorridors(List<Edge> paths, Random random) {
        List<Room> corridors = new ArrayList<>();
        for (Edge path : paths) {
            if (random.nextFloat() < 0.5) {
                int startX = Vector2dToVector2i(getLeftVertex(path)).x();
                int endX = Vector2dToVector2i(getRightVertex(path)).x();
                int fixedY = Vector2dToVector2i(getLeftVertex(path)).y();
                Room x = new Room(endX - startX, CORRIDOR_SIZE, new Vector2i(startX + ((endX - startX) / 2), fixedY), "default_hallway");
                corridors.add(x);
                int startY = Vector2dToVector2i(getBottomVertex(path)).y();
                int endY = Vector2dToVector2i(getTopVertex(path)).y();
                int fixedX = Vector2dToVector2i(getBottomVertex(path).equals(getLeftVertex(path)) ? getTopVertex(path) : getBottomVertex(path)).x();
                Room y = new Room(CORRIDOR_SIZE, endY - startY, new Vector2i(fixedX, startY + ((endY - startY) / 2)), "default_hallway");
                corridors.add(y);
            } else {
                int startX = Vector2dToVector2i(getLeftVertex(path)).x();
                int endX = Vector2dToVector2i(getRightVertex(path)).x();
                int fixedY = Vector2dToVector2i(getRightVertex(path)).y();
                Room x = new Room(endX - startX, CORRIDOR_SIZE, new Vector2i(startX + ((endX - startX) / 2), fixedY), "default_hallway");
                corridors.add(x);
                int startY = Vector2dToVector2i(getBottomVertex(path)).y();
                int endY = Vector2dToVector2i(getTopVertex(path)).y();
                int fixedX = Vector2dToVector2i(getBottomVertex(path).equals(getLeftVertex(path)) ? getBottomVertex(path) : getTopVertex(path)).x();
                Room y = new Room(CORRIDOR_SIZE, endY - startY, new Vector2i(fixedX, startY + ((endY - startY) / 2)), "default_hallway");
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
        if (room == null) {
            return false;
        }
        if (room.getRoomBottomLeft().x() <= 0
                || room.getRoomBottomLeft().y() <= 0) {
            return false;
        }
        if (room.getRoomBottomLeft().x() + room.getRoomWidth() >= MAP_SIZE
                || room.getRoomBottomLeft().y() + room.getRoomHeight() >= MAP_SIZE) {
            return false;
        }
        if (room.getRoomWidth() / room.getRoomHeight() > MAX_ROOM_RATIO) {
            return false;
        }
        double roomSize = room.getRoomWidth() * room.getRoomHeight();
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
