package utils.message;

import java.util.Arrays;
import utils.enums.ErrorType;
import utils.enums.GameState;
import utils.enums.MessageType;
import utils.exceptions.BattleshipException;

/**
 * Represents a game status message in the communication protocol.
 * <p>
 * This message carries the current game state, board size, and the status of two boards
 * along with additional information.
 * </p>
 *
 * @see Message
 */
public class GameStatusMessage extends Message {
    private byte gameState;
    private int boardSize;
    private byte[] board1;
    private byte[] board2;
    private byte[] info;

    /**
     * Constructs a GameStatusMessage with the specified parameters.
     *
     * @param gameState the current game state as a byte.
     *                  It must correspond to a valid {@link GameState} value.
     * @param boardSize the total size of the game board.
     * @param board1    an array representing the first board's status.
     * @param board2    an array representing the second board's status.
     * @param info      additional information as a byte array.
     * @throws BattleshipException if the provided game state is invalid.
     */
    public GameStatusMessage(byte gameState, int boardSize, byte[] board1, byte[] board2, byte[] info) throws BattleshipException {
        super(MessageType.GAMESTATUS);
        if (GameState.valueOf(gameState) == null) {
            throw new BattleshipException(ErrorType.INVALID_STATE);
        }
        this.gameState = gameState;
        this.boardSize = boardSize;
        this.board1 = board1;
        this.board2 = board2;
        this.info = info;
    }

    /**
     * Returns the game state.
     *
     * @return the game state as a byte.
     */
    public byte getGameState() {
        return gameState;
    }

    /**
     * Returns the size of the game board.
     *
     * @return the board size.
     */
    public int getBoardSize() {
        return boardSize;
    }

    /**
     * Returns the status of the first board.
     *
     * @return a byte array representing the first board's status.
     */
    public byte[] getBoard1() {
        return board1;
    }

    /**
     * Returns the status of the second board.
     *
     * @return a byte array representing the second board's status.
     */
    public byte[] getBoard2() {
        return board2;
    }

    /**
     * Returns the additional information associated with the game status.
     *
     * @return a byte array containing the additional information.
     */
    public byte[] getInfo() {
        return info;
    }

    /**
     * Checks whether this GameStatusMessage is equal to another object.
     * Two GameStatusMessage instances are considered equal if their message type is
     * {@link MessageType#GAMESTATUS} and all corresponding fields are identical.
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
        if (message.getType() != MessageType.GAMESTATUS) {
            return false;
        }
        GameStatusMessage gameStatusMessage = (GameStatusMessage) message;
        return this.gameState == gameStatusMessage.getGameState() &&
                this.boardSize == gameStatusMessage.getBoardSize() &&
                Arrays.equals(this.board1, gameStatusMessage.getBoard1()) &&
                Arrays.equals(this.board2, gameStatusMessage.getBoard2()) &&
                Arrays.equals(this.info, gameStatusMessage.getInfo());
    }
}