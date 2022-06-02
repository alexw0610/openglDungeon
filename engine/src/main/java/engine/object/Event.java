package engine.object;

import engine.component.Component;
import engine.enums.EventType;

public class Event<T extends Component> {

    private EventType eventType;
    private T component;
    private String createdByEntity;

    public Event(EventType eventType, T component, String createdByEntity) {
        this.eventType = eventType;
        this.component = component;
        this.createdByEntity = createdByEntity;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public T getComponent() {
        return component;
    }

    public void setComponent(T component) {
        this.component = component;
    }

    public String getCreatedByEntity() {
        return createdByEntity;
    }

    public void setCreatedByEntity(String createdByEntity) {
        this.createdByEntity = createdByEntity;
    }
}
