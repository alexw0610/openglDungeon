package engine.component;

public class UpgradeComponent implements Component {
    private static final long serialVersionUID = -705185947734602455L;

    private String upgradeTitle;
    private String upgradeRarity;
    private String toolTip;
    private String upgradeType;
    private double modifierValue;

    public String getToolTip() {
        return toolTip;
    }

    public void setToolTip(String toolTip) {
        this.toolTip = toolTip;
    }

    public String getUpgradeType() {
        return upgradeType;
    }

    public void setUpgradeType(String upgradeType) {
        this.upgradeType = upgradeType;
    }

    public double getModifierValue() {
        return modifierValue;
    }

    public void setModifierValue(Double modifierValue) {
        this.modifierValue = modifierValue;
    }

    public String getUpgradeTitle() {
        return upgradeTitle;
    }

    public void setUpgradeTitle(String upgradeTitle) {
        this.upgradeTitle = upgradeTitle;
    }

    public String getUpgradeRarity() {
        return upgradeRarity;
    }

    public void setUpgradeRarity(String upgradeRarity) {
        this.upgradeRarity = upgradeRarity;
    }
}
