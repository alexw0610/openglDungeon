package engine.component;

public class ZoneChangeComponent implements Component {

    private static final long serialVersionUID = -2254350620787946888L;
    private long seed;

    public ZoneChangeComponent() {
    }

    public ZoneChangeComponent(Integer seed) {
        this.seed = seed.longValue();
    }

    public long getSeed() {
        return this.seed;
    }

    public void setSeed(Integer seed) {
        this.seed = seed.longValue();
    }

    @Override
    public boolean isServerSide() {
        return true;
    }
}
