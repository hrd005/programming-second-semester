package stepanoff.denis.lab5.common.util;

import java.io.Serializable;

public class Authentication implements Serializable {

    private final String login;
    private final String pass;

    public Authentication(String login, String pass) {
        this.login = login;
        this.pass = pass;
    }

    public String getLogin() {
        return this.login;
    }

    public String getPass() {
        return this.pass;
    }
}
