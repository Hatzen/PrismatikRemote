package de.prismatikremote.hartz.prismatikremote;

import android.app.Application;

import de.prismatikremote.hartz.prismatikremote.backend.Communicator;

/**
 * Created by kaiha on 23.04.2017.
 */

public class GlobalVars extends Application {

    private Communicator communicator = Communicator.getInstance();
    // Maybe add RemoteState too..

    public Communicator getCommunicator(){
        return communicator;
    }
}