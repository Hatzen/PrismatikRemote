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
import de.prismatikremote.hartz.prismatikremote.backend.commands.GetMode;
import de.prismatikremote.hartz.prismatikremote.backend.commands.GetProfile;
import de.prismatikremote.hartz.prismatikremote.backend.commands.GetProfiles;
import de.prismatikremote.hartz.prismatikremote.backend.commands.GetStatus;
import de.prismatikremote.hartz.prismatikremote.backend.commands.Lock;
import de.prismatikremote.hartz.prismatikremote.backend.commands.SetColor;
import de.prismatikremote.hartz.prismatikremote.backend.commands.SetProfile;
import de.prismatikremote.hartz.prismatikremote.backend.commands.ToggleMode;
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
        void onError(String result);
        void onStepCompleted(Communication communication);
        void onSuccess();
    }

    public static Communicator getInstance() {
        return instance;
    }

    private String serverKey;
    private String serverIp;
    private int serverPort;

    // Singleton.
    private static final Communicator instance = new Communicator();

    // Keeps lock state (so lights keep color).
    private Executor blocker;

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

    public boolean hasBlocker() {
        return blocker != null;
    }

    public void refreshState(OnCompleteListener listener) {
        ArrayList<Communication> commands = new ArrayList<>();
        commands.add(new GetStatus());
        commands.add(new GetProfiles());
        commands.add(new GetProfile());
        commands.add(new GetCountLeds());
        commands.add(new GetMode());
        //TODO: Add all get commands

        startThread(commands, listener, false);
    }

    public void togglePower(OnCompleteListener listener) {
        ArrayList<Communication> commands = new ArrayList<>();
        commands.add(new GetStatus());
        commands.add(new ToggleStatus());
        commands.add(new GetStatus());

        startThread(commands, listener, false);
    }

    public void toggleMode(OnCompleteListener listener) {
        ArrayList<Communication> commands = new ArrayList<>();
        commands.add(new GetMode());
        commands.add(new ToggleMode());
        commands.add(new GetMode());

        startThread(commands, listener, false);
    }

    public void setProfile(String profile, OnCompleteListener listener) {
        ArrayList<Communication> commands = new ArrayList<>();
        commands.add(new SetProfile(profile));
        commands.add(new GetProfile());

        startThread(commands, listener, false);
    }

    public void setNotificationLight(int[][] colors, OnCompleteListener listener) {
        ArrayList<Communication> commands = new ArrayList<>();
        commands.add(new SetColor(colors));

        startThread(commands, listener, true);
    }

    public void unsetNotificationLight(OnCompleteListener listener) {
        if (blocker == null)
            return;
        synchronized (blocker) {
            blocker.notify();
        }
        blocker = null;
    }

    private void startThread(ArrayList<Communication> commands, OnCompleteListener listener, boolean keepLock) {
        surroundLock(commands);
        surroundStartAndEnd(commands);

        if(keepLock && blocker != null) {
            // TODO: Check how useful unsetting is (in EVERY case).
            if(listener != null)
                listener.onError("Lock already Blocked!");
            Log.e("Error!!", "Lock already Blocked!");
            unsetNotificationLight(listener);
            // TODO: Avoid time waiting (HACK for finishing unsetting).
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

        Executor executor = new Executor(commands, listener);
        if (keepLock && blocker == null) {
            blocker = executor;
            blocker.setKeepLock();
        }

        new Thread(executor).start();
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
        private boolean keepLock;

        Executor(ArrayList<Communication> commands, OnCompleteListener listener ) {
            this.commands = commands;
            this.listener = listener;
            keepLock = false;
        }

        public void setKeepLock() {
            keepLock = true;
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

                // Skip first Status Line.
                in.readLine();

                for (Communication communication : commands) {
                    if((communication instanceof Unlock) && keepLock) {
                        try {
                            synchronized (this) {
                                wait();
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    String input = communication.getCommand();
                    Log.e(TAG,"Input: >" + input );
                    Console.writeLine(">" + input);
                    out.println(input);

                    String output = in.readLine();
                    Log.d(TAG,"Output: <" + output );
                    boolean error = !communication.onRespond(output, listener);
                    if(error) {
                        Console.writeLine("!!!Error: " + output);
                    } else {
                        Console.writeLine("<" + output);
                    }
                    if(listener != null)
                        listener.onStepCompleted(communication);

                    // Sometimes after set the get returns old data.
                    //if(communication.isNeedsDelay()) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    //}
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
