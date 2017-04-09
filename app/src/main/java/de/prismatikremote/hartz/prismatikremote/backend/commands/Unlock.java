package de.prismatikremote.hartz.prismatikremote.backend.commands;

import de.prismatikremote.hartz.prismatikremote.backend.Communicator;

/**
 * Created by kaiha on 08.04.2017.
 */
public class Unlock extends Communication {

    public static final String RESPOND_SUCCESS = "unlock:success";

    @Override
    public String getCommand() {
        return "unlock";
    }

    @Override
    public boolean onRespond(String response, Communicator.OnCompleteListener listener ) {
        return response.equals(Unlock.RESPOND_SUCCESS);
    }
}
