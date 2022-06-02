package engine.object.ui;

import engine.enums.Color;

public class HealthBar extends UIElement {

    private final double healthPercentage;

    public HealthBar(double screenPositionX, double screenPositionY, double width, double height, double healthPercentage) {
        super(screenPositionX, screenPositionY, width * (healthPercentage), height);
        this.healthPercentage = healthPercentage;
        setColorForPercentage();
    }

    private void setColorForPercentage() {
        if (this.healthPercentage >= 0.66f) {
            super.setColor(Color.GREEN.value());
        } else if (this.healthPercentage >= 0.33f) {
            super.setColor(Color.YELLOW.value());
        } else {
            super.setColor(Color.RED.value());
        }
    }

}
