package code.hybrid_bearing_system.design;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        SimulationManager manager = new SimulationManager();
        BorderPane root = new BorderPane();
        /// Header
        HBox header = new HBox(10);
        header.setStyle("-fx-background-color: #1E1E1E; -fx-padding: 10;");
        /// Tabs
        TabPane tabPane = new TabPane();
        tabPane.setStyle("-fx-background-color: #1E1E1E;");
        Tab configTab = new Tab("Configuration", manager.getConfigurationPanel().getPane());
        Tab simTab = new Tab("Simulation", manager.getSimulationDashboard().getPane());
        Tab analysisTab = new Tab("Analysis", manager.getAnalysisPanel().getPane());
        configTab.setClosable(false);
        simTab.setClosable(false);
        analysisTab.setClosable(false);
        tabPane.getTabs().addAll(configTab, simTab, analysisTab);
        root.setTop(header);
        root.setCenter(tabPane);
        Scene scene = new Scene(root, 1200, 800);
        scene.setFill(Color.web("#1E1E1E"));
        /// Inline CSS
        String css = """
            .root {
                -fx-background-color: #1E1E1E;
            }
            .label {
                -fx-text-fill: #FFFFFF;
                -fx-font-size: 12px;
            }
            .button {
                -fx-background-color: #333333;
                -fx-text-fill: #FFFFFF;
                -fx-border-color: #FFFFFF;
                -fx-border-width: 1;
                -fx-padding: 5 10 5 10;
            }
            .button:hover {
                -fx-background-color: #555555;
            }
            .text-field {
                -fx-background-color: #333333;
                -fx-text-fill: #FFFFFF;
                -fx-border-color: #FFFFFF;
                -fx-border-width: 1;
            }
            .combo-box {
                -fx-background-color: #333333;
                -fx-text-fill: #FFFFFF;
            }
            .combo-box .list-cell {
                -fx-background-color: #333333;
                -fx-text-fill: #FFFFFF;
            }
            .combo-box-popup .list-view .list-cell:hover {
                -fx-background-color: #555555;
            }
            .slider .track {
                -fx-background-color: #333333;
            }
            .slider .thumb {
                -fx-background-color: #FFFFFF;
            }
            .slider .axis {
                -fx-tick-label-fill: #FFFFFF;
            }
            .titled-pane > .title {
                -fx-background-color: #333333;
                -fx-text-fill: #FFFFFF;
            }
            .titled-pane > .content {
                -fx-background-color: #1E1E1E;
                -fx-border-color: #FFFFFF;
                -fx-border-width: 1;
            }
            .tab-pane .tab-header-area .tab {
                -fx-background-color: #333333;
                -fx-text-fill: #FFFFFF;
            }
            .tab-pane .tab-header-area .tab:selected {
                -fx-background-color: #555555;
            }
            .tab-pane .tab-header-background {
                -fx-background-color: #1E1E1E;
            }
            """;
        try {
            String encodedCss = URLEncoder.encode(css, StandardCharsets.UTF_8.toString());
            scene.getStylesheets().add("data:text/css," + encodedCss);
        } catch (Exception e) {
            System.err.println("Warning: Failed to apply inline CSS. Proceeding with default styling.");
        }
        primaryStage.setTitle("Hybrid Bearing System Simulation");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}