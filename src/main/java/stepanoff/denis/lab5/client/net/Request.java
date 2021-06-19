package stepanoff.denis.lab5.client.net;

import stepanoff.denis.lab5.common.cmd.CommandLabel;
import stepanoff.denis.lab5.common.util.Authentication;
import stepanoff.denis.lab5.common.util.TypedEntity;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Representation of structure of Request to server
 */
public class Request {

    private final CommandLabel command;
    private final Authentication authentication;
    private final LinkedList<TypedEntity> toSend = new LinkedList<>();

    /**
     * Initialize Request with Command
     * @param command -- CommandLabel associated with Request
     */
    public Request(Authentication authentication, CommandLabel command) {
        this.authentication = authentication;
        this.command = command;
    }

    /**
     * Add something as argument for request
     * @param entity -- an object encapsulated in Typed Entity
     * @see TypedEntity
     * @return this request
     */
    public Request add(TypedEntity entity) {
        this.toSend.add(entity);
        return this;
    }

    /**
     * Get request as Queue to send.
     * @return Queue with arguments in order they was added starting with Request's Command Label
     */
    Queue<Object> toSendingQueue() {
        this.toSend.addFirst(new TypedEntity(this.command));
        this.toSend.addFirst(new TypedEntity(this.authentication));
        return new LinkedList<>(this.toSend);
    }
}
