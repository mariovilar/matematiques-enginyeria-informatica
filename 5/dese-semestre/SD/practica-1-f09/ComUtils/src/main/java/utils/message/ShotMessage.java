package utils.message;

import java.util.Arrays;
import utils.enums.ErrorType;
import utils.enums.MessageType;
import utils.exceptions.BattleshipException;

/**
 * Represents a shot message in the communication protocol.
 * <p>
 * This message is used to communicate a shot action by a player.
 * It contains the player's identifier, the game's identifier, and the target position.
 * The player's and game's identifiers must be exactly 5 digits, and the position must be an array of length 2.
 * </p>
 *
 * @see Message
 */
public class ShotMessage extends Message {
    private int player_id;
    private int game_id;    
    private byte[] position;

    /**
     * Constructs a ShotMessage with the specified player and game identifiers, and target position.
     *
     * @param player_id the player's identifier (must be exactly 5 digits)
     * @param game_id   the game's identifier (must be exactly 5 digits)
     * @param position  an array representing the target position (must have length 2)
     * @throws BattleshipException if the player_id or game_id is not exactly 5 digits,
     *                             or if the position array length is not 2
     */
    public ShotMessage(int player_id, int game_id, byte[] position) throws BattleshipException {
        super(MessageType.SHOT);

        int pLength = String.valueOf(player_id).length();
        int gLength = String.valueOf(game_id).length();
        if (pLength != 5) {
            throw new BattleshipException(ErrorType.INVALID_PLAYER_ID);
        }
        if (gLength != 5) {
            throw new BattleshipException(ErrorType.INVALID_GAME_ID);
        }
        if (position.length != 2) {
            throw new BattleshipException(ErrorType.INVALID_COORDINATE);
        }
        this.player_id = player_id;
        this.game_id = game_id;
        this.position = position;
    }

    /**
     * Returns the player's identifier.
     *
     * @return the player's id.
     */
    public int getPlayerId() {
        return this.player_id;
    }

    /**
     * Returns the game's identifier.
     *
     * @return the game id.
     */
    public int getGameId() {
        return this.game_id;
    }

    /**
     * Returns the target position of the shot.
     *
     * @return a byte array representing the target position.
     */
    public byte[] getPosition() {
        return this.position;
    }

    /**
     * Checks whether this ShotMessage is equal to another object.
     * <p>
     * Two ShotMessage objects are considered equal if their message type is {@link MessageType#SHOT},
     * and their player identifiers, game identifiers, and target positions are identical.
     * </p>
     *
     * @param obj the object to compare with.
     * @return true if the messages are equal; false otherwise.
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
        if (message.getType() != MessageType.SHOT) {
            return false;
        }
        ShotMessage shotMessage = (ShotMessage) message;
        return this.player_id == shotMessage.getPlayerId() && 
               this.game_id == shotMessage.getGameId() &&
               Arrays.equals(this.position, shotMessage.getPosition());
    }
}
