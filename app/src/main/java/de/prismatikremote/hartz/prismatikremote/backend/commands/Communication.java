package de.prismatikremote.hartz.prismatikremote.backend.commands;

import de.prismatikremote.hartz.prismatikremote.backend.Communicator;

/**
 * Created by kaiha on 08.04.2017.
 * Communication API based on:
 * https://github.com/Atarity/Lightpack-docs/blob/master/EN/Prismatik_API.md
 *
 * Steps for Communication:
 * 1. prepareDataForCommand
 * 2. getCommand
 * 3. onRespond
 */
public abstract class Communication {
    private static String TAG = "Communication";

    public static final String RESPOND_ERROR = "error";
    public static final String RESPOND_OK = "ok";
    public static final String RESPOND_BUSY = "busy ";
    public static final String RESPOND_NOT_LOCKED = "not locked";

    protected String command;

    public String getCommand() {
        return command;
    }

    public boolean onRespond(String response, Communicator.OnCompleteListener listener ) {
        return response.equals(Communication.RESPOND_OK);
    }

    public boolean isDelayNeeded() {
        return false;
    }
}
