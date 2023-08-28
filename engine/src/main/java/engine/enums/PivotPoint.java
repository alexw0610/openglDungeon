package engine.enums;

public enum PivotPoint {
    CENTER(0),
    BOTTOM_LEFT(1);

    private final int pivotId;

    PivotPoint(int pivotId) {
        this.pivotId = pivotId;
    }

    public int getPivotId() {
        return pivotId;
    }
}
