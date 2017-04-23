package de.prismatikremote.hartz.prismatikremote.helper;

import android.content.Context;

import de.prismatikremote.hartz.prismatikremote.GlobalVars;
import de.prismatikremote.hartz.prismatikremote.backend.Communicator;

/**
 * Created by kaiha on 23.04.2017.
 */

public class Helper {
    public static Communicator getCommunicator(Context context) {
        return ((GlobalVars) context.getApplicationContext()).getCommunicator();
    }
}
