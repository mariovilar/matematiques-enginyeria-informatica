package ub.edu;

/**
 * @author mario
 * Fem una classe abstracta d'estat per ser més nets en el codi
 */
public abstract class LlumState {

    protected LlumContext llumContext;

    public void setElectricLightContext(LlumContext llumContext) {
        this.llumContext = llumContext;
    }
    public abstract void turnOn();
    public abstract void turnOff();
    public abstract String getState();
}
