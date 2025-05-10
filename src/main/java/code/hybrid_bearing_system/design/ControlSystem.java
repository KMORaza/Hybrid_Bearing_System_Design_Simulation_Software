package code.hybrid_bearing_system.design;

public class ControlSystem {
    private double kp = 1000.0;
    private double ki = 10.0;
    private double kd = 50.0;
    private double integral = 0.0;
    private double previousError = 0.0;
    private double estimatedDisplacement = 0.0;
    private double estimatedVelocity = 0.0;
    private double covariance = 1.0;
    private double processNoise = 0.01;
    private double measurementNoise = 0.1;

    public double update(double measuredDisplacement, double measuredVelocity) {
        if (Double.isNaN(measuredDisplacement) || Double.isNaN(measuredVelocity)) {
            return 0.0;
        }

        double predictedDisplacement = estimatedDisplacement + 0.01 * estimatedVelocity;
        double predictedCovariance = covariance + processNoise;
        double kalmanGain = predictedCovariance / (predictedCovariance + measurementNoise);
        estimatedDisplacement = predictedDisplacement + kalmanGain * (measuredDisplacement - predictedDisplacement);
        estimatedVelocity = estimatedVelocity + kalmanGain * (measuredVelocity - estimatedVelocity);
        covariance = (1 - kalmanGain) * predictedCovariance;

        double setpoint = 0.0;
        double error = clamp(setpoint - estimatedDisplacement, -0.01, 0.01);
        if (Math.abs(error) > 0.005) {
            integral = 0.0;
        }
        integral += error * 0.01;
        integral = clamp(integral, -100, 100);
        double derivative = clamp((error - previousError) / 0.01, -100, 100);
        previousError = error;

        double control = kp * error + ki * integral + kd * derivative;
        return clamp(control, -1000, 1000);
    }

    public void setKp(double kp) {
        this.kp = clamp(kp, 0, 2000);
    }
    public void setKi(double ki) {
        this.ki = clamp(ki, 0, 50);
    }
    public void setKd(double kd) {
        this.kd = clamp(kd, 0, 100);
    }

    private double clamp(double value, double min, double max) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            return min;
        }
        return Math.max(min, Math.min(max, value));
    }
}