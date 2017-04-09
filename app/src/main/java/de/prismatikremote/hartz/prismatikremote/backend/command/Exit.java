package de.prismatikremote.hartz.prismatikremote.backend.command;

import de.prismatikremote.hartz.prismatikremote.backend.Communicator;

/**
 * Created by kaiha on 08.04.2017.
 */
public class Exit extends Communication {

    public static final String RESPOND_SUCCESS = "Goodbye!";

    @Override
    public String getCommand() {
        return "exit";
    }

    @Override
    public boolean onRespond(String response, Communicator.OnCompleteListener listener ) {
        return response.equals(Exit.RESPOND_SUCCESS);
    }
}
