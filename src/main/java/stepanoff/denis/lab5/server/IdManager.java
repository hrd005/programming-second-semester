package stepanoff.denis.lab5.server;

import java.util.stream.LongStream;

/**
 * Utility class for Id's management
 */
public class IdManager {

    private final LongStream currentIds;

    /**
     * @param currentIds Ids some entities already have
     */
    protected IdManager(LongStream currentIds) {
        this.currentIds = currentIds;
    }

    /**
     * Check if id can be used
     * @param id id that should be tested
     * @return boolean
     */
    public boolean isIdAppropriate(long id) {
        if (id <= 0)
            return false;
        else return this.currentIds.noneMatch((long t) -> t == id);
    }

//    /**
//     * Generate new id
//     * @param integerId true -- ID should be not greater than integer
//     * @return id
//     */
//    public long getNewId(boolean integerId) {
//
//        long maxPossible = integerId ? Integer.MAX_VALUE : Long.MAX_VALUE;
//
//        long maxCurrent = this.currentIds.max().orElse(0);
//
//        if (maxCurrent < maxPossible) {
//            return maxCurrent + 1;
//        } else {
//            long id = 1;
//            while (!this.isIdAppropriate(id) && id <= maxPossible) {
//                id += 1;
//            }
//
//            if (id <= 0 || id > maxPossible) {
//                Main.provideLogger().error("Looks like all possible ID's are run out.");
//                throw new IndexOutOfBoundsException("No ID could be created.");
//            }
//
//            return id;
//        }
//    }
}
