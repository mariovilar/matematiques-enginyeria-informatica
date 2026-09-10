package utils.message;

import utils.enums.MessageType;

/**
 * Abstract class representing a generic message in the communication protocol.
 * <p>
 * Every message in the system is characterized by a specific {@link MessageType}.
 * This class provides a common structure for all messages.
 * </p>
 */
public abstract class Message {
    private final MessageType type;
    
    /**
     * Constructs a Message with the specified message type.
     *
     * @param type the {@link MessageType} of the message.
     */
    public Message(MessageType type) {
        this.type = type;
    }
    
    /**
     * Returns the type of this message.
     *
     * @return the {@link MessageType} of this message.
     */
    public MessageType getType() {
        return this.type;
    }
}