package ub.edu.view;

import javafx.stage.Stage;
import ub.edu.controller.Controller;

import java.io.IOException;

public class Vista {
    protected Controller controller;
    public Vista(Stage stage, Controller controller) throws IOException {
        this.controller = controller;
        //Posem titol a la finestra

        Escena login = EscenaFactory.INSTANCE.creaEscena("login-view", "TripUB Login View");

        //Li enviem la finestra (stage) i el controlador a la nova escena
        login.setController(controller);

    }
}
