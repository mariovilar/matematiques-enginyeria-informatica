package utils.message;

import java.util.Arrays;
import utils.exceptions.BattleshipException;
import utils.enums.MessageType;

/**
 * Represents a game configuration message in the communication protocol.
 * <p>
 * This message carries the configuration details of the game including the board width,
 * board height, and the available vessels.
 * </p>
 *
 * @see Message
 */
public class GameConfigMessage extends Message {
    private byte w;
    private byte h;
    private byte[] vessels;

    /**
     * Constructs a GameConfigMessage with the specified board dimensions and vessel configuration.
     *
     * @param w       the width of the game board.
     * @param h       the height of the game board.
     * @param vessels an array representing the available vessels.
     * @throws BattleshipException if there is any error in the configuration parameters.
     */
    public GameConfigMessage(byte w, byte h, byte[] vessels) throws BattleshipException {
        super(MessageType.GAMECONFIG);
        this.w = w;
        this.h = h;
        this.vessels = vessels;
    }

    /**
     * Returns the width of the game board.
     *
     * @return the board width as a byte.
     */
    public byte getWidth() {
        return this.w;
    }

    /**
     * Returns the height of the game board.
     *
     * @return the board height as a byte.
     */
    public byte getHeight() {
        return this.h;
    }

    /**
     * Returns the available vessels configuration.
     *
     * @return an array representing the available vessels.
     */
    public byte[] getVessels() {
        return this.vessels;
    }

    /**
     * Checks whether this GameConfigMessage is equal to another object.
     * Two GameConfigMessage instances are considered equal if their message types are
     * {@link MessageType#GAMECONFIG} and their board dimensions and vessel configurations are identical.
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
        if (message.getType() != MessageType.GAMECONFIG) {
            return false;
        }
        GameConfigMessage gameConfigMessage = (GameConfigMessage) message;
        return this.w == gameConfigMessage.getWidth() &&
                this.h == gameConfigMessage.getHeight() &&
                Arrays.equals(this.vessels, gameConfigMessage.getVessels());
    }
}