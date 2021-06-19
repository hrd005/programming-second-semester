package stepanoff.denis.lab5.server.dataio;

public class SecurityManagerFactory {

    private static SecurityManager existing = null;

    public static SecurityManager getDefault() {
        if (existing == null) existing = (SecurityManager) DataSourceFactory.getDefault();
        return existing;
    }
}
