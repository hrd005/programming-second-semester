package stepanoff.denis.lab5.common.data;

import stepanoff.denis.lab5.server.Collection;
import stepanoff.denis.lab5.server.IdManager;

import java.io.Serializable;

import static stepanoff.denis.lab5.common.util.ConsoleWriter.*;

/**
 * Data structure from task
 */
public class Venue implements Serializable {

    private Venue() {}

    /**
     * Greater zero, unique, auto
     */
    private long id; //Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически

    /**
     * @return id
     */
    public long getId() {
        return this.id;
    }

    /**
     * not null
     */
    private String name; //Поле не может быть null, Строка не может быть пустой

    /**
     * @return name of Venue
     */
    public String getName() {
        return this.name;
    }

    /**
     * not null, greater zero
     */
    private Integer capacity; //Поле может быть null, Значение поля должно быть больше 0

    /**
     * @return capacity of Venue
     */
    public int getCapacity() {
        return this.capacity;
    }

    /**
     * not null
     */
    private VenueType type; //Поле может быть null

    /**
     * @return VenueType
     */
    public VenueType getType() {
        return this.type;
    }

    /**
     * Generate Venue with new id
     * @return Builder for Venue with illegal id, which will be fixed by server side
     */
    public static Builder newVenue() {
        return new Builder().id(-1);
    }

    /**
     * Generate Venue with old id
     * @param id id of Venue
     * @return Builder for Venue with specified id
     */
    public static Builder existing(long id) {
        return new Builder().id(id);
    }

    /**
     * Mutable version of Venue
     * @return Builder of Venue initialized with its values
     */
    public Builder builder() {
        return new Builder(this.copy());
    }

    /**
     * Builder for Venue
     */
    public static class Builder {
        private final Venue venue;

        private Builder() {
            this.venue = new Venue();
        }

        private Builder(Venue venue) {
            this.venue = venue;
        }

        private Builder id(long id) {

            this.venue.id = id;
            return this;
        }

        /**
         * Set name
         * @param name name of Venue
         * @return Builder
         */
        public Builder name(String name) {
            if (name == null || name.isEmpty())
                throw new IllegalArgumentException("Name can't be null or empty");

            this.venue.name = name;
            return this;
        }

        /**
         * set capacity
         * @param capacity capacity for Venue
         * @return Builder
         */
        public Builder capacity(int capacity) {
            if (capacity <= 0)
                throw new IllegalArgumentException("Capacity must be greater zero");

            this.venue.capacity = capacity;
            return this;
        }

        /**
         * Set capacity
         * @param capacity capacity as String for Venue
         * @return Builder
         */
        public Builder capacity(String capacity) {
            return this.capacity(Integer.parseInt(capacity));
        }

        /**
         * Set type
         * @param venueType type for Venue
         * @return Builder
         */
        public Builder venueType(VenueType venueType) {
            if (venueType == null)
                throw new IllegalArgumentException("Venue Type can't be null.");

            this.venue.type = venueType;
            return this;
        }

        /**
         * Set type
         * @param venueType type as String for Venue
         * @return Builder
         */
        public Builder venueType(String venueType) {
            return this.venueType(VenueType.valueOf(venueType.toUpperCase()));
        }

        /**
         * Get Venue with specified Values
         * @return Venue
         */
        public Venue build() {

            if (this.venue.name == null || this.venue.capacity == null || this.venue.type == null)
                throw new NotInitializedEntityException("Venue is not initialized bt built.");

            return this.venue;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Venue)) return false;

        Venue venue = (Venue) o;
        return venue.id == this.id &&
                venue.capacity.equals(this.capacity) &&
                venue.name.equals(this.name) &&
                venue.type.equals(this.type);
    }

    @Override
    public int hashCode() {
        return (int)id + capacity + name.hashCode() + type.ordinal();
    }

    @Override
    public String toString() {
        return getColored("Venue", Color.PURPLE) + "(id = " + this.id + ") {" +
                getColored("\n\t\tName = ", Color.GREEN) + this.name +
                getColored("\n\t\tCapacity = ", Color.GREEN) + this.capacity +
                getColored("\n\t\tType = ", Color.GREEN) + this.type.name().toLowerCase() + "\n\t}";
    }

    /**
     * Make a copy
     * @return a similar Venue
     */
    public Venue copy() {
        return new Builder().id(this.id)
                .name(this.name)
                .capacity(this.capacity)
                .venueType(this.type)
                .build();
    }
}
