package stepanoff.denis.lab5.common.util;

import java.io.Serializable;

/**
 * Encapsulation for all for transition between client and server
 * It saves the entity itself and its type
 */
public class TypedEntity implements Serializable {

    private final Object o;
    private final Class<?> clazz;

    /**
     * Encapsulate it.
     * @param object -- what you want to put inside
     * @param <T> -- type of object
     */
    public <T> TypedEntity(T object) {
        this.o = object;
        this.clazz = object.getClass();
    }

    /**
     * Get value
     * @return the entity stored inside with Object type (you can just cast it)
     */
    public Object get() {
        return this.o;
    }

    /**
     * Get the type of entity inside
     * @return Class of entity
     */
    public Class<?> getType() {
        return this.clazz;
    }
}
