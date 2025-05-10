package code.hybrid_bearing_system.design;

import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class SimulationDashboard {
    private VBox pane;
    private Canvas dashboardCanvas;
    private PhysicsEngine physicsEngine;
    private Visualization2D visualization2D;
    private ControlSystem controlSystem;
    private DataExporter dataExporter;
    private BearingModel bearingModel;
    private AnimationTimer timer;
    private double time;
    private Label statusLabel;
    private Slider kpSlider, kiSlider, kdSlider;

    public SimulationDashboard(BearingModel bearingModel, PhysicsEngine physicsEngine,
                               ControlSystem controlSystem, DataExporter dataExporter) {
        this.bearingModel = bearingModel;
        this.physicsEngine = physicsEngine;
        this.controlSystem = controlSystem;
        this.dataExporter = dataExporter;
        pane = new VBox(10);
        pane.setPadding(new Insets(10));
        pane.setStyle("-fx-background-color: #1E1E1E;");
        visualization2D = new Visualization2D();
        time = 0.0;

        /// Dashboard
        TitledPane dashboardPane = new TitledPane();
        dashboardPane.setText("Simulation Dashboard");
        dashboardCanvas = new Canvas(900, 500);
        VBox canvasBox = new VBox(dashboardCanvas);
        canvasBox.setAlignment(Pos.CENTER);
        dashboardPane.setContent(canvasBox);

        /// Controls
        TitledPane controlsPane = new TitledPane();
        controlsPane.setText("Simulation Controls");
        GridPane controlsGrid = new GridPane();
        controlsGrid.setHgap(10);
        controlsGrid.setVgap(10);
        controlsGrid.setPadding(new Insets(10));
        Button startButton = new Button("Start Simulation");
        startButton.setOnAction(e -> startSimulation());
        Button stopButton = new Button("Stop Simulation");
        stopButton.setOnAction(e -> stopSimulation());
        Button resetButton = new Button("Reset Simulation");
        resetButton.setOnAction(e -> resetSimulation());
        statusLabel = new Label("Simulation Stopped");
        controlsGrid.add(startButton, 0, 0);
        controlsGrid.add(stopButton, 1, 0);
        controlsGrid.add(resetButton, 2, 0);
        controlsGrid.add(statusLabel, 0, 1, 3, 1);
        controlsPane.setContent(controlsGrid);

        /// PID tuning
        TitledPane pidPane = new TitledPane();
        pidPane.setText("PID Controller Tuning");
        GridPane pidGrid = new GridPane();
        pidGrid.setHgap(10);
        pidGrid.setVgap(10);
        pidGrid.setPadding(new Insets(10));
        kpSlider = new Slider(0, 2000, 1000);
        kpSlider.setShowTickLabels(true);
        kpSlider.setShowTickMarks(true);
        Label kpLabel = new Label("Kp: 1000");
        kpSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            controlSystem.setKp(newVal.doubleValue());
            kpLabel.setText("Kp: " + String.format("%.0f", newVal.doubleValue()));
        });
        kiSlider = new Slider(0, 50, 10);
        kiSlider.setShowTickLabels(true);
        kiSlider.setShowTickMarks(true);
        Label kiLabel = new Label("Ki: 10");
        kiSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            controlSystem.setKi(newVal.doubleValue());
            kiLabel.setText("Ki: " + String.format("%.0f", newVal.doubleValue()));
        });
        kdSlider = new Slider(0, 100, 50);
        kdSlider.setShowTickLabels(true);
        kdSlider.setShowTickMarks(true);
        Label kdLabel = new Label("Kd: 50");
        kdSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            controlSystem.setKd(newVal.doubleValue());
            kdLabel.setText("Kd: " + String.format("%.0f", newVal.doubleValue()));
        });
        pidGrid.add(new Label("Proportional Gain:"), 0, 0);
        pidGrid.add(kpLabel, 1, 0);
        pidGrid.add(kpSlider, 2, 0);
        pidGrid.add(new Label("Integral Gain:"), 0, 1);
        pidGrid.add(kiLabel, 1, 1);
        pidGrid.add(kiSlider, 2, 1);
        pidGrid.add(new Label("Derivative Gain:"), 0, 2);
        pidGrid.add(kdLabel, 1, 2);
        pidGrid.add(kdSlider, 2, 2);
        pidPane.setContent(pidGrid);
        pane.getChildren().addAll(dashboardPane, controlsPane, pidPane);
    }

    public void startSimulation() {
        if (timer == null) {
            timer = new AnimationTimer() {
                @Override
                public void handle(long now) {
                    updateSimulation();
                    renderDashboard();
                    time += 0.01;
                    if (time >= 1.0) {
                        dataExporter.exportToCSV(physicsEngine, "simulation_data.csv");
                        time = 0.0;
                    }
                }
            };
            timer.start();
            statusLabel.setText("Simulation Running");
        }
    }

    public void stopSimulation() {
        if (timer != null) {
            timer.stop();
            timer = null;
            statusLabel.setText("Simulation Stopped");
        }
    }

    public void resetSimulation() {
        stopSimulation();
        physicsEngine.reset();
        visualization2D.reset();
        time = 0.0;
        statusLabel.setText("Simulation Reset");
        renderDashboard();
    }

    private void updateSimulation() {
        physicsEngine.update();
        double controlForce = controlSystem.update(physicsEngine.getRotorDisplacement(), physicsEngine.getRotorVelocity());
        physicsEngine.applyControlForce(controlForce);
    }

    private void renderDashboard() {
        GraphicsContext gc = dashboardCanvas.getGraphicsContext2D();
        gc.setFill(Color.web("#1E1E1E"));
        gc.fillRect(0, 0, dashboardCanvas.getWidth(), dashboardCanvas.getHeight());

        visualization2D.drawFrictionGauge(gc, physicsEngine.getFrictionForce());
        visualization2D.drawEnergyLossGauge(gc, physicsEngine.getEnergyLoss());
        visualization2D.drawTemperatureGauge(gc, physicsEngine.getTemperature());
        visualization2D.drawRotorDynamics(gc, physicsEngine.getRotorDisplacement());
        visualization2D.drawMagneticField(gc, physicsEngine.getMagneticFieldStrength());
        visualization2D.drawStressMap(gc, physicsEngine.getStress());
    }

    public VBox getPane() {
        return pane;
    }
}