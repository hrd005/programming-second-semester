package stepanoff.denis.lab5.data;

/**
 * Data class from task
 */
public class Coordinates {

    private Coordinates(double x, double y) {
        this.x =  x;
        this.y = y;
    }

    /**
     * Must be greater than -48. Not null
     */
    private final Double x; //Значение поля должно быть больше -48, Поле не может быть null
    public double getX() {
        return this.x;
    }

    private final double y;
    public double getY() {
        return this.y;
    }

    /**
     * Fabric method for Coordinates
     * @param x x value
     * @param y y value
     * @return new Coordinates
     */
    public static Coordinates create(double x, double y) {
        if (x <= -48) throw new IllegalArgumentException("X must be greater than -48.");

        return new Coordinates(x, y);
    }

    /**
     * Builder for Coordinates
     */
    public static class Builder {
        private double x;
        private double y;

        /**
         * Set x value
         * @param x x value
         * @return Builder
         * @throws IllegalArgumentException if X is incorrect
         */
        public Builder x(String x) {
            this.x = Double.parseDouble(x);
            if (this.x <= -48) {
                throw new IllegalArgumentException("X must be greater than -48.");
            }

            return this;
        }

        /**
         * Set y value
         * @param y y value
         * @return Builder
         */
        public Builder y(String y) {
            this.y = Double.parseDouble(y);
            return this;
        }

        /**
         * @return Coordinates with specified values
         */
        public Coordinates build() {
            return Coordinates.create(x, y);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Coordinates)) return false;

        Coordinates coordinates = (Coordinates) o;
        return coordinates.x.equals(this.x) && coordinates.y == this.y;
    }

    @Override
    public int hashCode() {
        return (int)(this.x + this.y);
    }

    @Override
    public String toString() {
        return String.format("(%.3f, %.3f)", this.x, this.y);
    }

    /**
     * Make a copy
     * @return a same Coordinates
     */
    public Coordinates copy() {
        return create(this.getX(), this.getY());
    }
}
