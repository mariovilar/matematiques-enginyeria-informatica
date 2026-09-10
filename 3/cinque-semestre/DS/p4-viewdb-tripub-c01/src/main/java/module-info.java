module ub.edu.cextremvista {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires org.apache.commons.lang3;

    opens ub.edu.view to javafx.fxml;
    exports ub.edu.view;
    exports ub.edu;
    opens ub.edu to javafx.fxml;
}