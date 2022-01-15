package engine.service;

import engine.handler.DungeonTemplateHandler;
import engine.handler.NavHandler;
import engine.loader.template.DungeonTemplate;
import engine.object.Edge;
import engine.object.Room;
import engine.object.TileMap;
import engine.service.util.MinimumSpanningTree;
import engine.service.util.Triangulator;
import org.joml.Random;
import org.joml.Vector2d;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DungeonGenerator {

    private static final int MAP_SIZE = 60;
    private static final int MAX_ROOM_SIDE_LENGTH = 12;
    private static final double MAX_ROOM_RATIO = 1.5;
    private static final int MAX_ROOM_SIZE = 96;
    private static final int MIN_ROOM_SIZE = 20;
    private static final int MAX_ROOM_AMOUNT = 32;
    private static final int CORRIDOR_SIZE = 2;

    public static Vector2d generate(long seed, String dungeonTemplate) {
        Random random = new Random(seed);
        List<Room> rooms = generateRooms(random, dungeonTemplate);
        List<Room> mainRooms = getBiggestRoomsNotIntersecting(rooms, 8);
        List<Edge> paths = Triangulator.triangulateVectorField(mainRooms.stream().map(room -> new Vector2d(room.getRoomPosition())).collect(Collectors.toList()), MAP_SIZE);
        paths = MinimumSpanningTree.getMinimumSpanningTreeEdges(paths);
        List<Room> corridors = generateCorridors(paths, random);
        rooms.removeAll(mainRooms);
        List<Room> sideRooms = getSideRooms(rooms, corridors, mainRooms, 8);
        TileMap tileMap = new TileMap(MAP_SIZE, seed);
        mainRooms.forEach(tileMap::addRoom);
        sideRooms.forEach(tileMap::addRoom);
        corridors.forEach(tileMap::addRoom);
        Room startRoom = mainRooms.stream()
                .sorted(Comparator.comparingInt(r -> r.getRoomPosition().x()))
                .sorted(Comparator.comparingInt(r -> r.getRoomPosition().y()))
                .findFirst().orElse(mainRooms.get(0));
        tileMap.initMap(random);
        tileMap.generateGlobalEntities(dungeonTemplate, mainRooms, random);
        NavHandler.getInstance().setNavMap(tileMap.getNavMap());
        return new Vector2d(startRoom.getRoomPosition().x(), startRoom.getRoomPosition().y());
    }

    private static List<Room> getSideRooms(List<Room> rooms, List<Room> corridors, List<Room> mainRooms, int amount) {
        List<Room> sideRooms = new ArrayList<>();
        boolean emptyPass = false;
        while (sideRooms.size() < amount && !emptyPass) {
            emptyPass = true;
            for (Room corridor : corridors) {
                for (Room room : rooms) {
                    if (corridor.intersectsRoom(room) && !isIntersectingAny(mainRooms, room) && !isIntersectingAny(sideRooms, room)) {
                        sideRooms.add(room);
                        emptyPass = false;
                        if (sideRooms.size() >= amount) {
                            return sideRooms;
                        }
                        break;
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

    private static List<Room> generateCorridors(List<Edge> paths, Random random) {
        List<Room> corridors = new ArrayList<>();
        for (Edge path : paths) {
            if (random.nextFloat() < 0.5) {
                generateCorridor(corridors, path, getLeftVertex(path), getTopVertex(path), getBottomVertex(path));
            } else {
                generateCorridor(corridors, path, getRightVertex(path), getBottomVertex(path), getTopVertex(path));
            }
        }
        return corridors;
    }

    private static void generateCorridor(List<Room> corridors, Edge path, Vector2d leftVertex, Vector2d topVertex, Vector2d bottomVertex) {
        //horizontal
        int startX;
        int endX;
        if (leftVertex.equals(getLeftVertex(path))) {
            //starting at
            startX = Vector2dToVector2i(getLeftVertex(path)).x();
            endX = Vector2dToVector2i(getRightVertex(path)).x() + 1;
        } else {
            //going toward
            startX = Vector2dToVector2i(getLeftVertex(path)).x() - 1;
            endX = Vector2dToVector2i(getRightVertex(path)).x();
        }
        int fixedY = Vector2dToVector2i(leftVertex).y();
        Room x = new Room(endX - startX, CORRIDOR_SIZE, new Vector2i(startX + ((endX - startX) / 2), fixedY), "default_hallway");
        corridors.add(x);
        //vertical
        int startY;
        int endY;
        if ((getBottomVertex(path).equals(getLeftVertex(path)) ? topVertex : bottomVertex).equals(getBottomVertex(path))) {
            //starting at
            startY = Vector2dToVector2i(getBottomVertex(path)).y();
            endY = Vector2dToVector2i(getTopVertex(path)).y() + 1;
        } else {
            //going toward
            startY = Vector2dToVector2i(getBottomVertex(path)).y() - 1;
            endY = Vector2dToVector2i(getTopVertex(path)).y();
        }
        int fixedX = Vector2dToVector2i(getBottomVertex(path).equals(getLeftVertex(path)) ? topVertex : bottomVertex).x();
        Room y = new Room(CORRIDOR_SIZE, endY - startY, new Vector2i(fixedX, startY + ((endY - startY) / 2)), "default_hallway");
        corridors.add(y);
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
