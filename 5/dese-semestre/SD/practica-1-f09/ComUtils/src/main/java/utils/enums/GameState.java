package utils.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * Enum representing the different states of the game.
 * <p>
 * Each state is associated with a unique byte code. The mapping between the code and the state
 * is maintained internally.
 * </p>
 */
public enum GameState {
    /** The game is waiting for players to join. */
    WAITING_PLAYERS((byte) 1),
    /** The game is in the setup phase. */
    SETUP((byte) 2),
    /** The game is currently in progress. */
    PLAYING((byte) 3),
    /** The game has finished. */
    FINISHED((byte) 4);

    private final byte code;
    private static final Map<Byte, GameState> map = new HashMap<>();

    static {
        for (GameState type : GameState.values()) {
            map.put(type.getCode(), type);
        }
    }

    /**
     * Constructor for the GameState enum.
     *
     * @param code The unique byte code for the game state.
     */
    GameState(byte code) {
        this.code = code;
    }

    /**
     * Returns the unique byte code associated with this game state.
     *
     * @return The game state's code.
     */
    public byte getCode() {
        return code;
    }

    /**
     * Returns the {@code GameState} corresponding to the provided byte code.
     *
     * @param code The byte code for which the game state is requested.
     * @return The matching {@code GameState}, or {@code null} if no match is found.
     */
    public static GameState valueOf(byte code) {
        GameState gameState = map.get(code);
        return gameState;
    }
}
