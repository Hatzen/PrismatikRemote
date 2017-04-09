package de.prismatikremote.hartz.prismatikremote.backend.commands;

import de.prismatikremote.hartz.prismatikremote.backend.Communicator;
import de.prismatikremote.hartz.prismatikremote.backend.RemoteState;

/**
 * Created by kaiha on 08.04.2017.
 */
public class GetProfile extends Communication {
    private static String TAG = "GetStatus";
    public static final String RESPOND_ON = "status:on";

    @Override
    public String getCommand() {
        return "getprofile";
    }

    @Override
    public boolean onRespond(String response, Communicator.OnCompleteListener listener ) {
        String prefix = "profile:";
        if (response.contains(prefix)) {
            String profile = response.substring(response.indexOf(prefix)+1);
            RemoteState.getInstance().setProfile(profile);
            return true;
        }
        return false;
    }
}
