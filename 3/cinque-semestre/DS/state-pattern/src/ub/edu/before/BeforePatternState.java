package ub.edu.before;

public class BeforePatternState {

    public static void main() {
        Light light = new Light();
        light.switchOn();
        System.out.println(light.getInfo());
        light.switchOff();
        System.out.println(light.getInfo());
    }
}
