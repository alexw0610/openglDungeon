package engine.component;

public class TrapComponent implements Component {
    private static final long serialVersionUID = -1319111595561604968L;

    private String targetDotComponentTemplateName;

    public TrapComponent(String templateName) {
        this.targetDotComponentTemplateName = templateName;
    }

    public String getTargetDotComponentTemplateName() {
        return targetDotComponentTemplateName;
    }

    public void setTargetDotComponentTemplateName(String targetDotComponentTemplateName) {
        this.targetDotComponentTemplateName = targetDotComponentTemplateName;
    }

    @Override
    public void onRemove() {

    }
}
