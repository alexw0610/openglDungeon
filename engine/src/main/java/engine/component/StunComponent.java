package engine.component;

public class StunComponent implements Component {
    private static final long serialVersionUID = 5939391868465433216L;

    private final double stunStartTime;
    private double stunDurationSeconds;

    public StunComponent(double stunDurationSeconds) {
        this.stunStartTime = System.nanoTime();
        this.stunDurationSeconds = stunDurationSeconds;
    }

    public double getStunStartTime() {
        return stunStartTime;
    }

    public double getStunDurationSeconds() {
        return stunDurationSeconds;
    }

    public void setStunDurationSeconds(double stunDurationSeconds) {
        this.stunDurationSeconds = stunDurationSeconds;
    }
}
