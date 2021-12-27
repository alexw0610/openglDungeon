package engine.component;

public class ZoneChangeComponent implements Component {

    private long seed;

    public ZoneChangeComponent() {
    }

    public ZoneChangeComponent(Integer seed) {
        this.seed = seed.longValue();
    }

    public long getSeed() {
        return this.seed;
    }

    public void setSeed(long seed) {
        this.seed = seed;
    }
}
