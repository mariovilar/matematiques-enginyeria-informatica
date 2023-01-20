package UnAltreExempleQueNoHaSortitBe.abans;

public class OperatingRoom {
    private boolean occupied;
    private boolean cleaning;

    public void startSurgery() {
        if (!occupied && !cleaning) {
            occupied = true;
        }
    }

    public void endSurgery() {
        if (occupied && !cleaning) {
            occupied = false;
            cleaning = true;
        }
    }

    public void finishCleaning() {
        if (!occupied && cleaning) {
            cleaning = false;
        }
    }
}
