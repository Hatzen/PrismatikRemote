package de.prismatikremote.hartz.prismatikremote.backend.commands;

import de.prismatikremote.hartz.prismatikremote.backend.Communicator;
import de.prismatikremote.hartz.prismatikremote.backend.RemoteState;

/**
 * Created by kaiha on 08.04.2017.
 */
public class GetGamma extends Communication {

    @Override
    public String getCommand() {
        return "getgamma";
    }

    @Override
    public boolean onRespond(String response, Communicator.OnCompleteListener listener ) {
        String prefix = "gamma:";
        if (response.contains(prefix)) {
            double gamma = Double.valueOf(response.substring(response.indexOf(prefix)+prefix.length()));
            RemoteState.getInstance().setGamma(gamma);
            return true;
        }
        return false;
    }
}
