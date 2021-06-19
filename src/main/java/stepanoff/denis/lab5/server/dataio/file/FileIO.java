package stepanoff.denis.lab5.server.dataio.file;

/**
 * IO Utilities
 */
public class FileIO extends stepanoff.denis.lab5.common.dataio.FileIO {

//    /**
//     * Get Reader for collection data
//     * @return FileWriter for current data format
//     */
//    public static FileReader getReader() {
//        return new CsvFileReader();
//    }

    /**
     * Get Writer for collection data
     * @return FileReader for current data format
     */
    public static FileWriter getWriter() {
        return new CsvFileWriter();
    }

}
