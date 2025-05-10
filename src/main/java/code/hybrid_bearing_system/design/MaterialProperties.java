package code.hybrid_bearing_system.design;

public class MaterialProperties {
    public static double getYoungsModulus(String material) {
        switch (material.toLowerCase()) {
            case "ceramic": return 380.0;
            case "steel": return 200.0;
            default: return 300.0;
        }
    }

    public static double getThermalConductivity(String material) {
        switch (material.toLowerCase()) {
            case "ceramic": return 30.0;
            case "steel": return 50.0;
            default: return 40.0;
        }
    }

    public static double getDensity(String material) {
        switch (material.toLowerCase()) {
            case "ceramic": return 3200;
            case "steel": return 7850;
            default: return 5000;
        }
    }
}