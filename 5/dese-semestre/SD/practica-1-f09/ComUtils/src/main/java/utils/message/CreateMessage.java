package utils.message;

import java.util.Arrays;

import utils.enums.ErrorType;
import utils.enums.MessageType;
import utils.exceptions.BattleshipException;

/**
 * Represents a message to create a new game.
 * <p>
 * This message contains the player's name and optional configuration details such as
 * the board dimensions, available vessels, and the AI flag. If configuration details are not provided,
 * default values are used.
 * </p>
 *
 * @see Message
 */
public class CreateMessage extends Message {
    private String playerName;

    /* Default values in case no additional input is provided */
    private byte height = 10;
    private byte width = 10;
    private byte[] vessels = {1,1,1,1,1};
    private byte ai = 1;

    /**
     * Constructs a CreateMessage with only the player name.
     * <p>
     * The player's name is validated to not exceed 50 characters.
     * </p>
     *
     * @param playerName the name of the player.
     * @throws BattleshipException if the player's name is invalid.
     */
    public CreateMessage(String playerName) throws BattleshipException {
        super(MessageType.CREATE);
        int len = playerName.length();
        if (len > 50) {
            throw new BattleshipException(ErrorType.INVALID_PLAYER_NAME);
        }
        this.playerName = playerName;
    }

    /**
     * Constructs a CreateMessage with the specified game configuration.
     *
     * @param playerName the name of the player.
     * @param width      the desired board width.
     * @param height     the desired board height.
     * @param vessels    an array representing the available vessels.
     * @param ai         the AI flag (1 for enabled, 0 for disabled).
     */
    public CreateMessage(String playerName, byte width, byte height, byte[] vessels, byte ai) {
        super(MessageType.CREATE);
        this.playerName = playerName;
        this.height = height;
        this.width = width;
        this.vessels = vessels;
        this.ai = ai;
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
     * Returns the board height.
     *
     * @return the height of the board.
     */
    public byte getHeight() {
        return this.height;
    }

    /**
     * Returns the board width.
     *
     * @return the width of the board.
     */
    public byte getWidth() {
        return this.width;
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
     * Returns the AI flag.
     *
     * @return 1 if AI is enabled; 0 otherwise.
     */
    public byte isAi() {
        return this.ai;
    }

    /**
     * Checks whether this CreateMessage is equal to another object.
     * Two CreateMessage objects are considered equal if their player name, board dimensions,
     * available vessels, and AI flag are all equal.
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
        if (message.getType() != MessageType.CREATE) {
            return false;
        }
        CreateMessage createMessage = (CreateMessage) message;
        return this.playerName.equals(createMessage.getPlayerName()) &&
                this.height == createMessage.getHeight() &&
                this.width == createMessage.getWidth() &&
                Arrays.equals(this.vessels, createMessage.getVessels()) &&
                this.ai == createMessage.isAi();
    }
}
