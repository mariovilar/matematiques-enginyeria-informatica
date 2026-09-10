package utils.message;

import utils.enums.ErrorType;
import utils.enums.MessageType;

/**
 * Represents an error message in the communication protocol.
 * <p>
 * The error message contains an {@link utils.enums.ErrorType} code and a descriptive message.
 * The message is automatically set using the error code's description unless an explicit message is provided.
 * </p>
 *
 * @see Message
 */
public class ErrorMessage extends Message {
    private ErrorType code;
    private String message;

    /**
     * Constructs an ErrorMessage with the specified error code.
     * <p>
     * The descriptive message is obtained from the error code.
     * </p>
     *
     * @param code the error code.
     */
    public ErrorMessage(ErrorType code) {
        super(MessageType.ERROR);
        this.code = code;
        this.message = code.getDescription();
    }

    /**
     * Constructs an ErrorMessage with the specified error code and message.
     *
     * @param code    the error code.
     * @param message a custom descriptive message.
     */
    public ErrorMessage(ErrorType code, String message) {
        super(MessageType.ERROR);
        this.code = code;
        this.message = message;
    }

    /**
     * Returns the error code associated with this message.
     *
     * @return the error code.
     */
    public ErrorType getCode() {
        return this.code;
    }

    /**
     * Returns the byte code corresponding to the error code.
     *
     * @return the error code as a byte.
     */
    public byte getByteCode() {
        return this.code.getCode();
    }

    /**
     * Returns the descriptive message associated with this error.
     *
     * @return the error message.
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * Checks whether this ErrorMessage is equal to another object.
     * Two ErrorMessage objects are considered equal if their message types are {@link MessageType#ERROR} 
     * and their error codes are identical.
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
        if (message.getType() != MessageType.ERROR) {
            return false;
        }
        ErrorMessage errorMessage = (ErrorMessage) message;
        return this.code == errorMessage.getCode();
    }
}
