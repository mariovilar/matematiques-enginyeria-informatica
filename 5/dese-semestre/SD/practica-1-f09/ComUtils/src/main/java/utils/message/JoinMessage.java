package utils.message;

import utils.enums.ErrorType;
import utils.enums.MessageType;
import utils.exceptions.BattleshipException;

/**
 * Represents a join message in the communication protocol.
 * <p>
 * This message is used when a player attempts to join a game.
 * The player's name is included and must not exceed 50 characters.
 * </p>
 *
 * @see Message
 */
public class JoinMessage extends Message {
    
    private String playerName;

    /**
     * Constructs a JoinMessage with the specified player name.
     *
     * @param playerName the name of the player joining the game (maximum 50 characters)
     * @throws BattleshipException if the playerName exceeds 50 characters
     */
    public JoinMessage(String playerName) throws BattleshipException {
        super(MessageType.JOIN);
        int len = playerName.length();
        if (len > 50) {
            throw new BattleshipException(ErrorType.INVALID_PLAYER_NAME);
        }
        this.playerName = playerName;
    }
    
    /**
     * Returns the name of the player.
     *
     * @return the player's name.
     */
    public String getPlayerName() {
        return this.playerName;
    }

    /**
     * Checks whether this JoinMessage is equal to another object.
     * <p>
     * Two JoinMessage objects are considered equal if their message type is
     * {@link MessageType#JOIN} and their player names are identical.
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
        if (message.getType() != MessageType.JOIN) {
            return false;
        }
        JoinMessage joinMessage = (JoinMessage) message;
        return this.playerName.equals(joinMessage.getPlayerName());
    }
}
