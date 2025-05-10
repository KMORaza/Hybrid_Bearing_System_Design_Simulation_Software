package code.hybrid_bearing_system.design;

import java.io.FileWriter;
import java.io.IOException;

public class DataExporter {
    public void exportToCSV(PhysicsEngine physics, String filename) {
        try (FileWriter writer = new FileWriter(filename, true)) {
            if (new java.io.File(filename).length() == 0) {
                writer.write("Time,Displacement,Velocity,Friction,EnergyLoss,Temperature,Stress,MagneticField\n");
            }
            writer.write(String.format("%.2f,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f\n",
                    System.currentTimeMillis() / 1000.0,
                    physics.getRotorDisplacement(),
                    physics.getRotorVelocity(),
                    physics.getFrictionForce(),
                    physics.getEnergyLoss(),
                    physics.getTemperature(),
                    physics.getStress(),
                    physics.getMagneticFieldStrength()));
        } catch (IOException e) {
            System.err.println("Error writing CSV: " + e.getMessage());
        }
    }
}