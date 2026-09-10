package ub.edu.view;

import javafx.stage.Stage;
import ub.edu.controller.Controller;

public abstract class Escena {
    protected Stage stage;
    protected Controller controller;

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void setStage(Stage newStage) {
        this.stage = newStage;
    }
}
