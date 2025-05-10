package code.hybrid_bearing_system.design;

public class BearingModel {
    private String bearingType = "Hybrid";
    private double spindleSpeed = 10000;
    private double load = 500;
    private double youngsModulus = 380;
    private double mass = 1.0;
    private double specificHeat = 900;
    private double bearingY = 100;
    private double rotorY = 160;
    public String getBearingType() {
        return bearingType;
    }
    public void setBearingType(String bearingType) {
        this.bearingType = bearingType;
    }
    public double getSpindleSpeed() {
        return spindleSpeed;
    }
    public void setSpindleSpeed(double spindleSpeed) {
        this.spindleSpeed = spindleSpeed;
    }
    public double getLoad() {
        return load;
    }
    public void setLoad(double load) {
        this.load = load;
    }
    public double getYoungsModulus() {
        return youngsModulus;
    }
    public void setYoungsModulus(double youngsModulus) {
        this.youngsModulus = youngsModulus;
    }
    public double getMass() {
        return mass;
    }
    public double getSpecificHeat() {
        return specificHeat;
    }
    public double getBearingY() {
        return bearingY;
    }
    public void setBearingY(double bearingY) {
        this.bearingY = bearingY;
    }
    public double getRotorY() {
        return rotorY;
    }
    public void setRotorY(double rotorY) {
        this.rotorY = rotorY;
    }
}