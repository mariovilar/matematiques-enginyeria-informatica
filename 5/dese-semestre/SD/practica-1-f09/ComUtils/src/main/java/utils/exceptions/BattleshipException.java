package utils.exceptions;

import utils.enums.ErrorType;

/**
 * Exception class representing errors specific to the Battleship game.
 * <p>
 * This exception encapsulates an {@link utils.enums.ErrorType} that provides
 * detailed information about the error.
 * </p>
 */
public class BattleshipException extends Exception {

    private final ErrorType errorType;

    /**
     * Constructs a new BattleshipException with the specified error type.
     *
     * @param errorType The {@code ErrorType} associated with this exception.
     */
    public BattleshipException(ErrorType errorType) {
        super(errorType.getDescription());
        this.errorType = errorType;
    }

    /**
     * Retrieves the {@code ErrorType} associated with this exception.
     *
     * @return The error type.
     */
    public ErrorType getErrorType() {
        return errorType;
    }

    /**
     * Returns the description of the error.
     *
     * @return A descriptive message of the error.
     */
    public String getDescription() {
        return errorType.getDescription();
    }
}