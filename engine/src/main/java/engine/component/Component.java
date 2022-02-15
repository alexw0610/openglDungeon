package engine.component;

import java.io.Serializable;

public interface Component extends Serializable {
    default boolean isServerSide() {
        return false;
    }
}
