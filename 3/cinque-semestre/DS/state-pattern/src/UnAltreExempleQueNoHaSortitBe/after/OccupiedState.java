package UnAltreExempleQueNoHaSortitBe.after;

public class OccupiedState implements OperatingRoomState {
    @Override
    public void startSurgery(OperatingRoom operatingRoom) {
        System.out.println("Cannot start surgery: operating room is already occupied");
    }

    @Override
    public void endSurgery(OperatingRoom operatingRoom) {
        operatingRoom.setState(operatingRoom.getState());
    }

    @Override
    public void finishCleaning(OperatingRoom operatingRoom) {
        System.out.println("Cannot finish cleaning: operating room is not being cleaned");
    }
}
