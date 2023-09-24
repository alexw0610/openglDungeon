package engine.component;

import java.io.Serializable;

public interface Component extends Serializable {
    default boolean isLocal() {
        return true;
    }

    public void onRemove();
}
