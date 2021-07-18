package engine.exception;

public enum EngineExceptionMessageTemplate {
    MESH_NOT_FOUND_EXCEPTION("Error. No Mesh found for primitive key %s."),
    FILE_DOES_NOT_EXIST_EXCEPTION("Error, the requested file does not exist");

    public final String msg;
    EngineExceptionMessageTemplate(String msg){
        this.msg = msg;
    }
}
