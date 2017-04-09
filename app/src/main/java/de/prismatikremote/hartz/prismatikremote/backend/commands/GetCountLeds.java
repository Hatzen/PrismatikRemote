package de.prismatikremote.hartz.prismatikremote.backend.commands;

import de.prismatikremote.hartz.prismatikremote.backend.Communicator;
import de.prismatikremote.hartz.prismatikremote.backend.RemoteState;

/**
 * Created by kaiha on 08.04.2017.
 */
public class GetCountLeds extends Communication {
    @Override
    public String getCommand() {
        return "getcountleds";
    }

    @Override
    public boolean onRespond(String response, Communicator.OnCompleteListener listener ) {
        String prefix = "countleds:";
        if (response.contains(prefix)) {
            int ledCount = Integer.valueOf(response.substring(response.indexOf(prefix)+1));
            RemoteState.getInstance().setCountLeds(ledCount);
            return true;
        }
        return false;
    }
}
