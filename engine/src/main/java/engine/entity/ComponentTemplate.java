package engine.entity;

import java.util.Map;

public class ComponentTemplate {

    private String type;
    private Map<String, Object> arguments;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, Object> getArguments() {
        return arguments;
    }

    public void setArguments(Map<String, Object> arguments) {
        this.arguments = arguments;
    }

    @Override
    public String toString() {
        return "ComponentTemplate{" +
                "type='" + type + '\'' +
                ", arguments=" + arguments +
                '}';
    }
}
