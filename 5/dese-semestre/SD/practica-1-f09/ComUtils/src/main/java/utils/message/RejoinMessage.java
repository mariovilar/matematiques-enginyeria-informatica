package utils.message;

import utils.enums.ErrorType;
import utils.enums.MessageType;
import utils.exceptions.BattleshipException;

/**
 * Represents a rejoin message in the communication protocol.
 * <p>
 * This message is used when a player attempts to rejoin an ongoing game.
 * The player's name is validated to not exceed 50 characters.
 * </p>
 *
 * @see Message
 */
public class RejoinMessage extends Message {
    private String playerName;

    /**
     * Constructs a RejoinMessage with the specified player name.
     *
     * @param playerName the name of the player attempting to rejoin (maximum 50 characters)
     * @throws BattleshipException if the playerName exceeds 50 characters
     */
    public RejoinMessage(String playerName) throws BattleshipException {
        super(MessageType.REJOIN);

        int len = playerName.length();
        if (len > 50) {
            throw new BattleshipException(ErrorType.INVALID_PLAYER_NAME);
        }
    
        this.playerName = playerName;
    }
    
    /**
     * Returns the player's name.
     *
     * @return the player's name.
     */
    public String getPlayerName() {
        return this.playerName;
    }

    /**
     * Checks whether this RejoinMessage is equal to another object.
     * <p>
     * Two RejoinMessage objects are considered equal if their message type is
     * {@link MessageType#REJOIN} and their player names are identical.
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
         if (message.getType() != MessageType.REJOIN) {
             return false;
         }
         RejoinMessage rejoinMessage = (RejoinMessage) message;
         return this.playerName.equals(rejoinMessage.getPlayerName());
    }
}
