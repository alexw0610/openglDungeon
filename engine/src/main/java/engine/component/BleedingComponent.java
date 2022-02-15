package engine.component;

public class BleedingComponent implements Component {

    private static final long serialVersionUID = -3928923046132052288L;
    private double bloodColorR;
    private double bloodColorG;
    private double bloodColorB;

    public BleedingComponent(Double bloodColorR, Double bloodColorG, Double bloodColorB) {
        this.bloodColorR = bloodColorR;
        this.bloodColorG = bloodColorG;
        this.bloodColorB = bloodColorB;
    }

    public double getBloodColorR() {
        return bloodColorR;
    }

    public void setBloodColorR(double bloodColorR) {
        this.bloodColorR = bloodColorR;
    }

    public double getBloodColorG() {
        return bloodColorG;
    }

    public void setBloodColorG(double bloodColorG) {
        this.bloodColorG = bloodColorG;
    }

    public double getBloodColorB() {
        return bloodColorB;
    }

    public void setBloodColorB(double bloodColorB) {
        this.bloodColorB = bloodColorB;
    }
}
