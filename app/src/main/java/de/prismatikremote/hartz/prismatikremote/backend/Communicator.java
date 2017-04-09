package de.prismatikremote.hartz.prismatikremote.backend;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import de.prismatikremote.hartz.prismatikremote.MainActivity;
import de.prismatikremote.hartz.prismatikremote.backend.command.ApiKey;
import de.prismatikremote.hartz.prismatikremote.backend.command.Communication;
import de.prismatikremote.hartz.prismatikremote.backend.command.Exit;
import de.prismatikremote.hartz.prismatikremote.backend.command.GetStatus;
import de.prismatikremote.hartz.prismatikremote.backend.command.Lock;
import de.prismatikremote.hartz.prismatikremote.backend.command.ToggleStatus;
import de.prismatikremote.hartz.prismatikremote.backend.command.TurnOff;
import de.prismatikremote.hartz.prismatikremote.backend.command.Unlock;

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
    public void setConnection(String serverIp, int serverPort, OnCompleteListener listener) {
        this.serverIp = serverIp;
        this.serverPort = serverPort;
        refreshState(listener);
    }

    public void refreshState(OnCompleteListener listener) {
        ArrayList<Communication> commands = new ArrayList<>();
        commands.add(new GetStatus());
        //TODO: Add all get commands

        startThread(commands, listener);
    }

    public void togglePower(OnCompleteListener listener) {
        ArrayList<Communication> commands = new ArrayList<>();
        commands.add(new GetStatus());
        commands.add(new ToggleStatus());

        startThread(commands, listener);
    }

    private void startThread(ArrayList<Communication> commands, OnCompleteListener listener) {
        surroundLock(commands);
        sourroundStartAndEnd(commands);
        new Thread(new Executer(commands, listener)).start();
    }

    private void surroundLock(ArrayList<Communication> commands) {
        commands.add(0, new Lock());
        commands.add(new Unlock());
    }

    private void sourroundStartAndEnd(ArrayList<Communication> commands) {
        //TODO: create commands
        if (serverKey != null)
            commands.add(0, new ApiKey());
        commands.add(new Exit());
    }

    //TODO: remove
    /* example code
    public static void main(String[] args) throws IOException {

        Socket pingSocket = null;
        PrintWriter out = null;
        BufferedReader in = null;

        try {
            pingSocket = new Socket("servername", 23);
            out = new PrintWriter(pingSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(pingSocket.getInputStream()));
        } catch (IOException e) {
            return;
        }

        out.println("ping");
        System.out.println(in.readLine());
        out.close();
        in.close();
        pingSocket.close();
    }*/


    /**
     * Executes a list of commands.
     */
    private class Executer implements Runnable {
        private ArrayList<Communication> commands;
        private OnCompleteListener listener;

        public Executer(ArrayList<Communication> commands, OnCompleteListener listener ) {
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

                // Skip first Status Line. TODO:Move elsewhere (dont execute every commandset)
                in.readLine();

                for (Communication com : commands) {
                    String input = com.getCommand();
                    Log.e(TAG,"Input: >" + input );
                    out.println(input);

                    String output = in.readLine();
                    Log.d(TAG,"Output: <" + output );
                    com.onRespond(output, listener);
                    if(listener != null)
                        listener.onStepCompletet(com);
                }

                out.close();
                in.close();
                pingSocket.close();
            } catch (IOException e) {
                System.err.println("Error: " + e.getMessage());
                if(listener != null)
                    listener.onError( e.getMessage() );
                return;
            }
            if(listener != null)
                listener.onSuccess();
            Log.e(TAG,"------------------------------------------------");
        }
    }

}
