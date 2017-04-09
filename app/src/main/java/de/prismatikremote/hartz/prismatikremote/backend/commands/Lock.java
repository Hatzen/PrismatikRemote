package de.prismatikremote.hartz.prismatikremote.backend.commands;

import de.prismatikremote.hartz.prismatikremote.backend.Communicator;

/**
 * Created by kaiha on 08.04.2017.
 */
public class Lock extends Communication {

    public static final String RESPOND_SUCCESS = "lock:success";

    @Override
    public String getCommand() {
        return "lock";
    }

    @Override
    public boolean onRespond(String response, Communicator.OnCompleteListener listener ) {
        return response.equals(Lock.RESPOND_SUCCESS);
    }
}
