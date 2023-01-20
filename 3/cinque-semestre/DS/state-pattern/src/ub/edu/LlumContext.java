package ub.edu;

/**
 * @author mario
 */
public class LlumContext {

    private LlumState llumState;

    public final static EncendreState TURN_ON_STATE = new EncendreState();

    public final static ApagarState TURN_OFF_STATE = new ApagarState();

    public LlumState getElectricLightState() {
        return llumState;
    }

    public void setElectricLightState(LlumState llumState) {
        this.llumState = llumState;
        this.llumState.setElectricLightContext(this);
    }

    public void turnOn() {
        this.llumState.turnOn();
    }

    public void turnOff() {
        this.llumState.turnOff();
    }
}
