package engine.component;

import engine.enums.UpgradeRarity;

import java.util.Objects;

public class UpgradeComponent implements Component {
    private static final long serialVersionUID = -705185947734602455L;

    private String upgradeTitle;
    private String upgradeIcon;
    private String upgradeRarity;
    private String toolTip;
    private String upgradeType;
    private String upgradeCategory;
    private String upgradeSlot;
    private double modifierValue;
    private int minSpawnLevel;

    public UpgradeComponent() {
        this.upgradeSlot = "Primary & Secondary";
    }

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

    public String getUpgradeCategory() {
        return upgradeCategory;
    }

    public void setUpgradeCategory(String upgradeCategory) {
        this.upgradeCategory = upgradeCategory;
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

    public String getUpgradeIcon() {
        return upgradeIcon;
    }

    public void setUpgradeIcon(String upgradeIcon) {
        this.upgradeIcon = upgradeIcon;
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

    public String getUpgradeSlot() {
        return upgradeSlot;
    }

    public void setUpgradeSlot(String upgradeSlot) {
        this.upgradeSlot = upgradeSlot;
    }

    public int getMinSpawnLevel() {
        return minSpawnLevel;
    }

    public void setMinSpawnLevel(Integer minSpawnLevel) {
        this.minSpawnLevel = minSpawnLevel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UpgradeComponent that = (UpgradeComponent) o;
        return upgradeTitle.equals(that.upgradeTitle);
    }

    @Override
    public int hashCode() {
        return Objects.hash(upgradeTitle);
    }

    public int compareTo(UpgradeComponent that) {
        int comparison = Integer.compare(UpgradeRarity.valueOf(that.upgradeRarity.toUpperCase()).getRank(),
                UpgradeRarity.valueOf(this.upgradeRarity.toUpperCase()).getRank());
        if (comparison == 0) {
            return that.upgradeTitle.compareTo(this.upgradeTitle);
        }
        return comparison;
    }

    @Override
    public void onRemove() {

    }
}
