package engine.component;

public class BombComponent implements Component {
    private static final long serialVersionUID = -8692990482586838968L;
    private double fuseTime;
    private double creationTime;
    private String attackComponentTemplate;

    public double getFuseTime() {
        return fuseTime;
    }

    public void setFuseTime(Double fuseTime) {
        this.fuseTime = fuseTime;
    }

    public double getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(double creationTime) {
        this.creationTime = creationTime;
    }

    public String getAttackComponentTemplate() {
        return attackComponentTemplate;
    }

    public void setAttackComponentTemplate(String attackComponentTemplate) {
        this.attackComponentTemplate = attackComponentTemplate;
    }

    @Override
    public void onRemove() {

    }
}
