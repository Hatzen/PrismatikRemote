package de.prismatikremote.hartz.prismatikremote.backend.commands;

import de.prismatikremote.hartz.prismatikremote.backend.RemoteState;

/**
 * Created by kaiha on 09.04.2017.
 */

public class ToggleStatus extends Communication {
    private static String TAG = "ToggleStatus";

    @Override
    public String getCommand() {
        String command = "setstatus:on";
        if (RemoteState.getInstance().getStatus() == RemoteState.Status.ON) {
            command = "setstatus:off";
        }
        return command;
    }

}
