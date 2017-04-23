package de.prismatikremote.hartz.prismatikremote.backend.commands;

import de.prismatikremote.hartz.prismatikremote.backend.Communicator;
import de.prismatikremote.hartz.prismatikremote.backend.RemoteState;

/**
 * Created by kaiha on 08.04.2017.
 */
public class GetBrightness extends Communication {

    @Override
    public String getCommand() {
        return "getbrightness";
    }

    @Override
    public boolean onRespond(String response, Communicator.OnCompleteListener listener ) {
        String prefix = "brightness:";
        if (response.contains(prefix)) {
            int brightness = Integer.valueOf(response.substring(response.indexOf(prefix)+prefix.length()));
            RemoteState.getInstance().setBrightness(brightness);
            return true;
        }
        return false;
    }
}
