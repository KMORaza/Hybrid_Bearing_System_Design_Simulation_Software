package code.hybrid_bearing_system.design;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;

public class Visualization2D {
    private double animationAngle = 0.0;

    public void drawFrictionGauge(GraphicsContext gc, double frictionForce) {
        double value = clamp(frictionForce / 1000, 0, 1);
        drawGauge(gc, 150, 150, 80, value, Color.web("#FF5555"), "Friction", String.format("%.2f N", frictionForce));
    }

    public void drawEnergyLossGauge(GraphicsContext gc, double energyLoss) {
        double value = clamp(energyLoss / 10000, 0, 1);
        drawGauge(gc, 450, 150, 80, value, Color.web("#00FF00"), "Energy Loss", String.format("%.2f J", energyLoss));
    }

    public void drawTemperatureGauge(GraphicsContext gc, double temperature) {
        double value = clamp((temperature - 20) / 480, 0, 1);
        drawGauge(gc, 750, 150, 80, value, Color.web("#00BFFF"), "Temperature", String.format("%.2f °C", temperature));
    }

    private void drawGauge(GraphicsContext gc, double x, double y, double radius, double value,
                           Color color, String label, String valueText) {
        gc.setFill(Color.web("#333333"));
        gc.fillOval(x - radius, y - radius, radius * 2, radius * 2);
        gc.setStroke(Color.web("#FFFFFF"));
        gc.setLineWidth(5);
        gc.strokeOval(x - radius, y - radius, radius * 2, radius * 2);
        gc.setStroke(color);
        gc.setLineWidth(10);
        double angle = -Math.PI / 2 + value * Math.PI;
        gc.strokeArc(x - radius, y - radius, radius * 2, radius * 2, -90, value * 180, javafx.scene.shape.ArcType.OPEN);
        gc.setFill(Color.WHITE);
        gc.fillText(label, x - 30, y + radius + 20);
        gc.fillText(valueText, x - 30, y + radius + 40);
    }

    public void drawRotorDynamics(GraphicsContext gc, double displacement) {
        gc.setFill(Color.web("#FF5555"));
        double x = 450 + displacement * 1000;
        gc.fillOval(x, 350, 30, 30);
        gc.setStroke(Color.web("#FFFFFF"));
        gc.strokeLine(450, 360, 450, 340);
        gc.setFill(Color.WHITE);
        gc.fillText("Displacement: " + String.format("%.2f mm", displacement * 1000), 450, 320);
        animationAngle += 0.1;
        gc.setStroke(Color.web("#FF5555"));
        gc.setLineWidth(2);
        for (int i = 0; i < 4; i++) {
            double theta = animationAngle + i * Math.PI / 2;
            gc.strokeLine(x + 15, 365, x + 15 + 20 * Math.cos(theta), 365 + 20 * Math.sin(theta));
        }
    }

    public void drawMagneticField(GraphicsContext gc, double fieldStrength) {
        if (fieldStrength > 0) {
            gc.setStroke(Color.web("#AA00FF"));
            gc.setLineWidth(2);
            int lines = (int) (fieldStrength * 5);
            for (int i = 0; i < lines; i++) {
                double theta = i * 2 * Math.PI / lines + animationAngle;
                double x1 = 150 + 50 * Math.cos(theta);
                double y1 = 400 + 50 * Math.sin(theta);
                double x2 = 150 + 100 * Math.cos(theta);
                double y2 = 400 + 100 * Math.sin(theta);
                gc.strokeLine(x1, y1, x2, y2);
            }
            gc.setFill(Color.WHITE);
            gc.fillText("Magnetic Field: " + String.format("%.2f T", fieldStrength), 100, 380);
        }
    }

    public void drawStressMap(GraphicsContext gc, double stress) {
        double normalizedStress = clamp(stress / 1e9, 0, 1);
        LinearGradient gradient = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#00BFFF")), new Stop(normalizedStress, Color.web("#FF5555")));
        gc.setFill(gradient);
        gc.fillRect(700, 350, 100, 50);
        gc.setStroke(Color.WHITE);
        gc.strokeRect(700, 350, 100, 50);
        gc.setFill(Color.WHITE);
        gc.fillText("Stress: " + String.format("%.2f MPa", stress / 1e6), 700, 330);
    }

    public void reset() {
        animationAngle = 0.0;
    }

    private double clamp(double value, double min, double max) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            return min;
        }
        return Math.max(min, Math.min(max, value));
    }
}