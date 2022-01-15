package engine.loader.template;

public class EntityInstanceTemplate {

    private String templateName;
    private double x;
    private double y;
    private int maxAmount;
    private int minAmount;
    private String locationConstraint;

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public int getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(int maxAmount) {
        this.maxAmount = maxAmount;
    }

    public int getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(int minAmount) {
        this.minAmount = minAmount;
    }

    public String getLocationConstraint() {
        return locationConstraint;
    }

    public void setLocationConstraint(String locationConstraint) {
        this.locationConstraint = locationConstraint;
    }
}
