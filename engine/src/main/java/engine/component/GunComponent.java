package engine.component;

import engine.enums.BulletModifier;

public class GunComponent implements Component {
    private String gunName;
    private String gunSprite;
    private double primaryBaseDamage;
    private double secondaryBaseDamage;
    private double primaryBaseAttackSpeed;
    private double secondaryBaseAttackSpeed;
    private double primaryBulletSpeed;
    private double secondaryBulletSpeed;
    private boolean primaryAttack;
    private boolean secondaryAttack;
    private boolean primaryModifierSlotAAvailable;
    private boolean secondaryModifierSlotAAvailable;
    private boolean primaryModifierSlotBAvailable;
    private boolean secondaryModifierSlotBAvailable;
    private BulletModifier primaryModifierSlotA;
    private BulletModifier secondaryModifierSlotA;
    private BulletModifier primaryModifierSlotB;
    private BulletModifier secondaryModifierSlotB;

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

    public double getPrimaryBulletSpeed() {
        return primaryBulletSpeed;
    }

    public void setPrimaryBulletSpeed(Double primaryBulletSpeed) {
        this.primaryBulletSpeed = primaryBulletSpeed;
    }

    public double getSecondaryBulletSpeed() {
        return secondaryBulletSpeed;
    }

    public void setSecondaryBulletSpeed(Double secondaryBulletSpeed) {
        this.secondaryBulletSpeed = secondaryBulletSpeed;
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

    public BulletModifier getPrimaryModifierSlotA() {
        return primaryModifierSlotA;
    }

    public void setPrimaryModifierSlotA(BulletModifier primaryModifierSlotA) {
        this.primaryModifierSlotA = primaryModifierSlotA;
    }

    public BulletModifier getSecondaryModifierSlotA() {
        return secondaryModifierSlotA;
    }

    public void setSecondaryModifierSlotA(BulletModifier secondaryModifierSlotA) {
        this.secondaryModifierSlotA = secondaryModifierSlotA;
    }

    public BulletModifier getPrimaryModifierSlotB() {
        return primaryModifierSlotB;
    }

    public void setPrimaryModifierSlotB(BulletModifier primaryModifierSlotB) {
        this.primaryModifierSlotB = primaryModifierSlotB;
    }

    public BulletModifier getSecondaryModifierSlotB() {
        return secondaryModifierSlotB;
    }

    public void setSecondaryModifierSlotB(BulletModifier secondaryModifierSlotB) {
        this.secondaryModifierSlotB = secondaryModifierSlotB;
    }
}
