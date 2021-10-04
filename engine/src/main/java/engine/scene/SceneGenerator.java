package engine.scene;

import engine.enums.TextureKey;
import engine.scene.delauny.DelaunyEdge;
import engine.scene.delauny.DelaunyTriangulator;
import org.joml.Vector2f;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class SceneGenerator {

    private static final int MAP_SIZE = 32;
    private static final int MAX_ROOM_SIDE_LENGTH = 16;
    private static final double MAX_ROOM_RATIO = 2;
    private static final double MAX_ROOM_SIZE = 128;
    private static final double MIN_ROOM_SIZE = 32;
    private static final int MAX_ROOM_AMOUNT = 32;
    public static final short CORRIDOR_SIZE = (short) (2);

    public static SceneTileMap generateScene() {
        SceneTileMap sceneTileMap = new SceneTileMap((short) MAP_SIZE);
        List<TileRoom> rooms = new ArrayList<>();
        generateRooms(rooms, MAX_ROOM_AMOUNT);
        List<TileRoom> mainRooms = getBiggestRoomsNotIntersecting(rooms, 8);
        List<DelaunyEdge> paths = DelaunyTriangulator.generateDelaunyGraph(
                mainRooms.stream().map(room -> new Vector2f(room.getRoomCenterTile())).collect(Collectors.toList()),
                MAP_SIZE);
        paths = MinimumSpanningTree.getMinimumSpanningTreeEdges(paths);
        List<TileRoom> corridors = generateCorridors(paths);
        mainRooms.forEach(sceneTileMap::applyTileRoom);
        corridors.forEach(sceneTileMap::applyTileRoom);
        sceneTileMap.setRoomPositions(mainRooms.stream().map(TileRoom::getRoomCenterTile).collect(Collectors.toList()));
        sceneTileMap.generateWalls();
        return sceneTileMap;
    }

    private static void generateRooms(List<TileRoom> rooms, int amount) {
        while (rooms.size() < amount) {
            TileRoom room = new TileRoom((short) (Math.random() * MAX_ROOM_SIDE_LENGTH),
                    (short) (Math.random() * MAX_ROOM_SIDE_LENGTH),
                    new Vector2i((int) (Math.random() * MAP_SIZE) + 1, (int) (Math.random() * MAP_SIZE) + 1),
                    TextureKey.FLOOR_RED_PLATES_DEBRIS
            );
            if (isValidRoom(room)) {
                rooms.add(room);
            }
        }
    }

    private static List<TileRoom> generateCorridors(List<DelaunyEdge> paths) {
        List<TileRoom> corridors = new ArrayList<>();
        for (DelaunyEdge path : paths) {
            int startX = vector2fToVector2i(getLeftVertex(path)).x();
            int endX = vector2fToVector2i(getRightVertex(path)).x();
            int fixedY = vector2fToVector2i(getLeftVertex(path)).y();
            for (int x = 0; x < endX - startX; x++) {
                corridors.add(new TileRoom(CORRIDOR_SIZE, CORRIDOR_SIZE, new Vector2i(startX + x, fixedY),
                        TextureKey.FLOOR_RED_PLATES_DEBRIS));
            }
            int startY = vector2fToVector2i(getBottomVertex(path)).y();
            int endY = vector2fToVector2i(getTopVertex(path)).y();
            int fixedX = vector2fToVector2i(getBottomVertex(path).equals(getLeftVertex(path)) ? getTopVertex(path) : getBottomVertex(path)).x();
            for (int y = 0; y < endY - startY; y++) {
                corridors.add(new TileRoom(CORRIDOR_SIZE, CORRIDOR_SIZE, new Vector2i(fixedX, startY + y),
                        TextureKey.FLOOR_RED_PLATES_DEBRIS));
            }
        }
        return corridors;
    }

    private static Vector2f getLeftVertex(DelaunyEdge path) {
        return path.getVertexA().x() < path.getVertexB().x() ? path.getVertexA() : path.getVertexB();
    }

    private static Vector2f getRightVertex(DelaunyEdge path) {
        return path.getVertexA().x() > path.getVertexB().x() ? path.getVertexA() : path.getVertexB();
    }

    private static Vector2f getBottomVertex(DelaunyEdge path) {
        return path.getVertexA().y() < path.getVertexB().y() ? path.getVertexA() : path.getVertexB();
    }

    private static Vector2f getTopVertex(DelaunyEdge path) {
        return path.getVertexA().y() > path.getVertexB().y() ? path.getVertexA() : path.getVertexB();
    }

    private static Vector2i vector2fToVector2i(Vector2f from) {
        return new Vector2i((short) from.x(), (short) from.y());
    }

    private static boolean isValidRoom(TileRoom room) {
        if (room.getRoomBottomLeftTile().x() + room.getRoomWidth() >= MAP_SIZE ||
                room.getRoomBottomLeftTile().y() + room.getRoomHeight() >= MAP_SIZE) {
            return false;
        }
        if ((double) room.getRoomWidth() / (double) room.getRoomHeight() > MAX_ROOM_RATIO) {
            return false;
        }
        double roomSize = room.getRoomWidth() * room.getRoomHeight();
        return !(roomSize > MAX_ROOM_SIZE) && !(roomSize < MIN_ROOM_SIZE);

    }

    private static List<TileRoom> getBiggestRoomsNotIntersecting(List<TileRoom> rooms, int amount) {
        List<TileRoom> biggesNotIntersectingRooms = new ArrayList<>();
        List<TileRoom> sortedRooms = rooms.stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());
        int count = 0;
        while (biggesNotIntersectingRooms.size() < amount) {
            if (count >= sortedRooms.size()) {
                break;
            }
            if (!isIntersectingAny(biggesNotIntersectingRooms, sortedRooms.get(count))) {
                biggesNotIntersectingRooms.add(sortedRooms.get(count));
            }
            count++;
        }
        return biggesNotIntersectingRooms;
    }

    private static boolean isIntersectingAny(List<TileRoom> targets, TileRoom roomToTest) {
        for (TileRoom target : targets) {
            boolean intersection = target.intersectsRoom(roomToTest);
            if (intersection) {
                return true;
            }
        }
        return false;
    }

}
