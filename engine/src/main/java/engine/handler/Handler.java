package engine.handler;

import java.util.List;

public interface Handler<T> {

    T getObject(String key);

    List<T> getAllObjects();

    void addObject(String key, T object);

    void addObject(T object);

    void removeObject(String key);

    void cleanup();
}
