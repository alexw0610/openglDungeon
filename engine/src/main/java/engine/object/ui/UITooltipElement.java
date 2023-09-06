package engine.object.ui;

public class UITooltipElement extends UIElement {

    private String tooltip;

    public UITooltipElement(double x, double y, double width, double height, int layer, String texture) {
        super(x, y, width, height, layer, texture);
    }

    public String getTooltip() {
        return tooltip;
    }

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }
}
