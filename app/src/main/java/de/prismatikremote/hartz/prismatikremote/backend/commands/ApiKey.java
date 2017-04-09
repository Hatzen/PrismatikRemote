package de.prismatikremote.hartz.prismatikremote.backend.commands;

/**
 * Created by kaiha on 08.04.2017.
 */
public class ApiKey extends Communication {
    private String apikey = "";

    public ApiKey(String apikey) {
        this.apikey = apikey;
    }

    @Override
    public String getCommand() {
        return "apikey:{" + apikey + "}";
    }
}
