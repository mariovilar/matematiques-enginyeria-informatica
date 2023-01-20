package UnAltreExempleQueNoHaSortitBe.after;

public class FreeState implements OperatingRoomState {
    @Override
    public void startSurgery(OperatingRoom operatingRoom) {
        operatingRoom.setState(operatingRoom.getState());
    }

    @Override
    public void endSurgery(OperatingRoom operatingRoom) {
        System.out.println("Cannot end surgery: operating room is not occupied");
    }

    @Override
    public void finishCleaning(OperatingRoom operatingRoom) {
        System.out.println("Cannot finish cleaning: operating room is not being cleaned");
    }
}
