package code.hybrid_bearing_system.design;

public class PhysicsEngine {
    private double rotorDisplacement = 0.0;
    private double rotorVelocity = 0.0;
    private double frictionForce = 0.0;
    private double energyLoss = 0.0;
    private double temperature = 20.0;
    private double magneticFieldStrength = 0.0;
    private double stress = 0.0;
    private BearingModel bearingModel;
    private ModalAnalysis modalAnalysis;
    private double controlForce = 0.0;

    public PhysicsEngine(BearingModel bearingModel) {
        this.bearingModel = bearingModel;
        modalAnalysis = new ModalAnalysis();
    }

    public void update() {
        double dt = 0.01;
        // Update system state using 4th-order Runge-Kutta integration
        // k1-k4 represent intermediate calculations for numerical integration
        // This provides higher accuracy than Euler integration for dynamic systems
        double[] k1 = dynamics(rotorDisplacement, rotorVelocity);
        double[] k2 = dynamics(rotorDisplacement + dt * k1[0] / 2, rotorVelocity + dt * k1[1] / 2);
        double[] k3 = dynamics(rotorDisplacement + dt * k2[0] / 2, rotorVelocity + dt * k2[1] / 2);
        double[] k4 = dynamics(rotorDisplacement + dt * k3[0], rotorVelocity + dt * k3[1]);
        /// Weighted average of k1-k4 for final state update
        double newDisplacement = rotorDisplacement + (dt / 6) * (k1[0] + 2 * k2[0] + 2 * k3[0] + k4[0]);
        double newVelocity = rotorVelocity + (dt / 6) * (k1[1] + 2 * k2[1] + 2 * k3[1] + k4[1]);
        rotorDisplacement = clamp(newDisplacement, -0.01, 0.01);
        rotorVelocity = clamp(newVelocity, -10.0, 10.0);
        double contactRadius = 0.01;
        /// Hertzian contact stress calculation
        /// Assumes circular contact area with radius 0.01m
        /// Effective modulus accounts for material properties (E) and Poisson's ratio (ν=0.3)
        double effectiveModulus = bearingModel.getYoungsModulus() * 1e9 / (1 - 0.3 * 0.3);
        double contactForce = bearingModel.getLoad();
        double contactArea = Math.PI * Math.pow(contactRadius, 2);
        frictionForce = clamp(0.05 * contactForce * (bearingModel.getSpindleSpeed() / 10000), 0, 1000);

        stress = clamp(contactForce / contactArea, 0, 1e9);

        energyLoss += frictionForce * Math.abs(rotorVelocity) * dt;
        if (bearingModel.getBearingType().equals("Magnetic") || bearingModel.getBearingType().equals("Hybrid")) {
            energyLoss += clamp(calculateEddyCurrentLoss(), 0, 1000);
        }
        energyLoss = clamp(energyLoss, 0, 1e6);
        /// Heat generation from friction (Q = F_friction * v)
        double heatGeneration = frictionForce * Math.abs(rotorVelocity);
        if (bearingModel.getBearingType().equals("Magnetic") || bearingModel.getBearingType().equals("Hybrid")) {
            /// Additional eddy current losses for magnetic bearings
            heatGeneration += clamp(0.1 * magneticFieldStrength * magneticFieldStrength, 0, 1000);
        }
        double heatDissipation = calculateHeatDissipation();
        /// Temperature change: ΔT = (Q_in - Q_out) / (m*c_p)
        temperature += (heatGeneration - heatDissipation) * dt / (bearingModel.getSpecificHeat() * bearingModel.getMass());
        temperature = clamp(temperature, 20.0, 500.0);

        if (bearingModel.getBearingType().equals("Magnetic") || bearingModel.getBearingType().equals("Hybrid")) {
            magneticFieldStrength = clamp(calculateMagneticField(), 0, 10.0);
        } else {
            magneticFieldStrength = 0.0;
        }
    }

    private double[] dynamics(double displacement, double velocity) {
        double mass = bearingModel.getMass();
        double stiffness = clamp(bearingModel.getYoungsModulus() * 1e9 * 0.01, 1e6, 1e9);
        double damping = 0.05 * Math.sqrt(stiffness * mass);
        double gyroscopicForce = clamp(0.01 * bearingModel.getSpindleSpeed() * velocity, -1000, 1000);
        double acceleration = (-stiffness * displacement - damping * velocity + calculateMagneticForce() + controlForce - gyroscopicForce) / mass;
        if (Double.isNaN(acceleration) || Double.isInfinite(acceleration)) {
            acceleration = 0.0;
        }
        return new double[]{velocity, clamp(acceleration, -1000, 1000)};
    }

    private double calculateMagneticForce() {
        if (bearingModel.getBearingType().equals("Magnetic") || bearingModel.getBearingType().equals("Hybrid")) {
            double current = 10.0;
            double turns = 100;
            double radius = 0.05;
            double mu0 = 4 * Math.PI * 1e-7;
            magneticFieldStrength = mu0 * turns * current / (2 * radius);
            double force = magneticFieldStrength * current * 0.01;
            return clamp(force, -1000, 1000);
        }
        return 0.0;
    }

    private double calculateMagneticField() {
        return magneticFieldStrength;
    }

    private double calculateEddyCurrentLoss() {
        double resistivity = 1e-6;
        double volume = 0.001;
        double loss = (magneticFieldStrength * magneticFieldStrength * volume) / resistivity * 0.01;
        return clamp(loss, 0, 1000);
    }

    private double calculateHeatDissipation() {
        /// Heat dissipation via convection and conduction
        double convectionCoefficient = 25.0;
        double surfaceArea = 0.01;
        double thermalConductivity = MaterialProperties.getThermalConductivity("ceramic");
        double conduction = thermalConductivity * surfaceArea * (temperature - 20.0) / 0.01;
        return clamp(convectionCoefficient * surfaceArea * (temperature - 20.0) + conduction, 0, 10000);
    }

    public void applyControlForce(double force) {
        this.controlForce = clamp(force, -1000, 1000);
    }

    public void reset() {
        rotorDisplacement = 0.0;
        rotorVelocity = 0.0;
        frictionForce = 0.0;
        energyLoss = 0.0;
        temperature = 20.0;
        magneticFieldStrength = 0.0;
        stress = 0.0;
        controlForce = 0.0;
    }

    private double clamp(double value, double min, double max) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            return min;
        }
        return Math.max(min, Math.min(max, value));
    }

    public double getRotorDisplacement() { return rotorDisplacement; }
    public double getRotorVelocity() { return rotorVelocity; }
    public double getFrictionForce() { return frictionForce; }
    public double getEnergyLoss() { return energyLoss; }
    public double getTemperature() { return temperature; }
    public double getMagneticFieldStrength() { return magneticFieldStrength; }
    public double getStress() { return stress; }
}