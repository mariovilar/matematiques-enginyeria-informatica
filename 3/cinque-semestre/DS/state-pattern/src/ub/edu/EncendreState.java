package ub.edu;

/**
 * @author mario
 */
public class EncendreState extends LlumState {

    @Override
    public void turnOn() {
        System.out.println("ja estic encès");
    }

    @Override
    public void turnOff() {
        System.out.println("apagant la llum...");
        super.llumContext.setElectricLightState(LlumContext.TURN_OFF_STATE);
    }

    @Override
    public String getState() {
        return "quanta llum! sembla lloret";
    }
}
