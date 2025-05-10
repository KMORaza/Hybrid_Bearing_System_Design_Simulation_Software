package code.hybrid_bearing_system.design;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class AnalysisPanel {
    private VBox pane;
    private Canvas comparisonCanvas;
    private BearingModel bearingModel;
    private PhysicsEngine physicsEngine;
    private Label resultLabel;

    public AnalysisPanel(BearingModel bearingModel, PhysicsEngine physicsEngine) {
        this.bearingModel = bearingModel;
        this.physicsEngine = physicsEngine;
        pane = new VBox(10);
        pane.setPadding(new Insets(10));
        pane.setStyle("-fx-background-color: #1E1E1E;");
        TitledPane comparisonPane = new TitledPane();
        comparisonPane.setText("Configuration Comparison");
        comparisonCanvas = new Canvas(600, 400);
        resultLabel = new Label("Select configurations to compare");
        Button compareButton = new Button("Compare Configurations");
        compareButton.setOnAction(e -> compareConfigurations());
        VBox comparisonBox = new VBox(10, comparisonCanvas, resultLabel, compareButton);
        comparisonBox.setAlignment(Pos.CENTER);
        comparisonPane.setContent(comparisonBox);
        pane.getChildren().add(comparisonPane);
    }

    private void compareConfigurations() {
        double[] energyLosses = new double[3];
        double[] rigidities = new double[3];
        double[] frictions = new double[3];
        String[] types = {"Magnetic", "Ceramic", "Hybrid"};
        String originalType = bearingModel.getBearingType();
        for (int i = 0; i < 3; i++) {
            bearingModel.setBearingType(types[i]);
            physicsEngine.reset();
            for (int j = 0; j < 100; j++) {
                physicsEngine.update();
            }
            energyLosses[i] = physicsEngine.getEnergyLoss();
            rigidities[i] = bearingModel.getYoungsModulus() * 1e9 * 0.01;
            frictions[i] = physicsEngine.getFrictionForce();
        }
        bearingModel.setBearingType(originalType);
        GraphicsContext gc = comparisonCanvas.getGraphicsContext2D();
        gc.setFill(Color.web("#1E1E1E"));
        gc.fillRect(0, 0, comparisonCanvas.getWidth(), comparisonCanvas.getHeight());
        gc.setFill(Color.web("#00FF00"));
        for (int i = 0; i < 3; i++) {
            double height = Math.min(energyLosses[i] * 0.05, 300);
            gc.fillRect(100 + i * 150, 350 - height, 100, height);
            gc.setFill(Color.WHITE);
            gc.fillText(types[i], 100 + i * 150, 370);
        }
        resultLabel.setText(String.format("Energy Loss (J): Magnetic: %.2f, Ceramic: %.2f, Hybrid: %.2f",
                energyLosses[0], energyLosses[1], energyLosses[2]));
    }

    public VBox getPane() {
        return pane;
    }
}