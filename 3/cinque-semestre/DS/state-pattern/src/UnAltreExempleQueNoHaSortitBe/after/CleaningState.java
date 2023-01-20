package UnAltreExempleQueNoHaSortitBe.after;

public class CleaningState implements OperatingRoomState {
    @Override
    public void startSurgery(OperatingRoom operatingRoom) {
        System.out.println("Cannot start surgery: operating room is being cleaned");
    }

    @Override
    public void endSurgery(OperatingRoom operatingRoom) {
        System.out.println("Cannot end surgery: operating room is being cleaned");
    }

    @Override
    public void finishCleaning(OperatingRoom operatingRoom) {
        System.out.println("Cannot finish cleaning: operating room is not being cleaned");
    }
}
