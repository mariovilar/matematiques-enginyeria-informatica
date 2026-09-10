package utils.message;

import utils.enums.MessageType;

/**
 * Represents a hit message in the communication protocol.
 * <p>
 * This message indicates that a shot has hit a target. The {@code sink} field specifies whether
 * the hit resulted in a vessel sinking.
 * </p>
 *
 * @see Message
 */
public class HitMessage extends Message {
    private byte sink;

    /**
     * Constructs a HitMessage with the specified sink status.
     *
     * @param sink a byte representing the sink status (e.g., 1 if the vessel is sunk, 0 otherwise)
     */
    public HitMessage(byte sink) {
        super(MessageType.HIT);
        this.sink = sink;
    }

    /**
     * Returns the sink status associated with this hit.
     *
     * @return the sink status as a byte.
     */
    public byte getSink() {
        return this.sink;
    }

    /**
     * Checks whether this HitMessage is equal to another object.
     * <p>
     * Two HitMessage objects are considered equal if their message type is {@link MessageType#HIT} 
     * and their sink values are identical.
     * </p>
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
         if (message.getType() != MessageType.HIT) {
             return false;
         }
         HitMessage hitMessage = (HitMessage) message;
         return this.sink == hitMessage.getSink();
    }
}
