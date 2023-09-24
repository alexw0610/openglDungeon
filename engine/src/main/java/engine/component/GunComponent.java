package engine.component;

public class GunComponent implements Component {
    private String gunName;
    private String gunSprite;
    private double primaryBaseDamage;
    private double secondaryBaseDamage;
    private double primaryBaseAttackSpeed;
    private double secondaryBaseAttackSpeed;
    private double primaryBaseBulletSpeed;
    private double secondaryBaseBulletSpeed;
    private boolean primaryAttack;
    private boolean secondaryAttack;
    private boolean primaryModifierSlotAAvailable;
    private boolean secondaryModifierSlotAAvailable;
    private boolean primaryModifierSlotBAvailable;
    private boolean secondaryModifierSlotBAvailable;
    private UpgradeComponent primaryModifierSlotA;
    private UpgradeComponent secondaryModifierSlotA;
    private UpgradeComponent primaryModifierSlotB;
    private UpgradeComponent secondaryModifierSlotB;

    public String getGunName() {
        return gunName;
    }

    public void setGunName(String gunName) {
        this.gunName = gunName;
    }

    public String getGunSprite() {
        return gunSprite;
    }

    public void setGunSprite(String gunSprite) {
        this.gunSprite = gunSprite;
    }

    public double getPrimaryBaseDamage() {
        return primaryBaseDamage;
    }

    public void setPrimaryBaseDamage(Double primaryBaseDamage) {
        this.primaryBaseDamage = primaryBaseDamage;
    }

    public double getSecondaryBaseDamage() {
        return secondaryBaseDamage;
    }

    public void setSecondaryBaseDamage(Double secondaryBaseDamage) {
        this.secondaryBaseDamage = secondaryBaseDamage;
    }

    public double getPrimaryBaseAttackSpeed() {
        return primaryBaseAttackSpeed;
    }

    public void setPrimaryBaseAttackSpeed(Double primaryBaseAttackSpeed) {
        this.primaryBaseAttackSpeed = primaryBaseAttackSpeed;
    }

    public double getSecondaryBaseAttackSpeed() {
        return secondaryBaseAttackSpeed;
    }

    public void setSecondaryBaseAttackSpeed(Double secondaryBaseAttackSpeed) {
        this.secondaryBaseAttackSpeed = secondaryBaseAttackSpeed;
    }

    public double getPrimaryBaseBulletSpeed() {
        return primaryBaseBulletSpeed;
    }

    public void setPrimaryBaseBulletSpeed(Double primaryBaseBulletSpeed) {
        this.primaryBaseBulletSpeed = primaryBaseBulletSpeed;
    }

    public double getSecondaryBaseBulletSpeed() {
        return secondaryBaseBulletSpeed;
    }

    public void setSecondaryBaseBulletSpeed(Double secondaryBaseBulletSpeed) {
        this.secondaryBaseBulletSpeed = secondaryBaseBulletSpeed;
    }

    public boolean isPrimaryAttack() {
        return primaryAttack;
    }

    public void setPrimaryAttack(Boolean primaryAttack) {
        this.primaryAttack = primaryAttack;
    }

    public boolean isSecondaryAttack() {
        return secondaryAttack;
    }

    public void setSecondaryAttack(Boolean secondaryAttack) {
        this.secondaryAttack = secondaryAttack;
    }

    public boolean isPrimaryModifierSlotAAvailable() {
        return primaryModifierSlotAAvailable;
    }

    public void setPrimaryModifierSlotAAvailable(Boolean primaryModifierSlotAAvailable) {
        this.primaryModifierSlotAAvailable = primaryModifierSlotAAvailable;
    }

    public boolean isSecondaryModifierSlotAAvailable() {
        return secondaryModifierSlotAAvailable;
    }

    public void setSecondaryModifierSlotAAvailable(Boolean secondaryModifierSlotAAvailable) {
        this.secondaryModifierSlotAAvailable = secondaryModifierSlotAAvailable;
    }

    public boolean isPrimaryModifierSlotBAvailable() {
        return primaryModifierSlotBAvailable;
    }

    public void setPrimaryModifierSlotBAvailable(Boolean primaryModifierSlotBAvailable) {
        this.primaryModifierSlotBAvailable = primaryModifierSlotBAvailable;
    }

    public boolean isSecondaryModifierSlotBAvailable() {
        return secondaryModifierSlotBAvailable;
    }

    public void setSecondaryModifierSlotBAvailable(Boolean secondaryModifierSlotBAvailable) {
        this.secondaryModifierSlotBAvailable = secondaryModifierSlotBAvailable;
    }

    public UpgradeComponent getPrimaryModifierSlotA() {
        return primaryModifierSlotA;
    }

    public void setPrimaryModifierSlotA(UpgradeComponent primaryModifierSlotA) {
        this.primaryModifierSlotA = primaryModifierSlotA;
    }

    public UpgradeComponent getSecondaryModifierSlotA() {
        return secondaryModifierSlotA;
    }

    public void setSecondaryModifierSlotA(UpgradeComponent secondaryModifierSlotA) {
        this.secondaryModifierSlotA = secondaryModifierSlotA;
    }

    public UpgradeComponent getPrimaryModifierSlotB() {
        return primaryModifierSlotB;
    }

    public void setPrimaryModifierSlotB(UpgradeComponent primaryModifierSlotB) {
        this.primaryModifierSlotB = primaryModifierSlotB;
    }

    public UpgradeComponent getSecondaryModifierSlotB() {
        return secondaryModifierSlotB;
    }

    public void setSecondaryModifierSlotB(UpgradeComponent secondaryModifierSlotB) {
        this.secondaryModifierSlotB = secondaryModifierSlotB;
    }

    @Override
    public void onRemove() {

    }
}
