package engine.object;

import engine.component.Component;
import engine.enums.EventType;

public class Event<T extends Component> {

    private EventType eventType;
    private T component;
    private String targetEntity;
    private String sourceEntity;

    public Event(EventType eventType, T component, String sourceEntity, String targetEntity) {
        this.eventType = eventType;
        this.component = component;
        this.sourceEntity = sourceEntity;
        this.targetEntity = targetEntity;
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

    public String getTargetEntity() {
        return targetEntity;
    }

    public void setTargetEntity(String targetEntity) {
        this.targetEntity = targetEntity;
    }

    public String getSourceEntity() {
        return sourceEntity;
    }

    public void setSourceEntity(String sourceEntity) {
        this.sourceEntity = sourceEntity;
    }
}
