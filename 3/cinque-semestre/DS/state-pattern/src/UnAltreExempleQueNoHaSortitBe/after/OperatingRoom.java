package UnAltreExempleQueNoHaSortitBe.after;

public class OperatingRoom {
    private OperatingRoomState freeState;
    private OperatingRoomState occupiedState;
    private OperatingRoomState cleaningState;
    private OperatingRoomState state;

    public OperatingRoom() {
        freeState = new FreeState();
        occupiedState = new OccupiedState();
        cleaningState = new CleaningState();
        state = freeState;
    }

    public void startSurgery() {
        state.startSurgery(this);
    }

    public void endSurgery() {
        state.endSurgery(this);
    }

    public void finishCleaning() {
        state.finishCleaning(this);
    }

    public OperatingRoomState getState() {
        return freeState;
    }

    public void setState(OperatingRoomState state) {
        this.state = state;
    }
}
