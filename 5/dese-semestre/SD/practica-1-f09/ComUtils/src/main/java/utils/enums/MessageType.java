package utils.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * Enum representing the different types of messages that can be exchanged between the client and the server.
 * <p>
 * Each message type is associated with a unique byte code. The mapping between the code and its corresponding
 * enum constant is maintained internally for quick lookup.
 * </p>
 */
public enum MessageType {
    /** Indicates that the message represents an error. */
    ERROR((byte) 0),
    /** Indicates that the message is an acknowledgment (OK). */
    OK((byte) 1),
    /** Indicates a request to create a new game session. */
    CREATE((byte) 2),
    /** Indicates a request to join an existing game session. */
    JOIN((byte) 3),
    /** Indicates a request to rejoin a game session. */
    REJOIN((byte) 4),
    /** Indicates a request to retrieve the game configuration. */
    GETCONFIG((byte) 5),
    /** Indicates a message containing game configuration details. */
    GAMECONFIG((byte) 6),
    /** Indicates a request to add a vessel to the game. */
    ADDVESSEL((byte) 7),
    /** Indicates a request to retrieve the current game status. */
    GETSTATUS((byte) 8),
    /** Indicates a message containing game status details. */
    GAMESTATUS((byte) 9),
    /** Indicates a request to perform a shot action in the game. */
    SHOT((byte) 10),
    /** Indicates a message confirming that a shot action resulted in a hit. */
    HIT((byte) 11),
    /** Indicates that the received message represents a failure. */
    FAIL((byte) 12),
    /** Indicates a request to leave the current game session. */
    LEAVE((byte) 13);

    private final byte code;
    private static final Map<Byte, MessageType> map = new HashMap<>();

    static {
        for (MessageType type : MessageType.values()) {
            map.put(type.getCode(), type);
        }
    }

    /**
     * Constructor for the MessageType enum.
     *
     * @param code The unique byte code for the message type.
     */
    MessageType(byte code) {
        this.code = code;
    }

    /**
     * Returns the unique byte code associated with this message type.
     *
     * @return The byte code representing the message type.
     */
    public byte getCode() {
        return code;
    }

    /**
     * Returns the {@code MessageType} corresponding to the provided byte code.
     *
     * @param code The byte code for which the message type is requested.
     * @return The matching {@code MessageType}, or {@code null} if no match is found.
     */
    public static MessageType valueOf(byte code) {
        MessageType messageType = map.get(code);
        return messageType;
    }
}