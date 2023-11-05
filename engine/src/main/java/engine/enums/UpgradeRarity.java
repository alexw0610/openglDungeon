package engine.enums;

public enum UpgradeRarity {

    COMMON("common", 0),
    RARE("rare", 1),
    EPIC("epic", 2),
    LEGENDARY("legendary", 3);

    private final String key;
    private final int rank;

    UpgradeRarity(String key, int rank) {
        this.key = key;
        this.rank = rank;
    }

    public String getKey() {
        return key;
    }

    public int getRank() {
        return rank;
    }
}
