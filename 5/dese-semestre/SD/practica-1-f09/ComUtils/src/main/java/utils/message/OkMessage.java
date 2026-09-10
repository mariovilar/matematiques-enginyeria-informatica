package utils.message;

import utils.enums.ErrorType;
import utils.enums.MessageType;
import utils.exceptions.BattleshipException;

/**
 * Represents an OK message in the communication protocol.
 * <p>
 * This message indicates a successful operation and carries the player's and game's identifiers.
 * Both identifiers must be exactly 5 digits.
 * </p>
 *
 * @see Message
 */
public class OkMessage extends Message {

    private int player_id;
    private int game_id;
    
    /**
     * Constructs an OkMessage with the specified player and game identifiers.
     *
     * @param player_id the player's identifier (must be exactly 5 digits)
     * @param game_id   the game's identifier (must be exactly 5 digits)
     * @throws BattleshipException if the player_id or game_id is not exactly 5 digits
     */
    public OkMessage(int player_id, int game_id) throws BattleshipException {
        super(MessageType.OK);

        int pLength = String.valueOf(player_id).length();
        int gLength = String.valueOf(game_id).length();
        if (pLength != 5) {
            throw new BattleshipException(ErrorType.INVALID_PLAYER_ID);
        }
        if (gLength != 5) {
            throw new BattleshipException(ErrorType.INVALID_GAME_ID);
        }
        
        this.player_id = player_id;
        this.game_id = game_id;
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
     * Checks whether this OkMessage is equal to another object.
     * <p>
     * Two OkMessage objects are considered equal if their message type is {@link MessageType#OK}
     * and their player and game identifiers are identical.
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
        if (message.getType() != MessageType.OK) {
            return false;
        }
        OkMessage okMessage = (OkMessage) message;
        return this.player_id == okMessage.getPlayerId() && 
               this.game_id == okMessage.getGameId();
    }
}
