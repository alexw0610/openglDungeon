package engine.component;

public class TooltipComponent implements Component {
    private static final long serialVersionUID = -5628115292948633863L;

    private String tooltip;

    public TooltipComponent(String tooltip) {
        this.tooltip = tooltip;
    }

    public String getTooltip() {
        return tooltip;
    }

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }

    @Override
    public void onRemove() {

    }
}
