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
import javafx.scene.input.MouseEvent;

public class ConfigurationPanel {
    private VBox pane;
    private ComboBox<String> bearingTypeCombo;
    private TextField speedField, loadField, materialField;
    private Canvas assemblyCanvas;
    private BearingModel bearingModel;
    private SimulationManager manager;
    private double dragX, dragY;
    private boolean dragging;
    private String selectedComponent;
    private Label statusLabel;
    private double rotationAngle;
    private double vibrationXOffset, vibrationYOffset;
    private AnimationTimer canvasTimer;
    private ToggleButton pauseButton;
    private Button resetAngleButton;
    private boolean paused;
    private boolean showBearingTooltip;
    private boolean showRotorTooltip;
    private double mouseX, mouseY;

    public ConfigurationPanel(BearingModel bearingModel, SimulationManager manager) {
        this.bearingModel = bearingModel;
        this.manager = manager;
        pane = new VBox(10);
        pane.setPadding(new Insets(10));
        pane.setStyle("-fx-background-color: #1E1E1E;");
        dragging = false;
        selectedComponent = null;
        rotationAngle = 0.0;
        vibrationXOffset = 0.0;
        vibrationYOffset = 0.0;
        paused = false;
        showBearingTooltip = false;
        showRotorTooltip = false;
        mouseX = 0;
        mouseY = 0;
        /// Parameters
        TitledPane paramsPane = new TitledPane();
        paramsPane.setText("Bearing Parameters");
        GridPane paramsGrid = new GridPane();
        paramsGrid.setHgap(10);
        paramsGrid.setVgap(10);
        paramsGrid.setPadding(new Insets(10));

        bearingTypeCombo = new ComboBox<>();
        bearingTypeCombo.getItems().addAll("Magnetic", "Ceramic", "Hybrid");
        bearingTypeCombo.setValue("Hybrid");
        bearingTypeCombo.setTooltip(new Tooltip("Select bearing type"));

        speedField = new TextField("10000");
        speedField.setTooltip(new Tooltip("Spindle speed (500-20000 RPM)"));
        loadField = new TextField("500");
        loadField.setTooltip(new Tooltip("Load (100-1000 N)"));
        materialField = new TextField("380");
        materialField.setTooltip(new Tooltip("Young's Modulus (100-500 GPa)"));

        paramsGrid.add(new Label("Bearing Type:"), 0, 0);
        paramsGrid.add(bearingTypeCombo, 1, 0);
        paramsGrid.add(new Label("Spindle Speed (RPM):"), 0, 1);
        paramsGrid.add(speedField, 1, 1);
        paramsGrid.add(new Label("Load (N):"), 0, 2);
        paramsGrid.add(loadField, 1, 2);
        paramsGrid.add(new Label("Young's Modulus (GPa):"), 0, 3);
        paramsGrid.add(materialField, 1, 3);

        paramsPane.setContent(paramsGrid);

        /// Assembly
        TitledPane assemblyPane = new TitledPane();
        assemblyPane.setText("Assembly Builder");
        assemblyCanvas = new Canvas(300, 400);
        drawAssembly();

        assemblyCanvas.setOnMousePressed(this::handleDragStart);
        assemblyCanvas.setOnMouseDragged(this::handleDrag);
        assemblyCanvas.setOnMouseReleased(this::handleDragEnd);
        assemblyCanvas.setOnMouseMoved(this::handleMouseMove);

        VBox assemblyBox = new VBox(10, assemblyCanvas);
        assemblyBox.setAlignment(Pos.CENTER);
        assemblyPane.setContent(assemblyBox);

        /// Controls
        Button applyButton = new Button("Apply Configuration");
        applyButton.setOnAction(e -> applyConfiguration());
        applyButton.setTooltip(new Tooltip("Apply settings"));

        pauseButton = new ToggleButton("Pause");
        pauseButton.setOnAction(e -> togglePause());
        pauseButton.setTooltip(new Tooltip("Pause or resume the rotor animation"));

        resetAngleButton = new Button("Reset Angle");
        resetAngleButton.setOnAction(e -> resetAnimationAngle());
        resetAngleButton.setTooltip(new Tooltip("Reset rotor rotation angle to zero"));
        resetAngleButton.setDisable(true); // Enabled only when paused

        statusLabel = new Label("Ready");

        VBox controlsBox = new VBox(10, applyButton, pauseButton, resetAngleButton, statusLabel);
        controlsBox.setAlignment(Pos.CENTER);

        pane.getChildren().addAll(paramsPane, assemblyPane, controlsBox);

        canvasTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!paused) {
                    rotationAngle += bearingModel.getSpindleSpeed() * 0.00001; // Scale rotation by speed
                    double vibrationAmplitude = Math.min(bearingModel.getSpindleSpeed() / 2000 + bearingModel.getLoad() / 200, 5); // Max 5px
                    vibrationXOffset = Math.sin(now / 2e8) * vibrationAmplitude * 0.5; // Slower X oscillation
                    vibrationYOffset = Math.cos(now / 2e8) * vibrationAmplitude; // Y oscillation
                    drawAssembly();
                }
            }
        };
        canvasTimer.start();
    }

    private void togglePause() {
        paused = pauseButton.isSelected();
        pauseButton.setText(paused ? "Resume" : "Pause");
        resetAngleButton.setDisable(!paused);
        statusLabel.setText(paused ? "Paused" : "Ready");
        if (!paused) {
            drawAssembly();
        }
    }

    private void resetAnimationAngle() {
        if (paused) {
            rotationAngle = 0.0;
            vibrationXOffset = 0.0;
            vibrationYOffset = 0.0;
            drawAssembly();
            statusLabel.setText("Paused (Angle Reset)");
        }
    }

    private void handleMouseMove(MouseEvent event) {
        mouseX = event.getX();
        mouseY = event.getY();
        double bearingY = bearingModel.getBearingY();
        double bearingHeight = 50 - Math.min(bearingModel.getLoad() / 100, 10);
        double rotorY = bearingModel.getRotorY();

        showBearingTooltip = (mouseX >= 50 && mouseX <= 250 && mouseY >= bearingY && mouseY <= bearingY + bearingHeight);
        showRotorTooltip = (mouseX >= 100 && mouseX <= 200 && mouseY >= rotorY - 10 && mouseY <= rotorY + 10);

        if (!dragging) {
            drawAssembly();
        }
    }

    private void drawAssembly() {
        GraphicsContext gc = assemblyCanvas.getGraphicsContext2D();
        gc.setFill(Color.web("#1E1E1E"));
        gc.fillRect(0, 0, assemblyCanvas.getWidth(), assemblyCanvas.getHeight());
        gc.setStroke(Color.web("#555555"));
        for (int y = 0; y <= 400; y += 20) {
            gc.strokeLine(0, y, 300, y);
        }
        /// Bearing visualization based on type, load, and modulus
        double bearingY = snapToGrid(bearingModel.getBearingY());
        double bearingHeight = 50 - Math.min(bearingModel.getLoad() / 100, 10);
        double modulus = bearingModel.getYoungsModulus();
        double borderWidth = 1 + (modulus - 100) / 400 * 3;
        String bearingType = bearingModel.getBearingType();
        switch (bearingType) {
            case "Magnetic":
                gc.setFill(Color.web("#AA00FF")); // Purple for magnetic
                break;
            case "Ceramic":
                gc.setFill(Color.web("#00BFFF")); // Blue for ceramic
                break;
            case "Hybrid":
                gc.setFill(Color.web("#FFAA00")); // Orange for hybrid
                break;
        }
        gc.fillRect(50, bearingY, 200, bearingHeight);
        gc.setStroke(Color.web("#FFFFFF"));
        gc.setLineWidth(borderWidth);
        gc.strokeRect(50, bearingY, 200, bearingHeight);
        /// Stress indicators: horizontal lines based on load and modulus
        double stressDensity = bearingModel.getLoad() / 1000; // 0 to 1
        double stressLineThickness = Math.max(0.5, 2.5 * (500 / modulus));
        int numStressLines = (int) (stressDensity * 10);
        gc.setStroke(Color.web("#FF5555", 0.6));
        gc.setLineWidth(stressLineThickness);
        for (int i = 1; i <= numStressLines; i++) {
            double y = bearingY + (i * bearingHeight / (numStressLines + 1));
            gc.strokeLine(50, y, 250, y);
        }
        gc.setFill(Color.web("#FFFFFF"));
        gc.fillText("Bearing (" + bearingType + ")", 50, bearingY - 5);
        /// Rotor visualization with rotation and vibration
        double rotorY = snapToGrid(bearingModel.getRotorY()) + (paused ? 0 : vibrationYOffset);
        double rotorXOffset = paused ? 0 : vibrationXOffset;
        gc.save();
        gc.translate(150 + rotorXOffset, rotorY + 10); // Center of rotor with vibration
        if (!paused) {
            gc.rotate(Math.toDegrees(rotationAngle % (2 * Math.PI)));
        }
        gc.setFill(Color.web("#FF5555")); // Red for rotor
        gc.fillRect(-50, -10, 100, 20);
        gc.setStroke(Color.web("#FFFFFF"));
        gc.setLineWidth(1);
        gc.strokeRect(-50, -10, 100, 20);
        gc.restore();
        gc.setFill(Color.web("#FFFFFF"));
        gc.fillText("Rotor", 100 + rotorXOffset, rotorY - 15);

        if (showBearingTooltip && !dragging) {
            String tooltipText = String.format("Type: %s\nLoad: %.0f N\nModulus: %.0f GPa\nY: %.0f",
                    bearingType, bearingModel.getLoad(), bearingModel.getYoungsModulus(), bearingY);
            double tx = Math.min(mouseX + 10, 170);
            double ty = Math.max(mouseY - 50, 10);
            gc.setFill(Color.web("#000000", 0.5));
            gc.fillRect(tx + 2, ty + 2, 130, 80);
            gc.setFill(Color.web("#333333"));
            gc.fillRect(tx, ty, 130, 80);
            gc.setStroke(Color.web("#FFFFFF"));
            gc.strokeRect(tx, ty, 130, 80);
            gc.setFill(Color.web("#FFFFFF"));
            gc.fillText(tooltipText, tx + 5, ty + 15);
        }
        if (showRotorTooltip && !dragging) {
            String tooltipText = String.format("Speed: %.0f RPM\nY: %.0f", bearingModel.getSpindleSpeed(), rotorY);
            double tx = Math.min(mouseX + 10, 190); // Avoid right edge
            double ty = Math.min(Math.max(mouseY + 10, 10), 350); // Avoid top/bottom edges
            gc.setFill(Color.web("#000000", 0.5)); // Shadow
            gc.fillRect(tx + 2, ty + 2, 110, 50);
            gc.setFill(Color.web("#333333"));
            gc.fillRect(tx, ty, 110, 50);
            gc.setStroke(Color.web("#FFFFFF"));
            gc.strokeRect(tx, ty, 110, 50);
            gc.setFill(Color.web("#FFFFFF"));
            gc.fillText(tooltipText, tx + 5, ty + 15);
        }
    }

    private double snapToGrid(double y) {
        return Math.round(y / 20.0) * 20.0;
    }

    private void handleDragStart(MouseEvent event) {
        double x = event.getX();
        double y = event.getY();
        double bearingY = bearingModel.getBearingY();
        double bearingHeight = 50 - Math.min(bearingModel.getLoad() / 100, 10);

        if (x >= 50 && x <= 250 && y >= bearingY && y <= bearingY + bearingHeight) {
            selectedComponent = "bearing";
            dragging = true;
            dragX = x;
            dragY = y;
            statusLabel.setText("Dragging Bearing");
        } else if (x >= 100 && x <= 200 && y >= bearingModel.getRotorY() - 10 && y <= bearingModel.getRotorY() + 10) {
            selectedComponent = "rotor";
            dragging = true;
            dragX = x;
            dragY = y;
            statusLabel.setText("Dragging Rotor");
        }
    }

    private void handleDrag(MouseEvent event) {
        if (dragging) {
            double newY = event.getY();
            if (selectedComponent.equals("bearing")) {
                bearingModel.setBearingY(snapToGrid(newY - (dragY - bearingModel.getBearingY())));
            } else if (selectedComponent.equals("rotor")) {
                bearingModel.setRotorY(snapToGrid(newY - (dragY - bearingModel.getRotorY())));
            }
            drawAssembly();
        }
    }

    private void handleDragEnd(MouseEvent event) {
        dragging = false;
        selectedComponent = null;
        statusLabel.setText(paused ? "Paused" : "Ready");
    }

    private void applyConfiguration() {
        try {
            String bearingType = bearingTypeCombo.getValue();
            double speed = Double.parseDouble(speedField.getText());
            double load = Double.parseDouble(loadField.getText());
            double youngsModulus = Double.parseDouble(materialField.getText());

            if (speed < 500 || speed > 20000) {
                statusLabel.setText("Error: Speed must be 500-20000 RPM");
                return;
            }
            if (load < 100 || load > 1000) {
                statusLabel.setText("Error: Load must be 100-1000 N");
                return;
            }
            if (youngsModulus < 100 || youngsModulus > 500) {
                statusLabel.setText("Error: Young's Modulus must be 100-500 GPa");
                return;
            }

            bearingModel.setBearingType(bearingType);
            bearingModel.setSpindleSpeed(speed);
            bearingModel.setLoad(load);
            bearingModel.setYoungsModulus(youngsModulus);

            statusLabel.setText("Configuration Applied" + (paused ? " (Paused)" : ""));
            manager.resetSimulation();
            drawAssembly();
        } catch (NumberFormatException e) {
            statusLabel.setText("Error: Invalid numeric input");
        }
    }

    public VBox getPane() {
        return pane;
    }

    public BearingModel getBearingModel() {
        return bearingModel;
    }
}