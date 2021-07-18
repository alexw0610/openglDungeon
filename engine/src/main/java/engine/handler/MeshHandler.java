package engine.handler;

import engine.enumeration.PrimitiveMeshShape;
import engine.exception.MeshNotFoundException;
import engine.object.Mesh;

import java.util.HashMap;
import java.util.Map;

import static engine.exception.EngineExceptionMessageTemplate.MESH_NOT_FOUND_EXCEPTION;

public class MeshHandler {
    public static final MeshHandler MESH_HANDLER = new MeshHandler();

    private Map<PrimitiveMeshShape, Mesh> primitiveMeshShapes = new HashMap<>();

    private MeshHandler() {
        for (PrimitiveMeshShape primitiveMeshShape : PrimitiveMeshShape.values()) {
            primitiveMeshShapes.put(primitiveMeshShape, new Mesh(primitiveMeshShape));
        }
        primitiveMeshShapes.forEach((primitiveMeshShape, mesh) -> mesh.loadMesh());
    }

    public Mesh getMeshForKey(PrimitiveMeshShape primitiveMeshShape) throws MeshNotFoundException {
        if (primitiveMeshShapes.containsKey(primitiveMeshShape)) {
            return primitiveMeshShapes.get(primitiveMeshShape);
        } else {
            throw new MeshNotFoundException(MESH_NOT_FOUND_EXCEPTION.msg, null, primitiveMeshShape.name());
        }
    }

}
