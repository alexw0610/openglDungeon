package engine.handler;

import engine.component.Component;
import engine.enums.EventType;
import engine.object.Event;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class EventHandler {
    private static final ThreadLocal<EventHandler> INSTANCE = ThreadLocal.withInitial(EventHandler::new);

    public static EventHandler getInstance() {
        return INSTANCE.get();
    }

    Map<Class<? extends Component>, LinkedList<Event<? extends Component>>> eventMap = new HashMap<>();

    private EventHandler() {
    }

    public void addEvent(EventType eventType, Component component, String createdByEntity) {
        if (!eventMap.containsKey(component.getClass())) {
            eventMap.put(component.getClass(), new LinkedList<>());
        }
        eventMap.get(component.getClass()).add(new Event<>(eventType, component, createdByEntity));
    }

    public <T extends Component> LinkedList<Event<T>> getEventsForComponent(Class<T> componentType) {
        if (this.eventMap.containsKey(componentType)) {
            LinkedList<? extends Event<?>> list = this.eventMap.get(componentType);
            return (LinkedList<Event<T>>) list;
        }
        return null;
    }

    public void clearAllEvents() {
        this.eventMap.clear();
    }
}
