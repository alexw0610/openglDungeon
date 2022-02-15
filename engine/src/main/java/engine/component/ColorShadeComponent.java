package engine.component;

public class ColorShadeComponent implements Component {

    private static final long serialVersionUID = -8244844041688813245L;
    private double redMultiplier;
    private double greenMultiplier;
    private double blueMultiplier;
    private double keepAliveUntil;

    public ColorShadeComponent(double redMultiplier, double greenMultiplier, double blueMultiplier, double keepAliveForMs) {
        this.redMultiplier = redMultiplier;
        this.greenMultiplier = greenMultiplier;
        this.blueMultiplier = blueMultiplier;
        setKeepAliveUntil(keepAliveForMs);
    }

    public double getRedMultiplier() {
        return redMultiplier;
    }

    public void setRedMultiplier(double redMultiplier) {
        this.redMultiplier = redMultiplier;
    }

    public double getGreenMultiplier() {
        return greenMultiplier;
    }

    public void setGreenMultiplier(double greenMultiplier) {
        this.greenMultiplier = greenMultiplier;
    }

    public double getBlueMultiplier() {
        return blueMultiplier;
    }

    public void setBlueMultiplier(double blueMultiplier) {
        this.blueMultiplier = blueMultiplier;
    }

    public double getKeepAliveUntil() {
        return keepAliveUntil;
    }

    public void setKeepAliveUntil(double keepAliveForMs) {
        this.keepAliveUntil = System.currentTimeMillis() + keepAliveForMs;
    }
}