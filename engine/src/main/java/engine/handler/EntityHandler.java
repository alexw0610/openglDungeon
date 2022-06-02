package engine.handler;

import engine.entity.Entity;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EntityHandler implements Handler<Entity> {
    private static final ThreadLocal<EntityHandler> INSTANCE = ThreadLocal.withInitial(EntityHandler::new);
    private final Map<String, Entity> objects = new HashMap<>();

    private EntityHandler() {
    }

    public static EntityHandler getInstance() {
        return INSTANCE.get();
    }

    public static void setInstance(EntityHandler entityHandler) {
        INSTANCE.set(entityHandler);
    }

    @Override
    public Entity getObject(String key) {
        synchronized (this.objects) {
            return objects.get(key);
        }
    }

    @Override
    public List<Entity> getAllObjects() {
        synchronized (this.objects) {
            return new ArrayList<>(this.objects.values());
        }
    }

    public Entity getEntityWithId(String entityId) {
        return this.objects.get(entityId);
    }

    public Entity getEntityWithComponent(Class component) {
        synchronized (this.objects) {
            return this.objects.values().parallelStream().unordered().filter(entity -> entity.hasComponentOfType(component)).findAny().orElse(null);
        }
    }

    public List<Entity> getAllEntitiesWithComponents(Class... components) {
        synchronized (this.objects) {
            List<Entity> entities = getAllObjects();
            for (Class component : components) {
                entities = entities.parallelStream().unordered().filter(entity -> entity.hasComponentOfType(component)).collect(Collectors.toList());
            }
            return entities;
        }
    }

    @Override
    public void addObject(String key, Entity object) {
        synchronized (this.objects) {
            object.setEntityId(key);
            this.objects.put(key, object);
        }
    }

    @Override
    public void addObject(Entity object) {
        synchronized (this.objects) {
            String key = RandomStringUtils.randomAlphanumeric(16);
            object.setEntityId(key);
            this.objects.put(key, object);
        }
    }

    @Override
    public void removeObject(String key) {
        synchronized (this.objects) {
            this.objects.remove(key);
        }
    }

    public void removeObjectsWithPrefix(String prefix) {
        for (String key : this.objects.keySet().stream().filter(key -> key.startsWith(prefix)).collect(Collectors.toList())) {
            removeObject(key);
        }
    }

    public List<Entity> getObjectsWithPrefix(String prefix) {
        List<Entity> entitiesToReturn = new ArrayList<>();
        synchronized (this.objects) {
            for (String key : this.objects.keySet().stream().filter(key -> key.startsWith(prefix)).collect(Collectors.toList())) {
                entitiesToReturn.add(this.objects.get(key));
            }
        }
        return entitiesToReturn;
    }

    public List<Entity> getGlobalObjects() {
        List<Entity> entitiesToReturn = new ArrayList<>();
        synchronized (this.objects) {
            for (String key : this.objects.keySet().stream().filter(key -> key.contains("_GLOBAL_")).collect(Collectors.toList())) {
                entitiesToReturn.add(this.objects.get(key));
            }
        }
        return entitiesToReturn;
    }

    public double getEntityCount() {
        return this.objects.size();
    }
}
