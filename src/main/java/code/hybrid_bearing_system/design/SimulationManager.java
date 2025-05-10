package code.hybrid_bearing_system.design;

public class SimulationManager {
    private ConfigurationPanel configPanel;
    private SimulationDashboard simDashboard;
    private AnalysisPanel analysisPanel;
    private BearingModel bearingModel;
    private PhysicsEngine physicsEngine;
    private ControlSystem controlSystem;
    private DataExporter dataExporter;

    public SimulationManager() {
        bearingModel = new BearingModel();
        physicsEngine = new PhysicsEngine(bearingModel);
        controlSystem = new ControlSystem();
        dataExporter = new DataExporter();
        configPanel = new ConfigurationPanel(bearingModel, this);
        simDashboard = new SimulationDashboard(bearingModel, physicsEngine, controlSystem, dataExporter);
        analysisPanel = new AnalysisPanel(bearingModel, physicsEngine);
    }

    public void startSimulation() {
        simDashboard.startSimulation();
    }

    public void stopSimulation() {
        simDashboard.stopSimulation();
    }

    public void resetSimulation() {
        simDashboard.resetSimulation();
        physicsEngine.reset();
    }

    public ConfigurationPanel getConfigurationPanel() {
        return configPanel;
    }

    public SimulationDashboard getSimulationDashboard() {
        return simDashboard;
    }

    public AnalysisPanel getAnalysisPanel() {
        return analysisPanel;
    }
}