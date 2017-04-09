package de.prismatikremote.hartz.prismatikremote.backend;

import android.util.Log;

import com.jraska.console.Console;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import de.prismatikremote.hartz.prismatikremote.backend.commands.ApiKey;
import de.prismatikremote.hartz.prismatikremote.backend.commands.Communication;
import de.prismatikremote.hartz.prismatikremote.backend.commands.Exit;
import de.prismatikremote.hartz.prismatikremote.backend.commands.GetCountLeds;
import de.prismatikremote.hartz.prismatikremote.backend.commands.GetProfile;
import de.prismatikremote.hartz.prismatikremote.backend.commands.GetProfiles;
import de.prismatikremote.hartz.prismatikremote.backend.commands.GetStatus;
import de.prismatikremote.hartz.prismatikremote.backend.commands.Lock;
import de.prismatikremote.hartz.prismatikremote.backend.commands.SetProfile;
import de.prismatikremote.hartz.prismatikremote.backend.commands.ToggleStatus;
import de.prismatikremote.hartz.prismatikremote.backend.commands.Unlock;

/**
 * Created by kaiha on 08.04.2017.
 */

public class Communicator {
    private static String TAG = "Communicator";

    /**
     * Interface to inform caller that all commands have finished.
     */
    public interface OnCompleteListener {
        public void onError(String result);
        public void onStepCompletet(Communication communication);
        public void onSuccess();
    }

    private String serverKey;
    private String serverIp;
    private int serverPort;

    // Singleton.
    private static final Communicator instance = new Communicator();

    public static Communicator getInstance() {
        return instance;
    }

    /**
     * Setup the Connection information.
     * @param serverIp
     * @param serverPort
     */
    public void setConnection(String serverIp, int serverPort, String serverKey, OnCompleteListener listener) {
        this.serverIp = serverIp;
        this.serverPort = serverPort;
        if(serverKey.equals(""))
            this.serverKey = null;
        else
            this.serverKey = serverKey;
        refreshState(listener);
    }

    public void refreshState(OnCompleteListener listener) {
        ArrayList<Communication> commands = new ArrayList<>();
        commands.add(new GetStatus());
        commands.add(new GetProfiles());
        commands.add(new GetProfile());
        commands.add(new GetCountLeds());
        //TODO: Add all get commands

        startThread(commands, listener);
    }

    public void togglePower(OnCompleteListener listener) {
        ArrayList<Communication> commands = new ArrayList<>();
        commands.add(new GetStatus());
        // TODO: Maybe check if the status changed and behave like it should!?
        commands.add(new ToggleStatus());

        startThread(commands, listener);
    }

    public void setProfile(String profile, OnCompleteListener listener) {
        ArrayList<Communication> commands = new ArrayList<>();
        commands.add(new SetProfile(profile));
        commands.add(new GetProfile());

        startThread(commands, listener);
    }

    private void startThread(ArrayList<Communication> commands, OnCompleteListener listener) {
        surroundLock(commands);
        surroundStartAndEnd(commands);
        new Thread(new Executor(commands, listener)).start();
    }

    private void surroundLock(ArrayList<Communication> commands) {
        commands.add(0, new Lock());
        commands.add(new Unlock());
    }

    private void surroundStartAndEnd(ArrayList<Communication> commands) {
        if (serverKey != null)
            commands.add(0, new ApiKey(serverKey));
        commands.add(new Exit());
    }

    /**
     * Executes a list of commands.
     */
    private class Executor implements Runnable {
        private ArrayList<Communication> commands;
        private OnCompleteListener listener;

        Executor(ArrayList<Communication> commands, OnCompleteListener listener ) {
            this.commands = commands;
            this.listener = listener;
        }

        @Override
        public void run() {
            Socket pingSocket;
            PrintWriter out;
            BufferedReader in;
            try {
                pingSocket = new Socket(serverIp, serverPort);
                out = new PrintWriter(pingSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(pingSocket.getInputStream()));

                // Skip first Status Line. TODO:Move elsewhere (dont execute for every commandset)
                in.readLine();

                for (Communication com : commands) {
                    String input = com.getCommand();
                    Log.e(TAG,"Input: >" + input );
                    Console.writeLine(">" + input);
                    out.println(input);

                    String output = in.readLine();
                    Log.d(TAG,"Output: <" + output );
                    boolean error = !com.onRespond(output, listener);
                    if(error) {
                        Console.writeLine("!!!Error: " + output);
                    } else {
                        Console.writeLine("<" + output);
                    }
                    if(listener != null)
                        listener.onStepCompletet(com);
                }

                out.close();
                in.close();
                pingSocket.close();
            } catch (IOException e) {
                System.err.println("Error: " + e.getMessage());
                Console.writeLine("Error: " + e.getMessage());
                if(listener != null)
                    listener.onError( e.getMessage() );
                return;
            }
            if(listener != null)
                listener.onSuccess();
            Console.writeLine("---");
            Log.e(TAG,"------------------------------------------------");
        }
    }

}