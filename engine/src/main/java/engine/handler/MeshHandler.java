package engine.handler;

import engine.enums.PrimitiveMeshShape;
import engine.object.Mesh;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class MeshHandler {
    private static final MeshHandler INSTANCE = new MeshHandler();
    private final Map<String, Mesh> meshes = new HashMap<>();

    private MeshHandler() {
        for (PrimitiveMeshShape primitiveMeshShape : PrimitiveMeshShape.values()) {
            meshes.put(primitiveMeshShape.value(), new Mesh(primitiveMeshShape));
        }
        meshes.forEach((primitive, mesh) -> mesh.loadMesh());
    }

    public static MeshHandler getInstance() {
        return INSTANCE;
    }

    public void addMesh(String key, Mesh mesh) {
        Mesh prev = this.meshes.put(key, mesh);
        mesh.loadMesh();
        if (prev != null) {
            prev.unload();
        }
    }

    public void removeMesh(String key) {
        if (this.meshes.containsKey(key)) {
            this.meshes.get(key).unload();
            this.meshes.remove(key);
        }
    }

    public void removeMeshesWithPrefix(String prefix) {
        for (String key : this.meshes.keySet().stream().filter(key -> key.startsWith(prefix)).collect(Collectors.toList())) {
            removeMesh(key);
        }
    }

    public Mesh getMeshForKey(String key) {
        return meshes.get(key);
    }

    public Mesh getMeshForKey(PrimitiveMeshShape primitiveMeshShape) {
        return meshes.get(primitiveMeshShape.value());
    }
}
