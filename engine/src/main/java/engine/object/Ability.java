package engine.object;

public class Ability {

    private String name;
    private int lastUsed;
    private String icon;
    private String attackComponentName;

    public Ability(String name, int lastUsed, String icon, String attackComponentName) {
        this.name = name;
        this.lastUsed = lastUsed;
        this.icon = icon;
        this.attackComponentName = attackComponentName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLastUsed() {
        return lastUsed;
    }

    public void setLastUsed(int lastUsed) {
        this.lastUsed = lastUsed;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getAttackComponentName() {
        return attackComponentName;
    }

    public void setAttackComponentName(String attackComponentName) {
        this.attackComponentName = attackComponentName;
    }
}
