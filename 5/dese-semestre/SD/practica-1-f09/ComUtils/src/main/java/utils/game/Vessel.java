package utils.game;

/**
 * Represents a vessel in a battleship style game.
 * <p>
 * A Vessel is defined by its type and the positions it occupies on the game board.
 * The type indicates the vessel's size and a valid type must be between 1 and 5.
 * The positions are stored as a byte array of length 2, representing the coordinates.
 * </p>
 */
public class Vessel {
    private byte[] initialPosition;
    private byte[] finalPosition;
    private byte type;

    /**
     * Constructs a Vessel with the specified type and positions.
     *
     * @param type            the type of the vessel (must be between 1 and 5)
     * @param initialPosition the starting coordinates of the vessel (length must be 2)
     * @param finalPosition   the ending coordinates of the vessel (length must be 2)
     * @throws Exception if the positions are invalid or the type is not in the valid range
     */
    public Vessel(byte type, byte[] initialPosition, byte[] finalPosition) throws Exception {
        if(initialPosition.length != 2 || finalPosition.length != 2) {
            throw new IllegalArgumentException("Invalid vessel position");
        }
        if(type < 1 || type > 5) {
            throw new IllegalArgumentException("Invalid vessel type");
        }
        this.initialPosition = initialPosition;
        this.finalPosition = finalPosition;
        this.type = type;
    }

    /**
     * Returns the initial position of the vessel.
     *
     * @return a byte array representing the starting coordinates of the vessel
     */
    public byte[] getInitialPosition() {
        return this.initialPosition;
    }

    /**
     * Returns the final position of the vessel.
     *
     * @return a byte array representing the ending coordinates of the vessel
     */
    public byte[] getFinalPosition() {
        return this.finalPosition;
    }

    /**
     * Returns the type of the vessel.
     *
     * @return the type of the vessel as a byte
     */
    public byte getType() {
        return this.type;
    }
}
