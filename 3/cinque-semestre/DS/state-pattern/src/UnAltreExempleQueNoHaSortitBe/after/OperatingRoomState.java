package UnAltreExempleQueNoHaSortitBe.after;

public interface OperatingRoomState {
    void startSurgery(OperatingRoom operatingRoom);
    void endSurgery(OperatingRoom operatingRoom);
    void finishCleaning(OperatingRoom operatingRoom);
}
