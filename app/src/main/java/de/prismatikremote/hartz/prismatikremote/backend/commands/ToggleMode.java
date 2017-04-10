package de.prismatikremote.hartz.prismatikremote.backend.commands;

import de.prismatikremote.hartz.prismatikremote.backend.RemoteState;

/**
 * Created by kaiha on 09.04.2017.
 */

public class ToggleMode extends Communication {
    public static final String AMBILIGHT = "ambilight";
    public static final String MOODLAMP = "moodlamp";

    @Override
    public String getCommand() {
        String command = "setmode:";
        if (RemoteState.getInstance().getMode() == RemoteState.Mode.AMBILIGHT) {
            command = command + MOODLAMP;
        } else {
            command = command + AMBILIGHT;
        }
        return command;
    }

}
