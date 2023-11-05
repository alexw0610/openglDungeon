package engine.handler;

import engine.component.internal.CreatedAtComponent;
import engine.entity.Entity;
import engine.object.generation.World;
import engine.service.RenderService;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class EntityHandler implements Handler<Entity> {
    private static final ThreadLocal<EntityHandler> INSTANCE = ThreadLocal.withInitial(EntityHandler::new);
    private final Map<String, Entity> objects = new HashMap<>();

    private World world;

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
        return new ArrayList<>(this.objects.values());

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
        List<Entity> entities = getAllObjects();
        for (Class component : components) {
            entities = entities.stream().filter(entity -> entity.hasComponentOfType(component)).collect(Collectors.toList());
        }
        return entities;
    }

    public List<Entity> getAllEntitiesWithAnyOfComponents(Class... components) {
        List<Entity> entities = new LinkedList<>();
        for (Class component : components) {
            entities.addAll(getAllObjects()
                    .stream()
                    .filter(entity -> !entities.contains(entity))
                    .filter(entity -> entity.hasComponentOfType(component))
                    .collect(Collectors.toList()));
        }
        return entities;
    }

    @Override
    public void addObject(String key, Entity object) {
        synchronized (this.objects) {
            object.setEntityId(key);
            object.addComponent(new CreatedAtComponent(RenderService.renderTick));
            this.objects.put(key, object);
        }
    }

    @Override
    public void addObject(Entity object) {
        synchronized (this.objects) {
            String key = RandomStringUtils.randomAlphanumeric(16);
            object.setEntityId(key);
            object.addComponent(new CreatedAtComponent(RenderService.renderTick));
            this.objects.put(key, object);
        }
    }

    @Override
    public void removeObject(String key) {
        synchronized (this.objects) {
            if (this.objects.containsKey(key)) {
                this.objects.get(key).onRemove();
            }
            this.objects.remove(key);
        }
    }

    public void removeObjectsWithPrefix(String prefix) {
        for (String key : this.objects.keySet().stream().filter(key -> key.startsWith(prefix)).collect(Collectors.toList())) {
            removeObject(key);
        }
    }

    public void removeAllObjectsWithoutPrefix(String... prefix) {
        for (String key : this.objects.keySet().stream().filter(key -> Arrays.stream(prefix).noneMatch(key::startsWith)).collect(Collectors.toList())) {
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

    public double getEntityCount() {
        return this.objects.size();
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public void cleanup() {
        Iterator<Entity> iterator = this.objects.values().iterator();
        while (iterator.hasNext()) {
            Entity entity = iterator.next();
            entity.onRemove();
        }
        this.objects.clear();
    }
}
