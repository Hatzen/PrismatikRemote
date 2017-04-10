package de.prismatikremote.hartz.prismatikremote.backend.commands;

import de.prismatikremote.hartz.prismatikremote.backend.Communicator;
import de.prismatikremote.hartz.prismatikremote.backend.RemoteState;

/**
 * Created by kaiha on 08.04.2017.
 */
public class GetMode extends Communication {

    public static final String RESPOND_AMBILIGHT = "mode:ambilight";
    public static final String RESPOND_MOODLAMP = "mode:moodlamp";

    @Override
    public String getCommand() {
        return "getmode";
    }

    @Override
    public boolean onRespond(String response, Communicator.OnCompleteListener listener ) {
        RemoteState.Mode currentMode;
        switch(response) {
            case RESPOND_AMBILIGHT:
                currentMode = RemoteState.Mode.AMBILIGHT;
                break;
            case RESPOND_MOODLAMP:
                currentMode = RemoteState.Mode.MOODLAMP;
                break;
            default:
                return false;
        }
        RemoteState.getInstance().setMode(currentMode);
        return true;
    }
}
