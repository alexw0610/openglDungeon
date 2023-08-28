package engine.object.ui;

public class UIKerning {
    private final int firstCharacterId;
    private final int secondCharacterId;
    private final int amount;

    public UIKerning(int firstCharacterId, int secondCharacterId, int amount) {
        this.firstCharacterId = firstCharacterId;
        this.secondCharacterId = secondCharacterId;
        this.amount = amount;
    }

    public int getFirstCharacterId() {
        return firstCharacterId;
    }

    public int getSecondCharacterId() {
        return secondCharacterId;
    }

    public int getAmount() {
        return amount;
    }
}
