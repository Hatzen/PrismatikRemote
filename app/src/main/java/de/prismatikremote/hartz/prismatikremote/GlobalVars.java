package de.prismatikremote.hartz.prismatikremote;

import android.app.Application;

import de.prismatikremote.hartz.prismatikremote.backend.Communicator;

/**
 * Created by kaiha on 23.04.2017.
 */

public class GlobalVars extends Application {


    // TODO XY: Maybe move this in the Service and let it the only reference.
    private Communicator communicator = Communicator.getInstance();

    // Maybe add RemoteState too..

    public Communicator getCommunicator(){

        // TODO XY: Maybe move this in the Service and let it the only reference.
        // and call startService()onStartCommand


        // TODO: Maybe move this in the Service and let it the only reference.
        return communicator;
    }
}