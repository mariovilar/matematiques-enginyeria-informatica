package ub.edu;

import javafx.application.Application;
import javafx.stage.Stage;
import ub.edu.controller.Controller;
import ub.edu.view.Vista;

import java.io.IOException;


public class AppMain extends Application{
    private Controller controller;
    private Vista vista;

    @Override
    public void start(Stage stage) throws IOException {
        // Creacio de la vista, el model i el controlador
        //TODO: Fer els canvis que es creguin necessaris per a fer la creació
        // de controlador, model i vista: Qui crea a qui?
        controller = new Controller();
        vista = new Vista(stage, controller);
    }

    public static void main(String[] args) {
        launch();
    }


}