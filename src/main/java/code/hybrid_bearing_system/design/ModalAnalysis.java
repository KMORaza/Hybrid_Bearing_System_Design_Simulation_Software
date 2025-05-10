package code.hybrid_bearing_system.design;

public class ModalAnalysis {
    public double calculateResonanceFrequency(double stiffness, double mass) {
        if (mass <= 0 || stiffness <= 0) {
            return 0.0;
        }
        return Math.sqrt(stiffness / mass) / (2 * Math.PI);
    }

    public double[] calculateVibrationModes(double stiffness, double mass) {
        double fundamental = calculateResonanceFrequency(stiffness, mass);
        double[] modes = new double[3];
        modes[0] = fundamental;
        modes[1] = 2 * fundamental;
        modes[2] = 3 * fundamental;
        return modes;
    }

    public double calculateCriticalSpeed(double stiffness, double mass) {
        return 60 * calculateResonanceFrequency(stiffness, mass);
    }
}