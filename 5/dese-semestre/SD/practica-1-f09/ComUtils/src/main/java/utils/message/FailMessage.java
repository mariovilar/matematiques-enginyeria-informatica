package utils.message;

import utils.enums.MessageType;

/**
 * Represents a failure message in the communication protocol.
 * <p>
 * This message is used to indicate that an operation has failed.
 * </p>
 *
 * @see Message
 */
public class FailMessage extends Message {

    /**
     * Constructs a FailMessage.
     */
    public FailMessage() {
        super(MessageType.FAIL);
    }

    /**
     * Checks whether this FailMessage is equal to another object.
     * Two FailMessage objects are considered equal if their message types are {@link MessageType#FAIL}.
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
        return message.getType() == MessageType.FAIL;
    }
}
