package de.prismatikremote.hartz.prismatikremote.backend.commands;

import de.prismatikremote.hartz.prismatikremote.backend.Communicator;

/**
 * Created by kaiha on 08.04.2017.
 */
public class ApiKey extends Communication {

    public static final String RESPOND_SUCCESS = "ok";

    private String apikey = "";

    @Override
    public String getCommand() {
        return "apikey:{" + apikey + "}";
    }

    @Override
    public boolean onRespond(String response, Communicator.OnCompleteListener listener ) {
        return response.equals(ApiKey.RESPOND_SUCCESS);
    }
}
