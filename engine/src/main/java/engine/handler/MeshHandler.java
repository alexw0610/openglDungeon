package engine.handler;

import engine.enums.PrimitiveMeshShape;
import engine.object.Mesh;

import java.util.HashMap;
import java.util.Map;

public class MeshHandler {
    private static final MeshHandler INSTANCE = new MeshHandler();
    private final Map<String, Mesh> meshes = new HashMap<>();

    private MeshHandler() {
        for (PrimitiveMeshShape primitiveMeshShape : PrimitiveMeshShape.values()) {
            meshes.put(primitiveMeshShape.getKey(), new Mesh(primitiveMeshShape));
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

    public Mesh getMeshForKey(String key) {
        return meshes.get(key);
    }

    public Mesh getMeshForKey(PrimitiveMeshShape primitiveMeshShape) {
        return meshes.get(primitiveMeshShape.getKey());
    }
}
