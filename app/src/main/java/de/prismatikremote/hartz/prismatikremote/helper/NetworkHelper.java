package de.prismatikremote.hartz.prismatikremote.helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import de.prismatikremote.hartz.prismatikremote.GlobalVars;
import de.prismatikremote.hartz.prismatikremote.backend.Communicator;

/**
 * Created by kaiha on 23.04.2017.
 */

public class NetworkHelper {

    public final static int DEFAULT_PORT = 3636;
    public final static int DEFAULT_TIMEOUT = 200;

    private static final ExecutorService es = Executors.newFixedThreadPool(255);

    public static Communicator getCommunicator(Context context) {
        return ((GlobalVars) context.getApplicationContext()).getCommunicator();
    }

    public static boolean isValidNetwork(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    return true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    return false;
        }
        return false;
    }

    public static boolean hasAPIConnection(Context context) {
        try {
            NetworkHelper.getCommunicator(context).refreshState(null);
            return true;
        } catch (Exception e) {
            // TODO: Make work with Waiting without blocking. Threading..
            //UiHelper.showAlert(this, e.getMessage());
            return false;
        }
    }

    public static String getAvaiableConnection(Context context) {
        for( int i = 1; i < 255; i++) {
            String ip = getNetPartOfIpAddress(context) + i;
            Future<String> future = findSocket(ip, DEFAULT_PORT);
            Log.d("Avaibles Connections", "" + i);
            try {
                if (future.get() != null) {
                    return future.get();
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static String getNetPartOfIpAddress(Context context) {
        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        wm.getConnectionInfo().getNetworkId();
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        ip = ip.substring(0, ip.lastIndexOf('.')+1);
        return ip;
        // TODO: get subnetmask and build net address
    }

    public static Future<String> findSocket(final String ip, final int port) {
        return es.submit(new Callable<String>() {
            @Override
            public String call() {
                try {
                    Socket socket = new Socket();
                    socket.connect(new InetSocketAddress(ip, port), DEFAULT_TIMEOUT);
                    socket.close();
                    // es.shutdown();
                    return ip;
                } catch (Exception ex) {
                    return null;
                }
            }
        });
    }

}
