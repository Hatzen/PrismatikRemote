package de.prismatikremote.hartz.prismatikremote.backend.commands;

import de.prismatikremote.hartz.prismatikremote.backend.Communicator;
import de.prismatikremote.hartz.prismatikremote.backend.RemoteState;

/**
 * Created by kaiha on 08.04.2017.
 */
public class GetSmoothness extends Communication {

    @Override
    public String getCommand() {
        return "getsmooth";
    }

    @Override
    public boolean onRespond(String response, Communicator.OnCompleteListener listener ) {
        String prefix = "smooth:";
        if (response.contains(prefix)) {
            int smooth = Integer.valueOf(response.substring(response.indexOf(prefix)+prefix.length()));
            RemoteState.getInstance().setSmoothness(smooth);
            return true;
        }
        return false;
    }
}
