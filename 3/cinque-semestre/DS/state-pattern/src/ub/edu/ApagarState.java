package ub.edu;

/**
 * @author mario
 */
public class ApagarState extends LlumState {

    @Override
    public void turnOn() {
        System.out.println("hi ha llum al final del túnel...");
        super.llumContext.setElectricLightState(LlumContext.TURN_ON_STATE);
    }

    @Override
    public void turnOff() {
        System.out.println("ja estic apagat i tinc molta por!");
    }

    @Override
    public String getState() {
        return "estic més apagat que l'esperit nadalenc d'un estudiant universitari";
    }
}
