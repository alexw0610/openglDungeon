package engine.component.internal;

import engine.component.Component;

public class CreatedAtComponent implements Component {
    private static final long serialVersionUID = -8273157692480120642L;

    private final double engineTick;

    public CreatedAtComponent(double engineTick) {
        this.engineTick = engineTick;
    }

    public double getEngineTick() {
        return engineTick;
    }
}
