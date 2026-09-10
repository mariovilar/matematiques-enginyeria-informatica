package ub.edu.view;

import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.text.Text;


public class EscenaReservarAllotjament extends Escena{
    public Text nomAllotjamant_text;
    public Text preuPerNitAllotjament_text;
    public Text preuEsmorzar_text;
    public Text preuMP_text;
    public Text preuPC_text;
    public RadioButton radioButton_Group1_Text1;
    public RadioButton radioButton_Group1_Text2;
    public RadioButton radioButton_Group1_Text3;
    public Button button_reservar;
    public Button button_cancel;
    private String correu_persona;
    private Integer id_ruta;
    private Integer id_tram_etapa;
    private Integer id_allotjament;


    public void start() throws Exception {
        System.out.println("Entro en Reservar Allotjament");
        this.correu_persona=this.controller.getSessionMemory().getCorreuPersona();
        this.id_ruta=this.controller.getSessionMemory().getIdRuta();
        this.id_tram_etapa=this.controller.getSessionMemory().getIdTram();
        this.id_allotjament=this.controller.getSessionMemory().getIdAllotjament();
        initialize_RB();
    }

    private void initialize_RB() {
        ToggleGroup group = new ToggleGroup();
        radioButton_Group1_Text1.setToggleGroup(group);
        radioButton_Group1_Text2.setToggleGroup(group);
        radioButton_Group1_Text3.setToggleGroup(group);
    }

    public void onButtonReservarClick(){
        //enviar la reserva
        System.out.println("TODO: Entro a enviar una reserva");

        //TODO:
        // La idea es: guardar la reserva en el modelo y actualizar la vista en caso necesario
        // Para ello:
        // 1-Recoger los datos seleccionados de la vista (cómo se recogen? ver código de más abajo)
        // 2-Conectar con el controller pasandole los parametros necesarios para que
        // el controller el modelo (y opcionalmente se ejecute el metodo add de los respectivos DAO_Reserva_DB)
        // 3- Refrescar la vista si es necesario

        // Aqui tienes un código de ejemplo para ver cómo recoger el valor de un radio button

        String typeReserva="NULL";

        if (radioButton_Group1_Text1.isSelected()) {
            typeReserva = radioButton_Group1_Text1.getText();
        }else{
            //TODO: recoger los otros tipos de reserva
            /** Your Code Here **/

            System.out.println("TODO: recollir altres tipus d'Allotjament");
        }
        System.out.println("Reserva de tipus: "+ typeReserva);

        //TODO: hacer efectiva la reserva Allotjament -> controller...
        /** Your Code Here **/


        stage.close();
    }

    public void onButtonCancelarClick(){
        //cancelar la reserva
        System.out.println("Entro en cancelar una reserva");
        stage.close();
    }

}


