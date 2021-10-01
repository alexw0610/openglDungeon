package engine.handler;

import engine.entity.Entity;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityHandler implements Handler<Entity> {

    private static final EntityHandler INSTANCE = new EntityHandler();
    private final Map<String, Entity> objects = new HashMap<>();

    private EntityHandler() {
    }

    public static EntityHandler getInstance() {
        return INSTANCE;
    }

    @Override
    public Entity getObject(String key) {
        return objects.get(key);
    }

    @Override
    public List<Entity> getAllObjects() {
        return new ArrayList<>(this.objects.values());
    }

    @Override
    public void addObject(String key, Entity object) {
        this.objects.put(key, object);
    }

    @Override
    public void addObject(Entity object) {
        this.objects.put(RandomStringUtils.randomAlphanumeric(16), object);
    }

    @Override
    public void removeObject(String key) {
        this.objects.remove(key);
    }
}
