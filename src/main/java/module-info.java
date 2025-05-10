module code.hybrid_bearing_system.design.hybridbearingsystemdesign {
    requires javafx.controls;
    requires javafx.fxml;


    opens code.hybrid_bearing_system.design to javafx.fxml;
    exports code.hybrid_bearing_system.design;
}