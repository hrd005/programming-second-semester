package stepanoff.denis.lab5.server.dataio;

public interface SecurityManager {

    int authenticate(String login, String pass);

    int register(String login, String pass);
}
