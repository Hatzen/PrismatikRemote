package de.prismatikremote.hartz.prismatikremote.backend.commands;

import java.util.ArrayList;
import java.util.Arrays;

import de.prismatikremote.hartz.prismatikremote.backend.Communicator;
import de.prismatikremote.hartz.prismatikremote.backend.RemoteState;

/**
 * Created by kaiha on 08.04.2017.
 */
public class GetProfiles extends Communication {
    @Override
    public String getCommand() {
        return "getprofiles";
    }

    @Override
    public boolean onRespond(String response, Communicator.OnCompleteListener listener ) {
        String prefix = "profiles:";
        if (response.contains(prefix)) {
            response = response.substring(response.indexOf(prefix)+prefix.length()+1);
            ArrayList<String> list = new ArrayList(Arrays.asList(response.split(";")));
            RemoteState.getInstance().setProfiles(list);
            return true;
        }
        return false;
    }
}
