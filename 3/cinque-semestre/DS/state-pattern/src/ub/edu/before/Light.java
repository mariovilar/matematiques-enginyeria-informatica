package ub.edu.before;

public class Light {
    private boolean on;

    public void switchOn() {
        if (!on) {
            on = true;
        }
    }

    public void switchOff() {
        if (on) {
            on = false;
        }
    }

    public String getInfo() {
        if(on) {
            return "is on";
        } else {
            return "is of";
        }
    }
}
