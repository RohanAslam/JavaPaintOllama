module ca.utoronto.utm.paint {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.json;


    opens ca.paint to javafx.fxml;
    exports ca.paint;
}