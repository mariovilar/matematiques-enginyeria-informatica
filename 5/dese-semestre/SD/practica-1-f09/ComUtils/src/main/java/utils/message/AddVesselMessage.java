package utils.message;

import java.util.Arrays;

import utils.enums.ErrorType;
import utils.enums.MessageType;
import utils.exceptions.BattleshipException;

/**
 * Represents a message to add a vessel to the game.
 * <p>
 * This message contains the player ID, game ID, vessel type,
 * and the initial and final positions of the vessel. The IDs must be exactly 5 digits,
 * the vessel type must be between 1 and 5, and the positions must be arrays of length 2.
 * </p>
 *
 * @see Message
 */
public class AddVesselMessage extends Message {
    private int player_id;
    private int game_id;
    private byte vessel_type;
    private byte[] initial_position;
    private byte[] final_position;

    /**
     * Constructs an AddVesselMessage with the specified parameters.
     *
     * @param player_id        the player's identifier (must be exactly 5 digits)
     * @param game_id          the game's identifier (must be exactly 5 digits)
     * @param type             the type of the vessel (must be between 1 and 5)
     * @param initial_position the initial coordinates of the vessel (array of length 2)
     * @param final_position   the final coordinates of the vessel (array of length 2)
     * @throws BattleshipException if any parameter is invalid
     */
    public AddVesselMessage(int player_id, int game_id, byte type, byte[] initial_position, byte[] final_position) throws BattleshipException {
        super(MessageType.ADDVESSEL);
        int pLength = String.valueOf(player_id).length();
        int gLength = String.valueOf(game_id).length();
        if (pLength != 5) {
            throw new BattleshipException(ErrorType.INVALID_PLAYER_ID);
        }
        if (gLength != 5) {
            throw new BattleshipException(ErrorType.INVALID_GAME_ID);
        }
        if (type < 1 || type > 5) {
            throw new BattleshipException(ErrorType.TYPE_NOT_AVAILABLE);
        }
        if (initial_position.length != 2 || final_position.length != 2) {
            throw new BattleshipException(ErrorType.INVALID_COORDINATE);
        }
        this.player_id = player_id;
        this.game_id = game_id;
        this.vessel_type = type;
        this.initial_position = initial_position;
        this.final_position = final_position;
    }

    /**
     * Returns the player ID associated with this message.
     *
     * @return the player's identifier.
     */
    public int getPlayerId() {
        return this.player_id;
    }

    /**
     * Returns the game ID associated with this message.
     *
     * @return the game's identifier.
     */
    public int getGameId() {
        return this.game_id;
    }

    /**
     * Returns the type of the vessel.
     *
     * @return the vessel type as a byte.
     */
    public byte getVesselType() {
        return this.vessel_type;
    }

    /**
     * Returns the initial position of the vessel.
     *
     * @return a byte array of length 2 representing the initial coordinates.
     */
    public byte[] getInitialPosition() {
        return this.initial_position;
    }

    /**
     * Returns the final position of the vessel.
     *
     * @return a byte array of length 2 representing the final coordinates.
     */
    public byte[] getFinalPosition() {
        return this.final_position;
    }

    /**
     * Returns a string representation of the message.
     *
     * @return a string containing the message type, player ID, game ID, vessel type,
     *         and the vessel's positions.
     */
    public String toString() {
        return super.toString() + " " + this.player_id + " " + this.game_id + " " + this.vessel_type
                + " " + Arrays.toString(this.initial_position) + " " + Arrays.toString(this.final_position);
    }

    /**
     * Checks whether this message is equal to another object.
     * Two AddVesselMessage objects are considered equal if all their fields are equal.
     *
     * @param obj the object to compare with.
     * @return true if the messages are equal, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        Message message = (Message) obj;
        if (message.getType() != MessageType.ADDVESSEL) {
            return false;
        }
        AddVesselMessage addVesselMessage = (AddVesselMessage) message;
        return this.player_id == addVesselMessage.getPlayerId() &&
               this.game_id == addVesselMessage.getGameId() &&
               this.vessel_type == addVesselMessage.getVesselType() &&
               Arrays.equals(this.initial_position, addVesselMessage.getInitialPosition()) &&
               Arrays.equals(this.final_position, addVesselMessage.getFinalPosition());
    }
}
