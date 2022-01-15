package engine.component;

public class ZoneChangeComponent implements Component {

    private long seed;
    private String zoneTemplateName;

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

    public String getZoneTemplateName() {
        return zoneTemplateName;
    }

    public void setZoneTemplateName(String zoneTemplateName) {
        this.zoneTemplateName = zoneTemplateName;
    }
}
