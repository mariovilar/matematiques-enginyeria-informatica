package ub.edu;

/**
 * @author mario
 */
public class Test {

    public static void main(String[] args) {
        LlumContext llumContext = new LlumContext();
        llumContext.setElectricLightState(new EncendreState());
        System.out.println(String.format("Estat de la llum: [%s]", llumContext.getElectricLightState().getState()));
        llumContext.turnOn();

        llumContext.turnOff();
        System.out.println(String.format("Estat de la llum: [%s]", llumContext.getElectricLightState().getState()));
    }
}
