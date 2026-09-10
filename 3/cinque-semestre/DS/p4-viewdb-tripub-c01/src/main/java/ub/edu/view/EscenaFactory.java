package ub.edu.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ub.edu.AppMain;

import java.io.IOException;

public enum EscenaFactory {
    INSTANCE;

    Escena creaEscena (String sceneName, String titol) throws IOException {
        Stage newStage = new Stage();
        //Posem titol a la finestra
        newStage.setTitle(titol);
        //Carreguem escena
        FXMLLoader fxmlLoader = new FXMLLoader(AppMain.class.getResource("/ub/edu/view/" + sceneName + ".fxml"));
        //Carreguem l'escena
        Scene newScene = new Scene(fxmlLoader.load());
        // getController retorna una instància del controlador de la finestra que
        // s'està carregant. NO es el controlador del Model-Vista-Controlador.
        Escena escena = fxmlLoader.getController();
        //Li enviem la finestra (stage) a la nova escena
        escena.setStage(newStage);
        //Enviem el css de l'escena només en cas que existeixi
        java.net.URL resource = AppMain.class.getResource("/ub/edu/view/" + sceneName + ".css");
        if (resource != null) newScene.getStylesheets().add(resource.toExternalForm());

        //Setegem l'escena en el stage
        newStage.setScene(newScene);
        //Mostrem la finestra
        newStage.show();
        return escena;
    }
}
