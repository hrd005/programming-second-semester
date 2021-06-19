package stepanoff.denis.lab5.server.dataio;

public class DataSourceFactory {

    private static DataSource existing = null;

    public static DataSource getDefault() {
        if (existing == null) {
            existing = new DataBase();
        }
        return existing;
    }
}
