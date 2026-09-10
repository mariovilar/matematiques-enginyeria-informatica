package utils.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * Enum representing the different types of errors that can occur.
 * <p>
 * Each error type is associated with a unique byte code and a descriptive message.
 * </p>
 */
public enum ErrorType {
    /** Indicates that an unknown error has occurred. */
    UNKNOWN_ERROR((byte)0, "Error desconegut"),
    /** Indicates that the player's name already exists. */
    PLAYER_NAME_EXISTS((byte)1, "El nom de jugador ja existeix"),
    /** Indicates that the player's name is invalid. */
    INVALID_PLAYER_NAME((byte)2, "El nom de jugador és invàlid"),
    /** Indicates that the game identifier is invalid. */
    INVALID_GAME_ID((byte)3, "L'identificador de partida és invàlid"),
    /** Indicates that the player identifier is invalid. */
    INVALID_PLAYER_ID((byte)4, "L'identificador de jugador és invàlid"),
    /** Indicates that the game is not available. */
    GAME_NOT_AVAILABLE((byte)5, "Partida no disponible"),
    /** Indicates that the game parameters are invalid. */
    INVALID_GAME_PARAMETERS((byte)6, "Paràmetres de partida incorrectes"),
    /** Indicates that the provided length is incorrect. */
    INVALID_LENGTH((byte)7, "Longitud incorrecta"),
    /** Indicates that the requested type is not available. */
    TYPE_NOT_AVAILABLE((byte)8, "Tipus no disponible"),
    /** Indicates that the provided coordinate is incorrect. */
    INVALID_COORDINATE((byte)9, "Coordenada incorrecta"),
    /** Indicates that the game state is incorrect. */
    INVALID_STATE((byte)10, "Estat incorrecte");

    private final byte id;
    private final String description;
    private static final Map<Byte, ErrorType> map = new HashMap<>();

    static {
        for (ErrorType type : ErrorType.values()) {
            map.put(type.getCode(), type);
        }
    }

    /**
     * Constructor for the ErrorType enum.
     *
     * @param id          The unique byte code for the error type.
     * @param description A description of the error type.
     */
    ErrorType(byte id, String description) {
        this.id = id;
        this.description = description;
    }

    /**
     * Returns the unique byte code associated with this error type.
     *
     * @return The error code.
     */
    public byte getCode() {
        return this.id;
    }

    /**
     * Returns the description of this error type.
     *
     * @return A descriptive message for the error type.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the {@code ErrorType} corresponding to the provided byte code.
     *
     * @param code The byte code for which the error type is requested.
     * @return The matching {@code ErrorType}, or {@code null} if no match is found.
     */
    public static ErrorType valueOf(byte code) {
        ErrorType errorType = map.get(code);
        return errorType;
    }
}