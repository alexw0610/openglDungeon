package engine.handler;

import engine.component.Component;
import engine.enums.EventType;
import engine.object.Event;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class EventHandler {
    private static final ThreadLocal<EventHandler> INSTANCE = ThreadLocal.withInitial(EventHandler::new);

    public static EventHandler getInstance() {
        return INSTANCE.get();
    }

    public static void setInstance(EventHandler eventHandler) {
        INSTANCE.set(eventHandler);
    }

    Map<EventType, LinkedList<Event<? extends Component>>> eventMap = new HashMap<>();

    private EventHandler() {
    }

    public void addEvent(EventType eventType, Component component, String sourceEntity, String targetEntity) {
        if (!eventMap.containsKey(eventType)) {
            eventMap.put(eventType, new LinkedList<>());
        }
        eventMap.get(eventType).add(new Event<>(eventType, component, sourceEntity, targetEntity));
    }

    public List<Event<? extends Component>> getEventsForType(EventType eventType) {
        if (this.eventMap.containsKey(eventType)) {
            return this.eventMap.get(eventType);
        }
        return null;
    }

    public Event<? extends Component> pollEventForType(EventType eventType) {
        if (this.eventMap.containsKey(eventType)) {
            return this.eventMap.get(eventType).poll();
        }
        return null;
    }

    public void clearAllEvents() {
        this.eventMap.clear();
    }
}
