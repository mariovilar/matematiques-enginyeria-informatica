package UnAltreExempleQueNoHaSortitBe.after;

public class Test {

    public static void main(String[] args) {
        OperatingRoom operatingRoom = new OperatingRoom();
        operatingRoom.setState(new FreeState());
        System.out.println(String.format("Estat de la llum: [%s]", operatingRoom.getState()));

        operatingRoom.setState(new CleaningState());
        operatingRoom.finishCleaning();

        System.out.println(String.format("Estat de la llum: [%s]", operatingRoom.getState()));
    }
}
