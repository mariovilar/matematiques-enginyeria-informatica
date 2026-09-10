package ub.edu.view;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import ub.edu.model.StatusType;

import java.io.IOException;

public class EscenaLogin extends Escena {

    public Button login_btn;
    public TextField login_correu;
    public PasswordField login_pwd;
    public Button register_btn;
    public Button cancel_btn;
    private String correu;

    public void start(){
        this.correu = this.controller.getSessionMemory().getCorreuPersona();
        if(correu!=null && !correu.equals("")){
            //caso que hemos registrado correctamemnte una nueva persona
            //poner su email pero la pwd a null para escribirla nosotros
            this.login_correu.setText(correu);
            this.login_pwd.setText(null);
        }else{
            //caso que volvemos del registro sin crear una nueva persona
            //por bondad dejamos los campos default para el login en lugar de hacer reset
            //no hacer nada
        }

    }

    @FXML
    protected void onLoginButtonClick() {
        String correu = login_correu.getText();
        String pwd = login_pwd.getText();

        StatusType resultat = controller.loguejarSociStatus(correu, pwd);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        if (resultat == StatusType.CORREU_INEXISTENT || resultat == StatusType.CONTRASENYA_INCORRECTA) {
            //Problema en el login:
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error en el login");
            alert.setContentText(resultat.toString());
        } else {
            //Login amb èxit:
            alert.setTitle("Login exitòs");
            alert.setHeaderText("Login exitòs");
            //Fent això, canviarem l'escena quan es tanqui el diàleg
            alert.setOnCloseRequest(new EventHandler<DialogEvent>() {
                @Override
                public void handle(DialogEvent dialogEvent) {
                    //System.out.println(alert.getResult());
                    String resu_ButtonData = String.valueOf(alert.getResult().getButtonData());
                    if(resu_ButtonData.equals("OK_DONE")){
                        //boton aceptar
                        try {
                            event_goMain(correu);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }else{
                        //boton cancelar
                        //no hacer nada, nos quedamos donde ya estamos
                    }
                }
            });
        }
        alert.showAndWait();
    }


    private void event_goMain (String correuPersona) throws Exception {
        try {
            Escena main = EscenaFactory.INSTANCE.creaEscena("main-view", "TripUB Main View");
            EscenaMain escenaMain = ((EscenaMain) main);
            main.setController(controller);
            this.controller.getSessionMemory().setCorreuPersona(correuPersona);
            escenaMain.start();
            stage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onRegisterWindow(){
        try {
            Escena register = EscenaFactory.INSTANCE.creaEscena("register-view", "TripUB Register View");
            EscenaRegister escenaRegister = ((EscenaRegister) register);
            register.setController(controller);
            escenaRegister.start();
            stage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onCancelButtonClick(){
        System.exit(0);
    }

}