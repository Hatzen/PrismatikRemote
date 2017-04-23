package de.prismatikremote.hartz.prismatikremote.services;


import android.content.Context;
import android.content.SharedPreferences;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import de.prismatikremote.hartz.prismatikremote.activities.Notifications;
import de.prismatikremote.hartz.prismatikremote.activities.Onboarding;
import de.prismatikremote.hartz.prismatikremote.backend.RemoteState;
import de.prismatikremote.hartz.prismatikremote.helper.Helper;
import de.prismatikremote.hartz.prismatikremote.helper.UiHelper;
import de.prismatikremote.hartz.prismatikremote.model.ColorObject;

/**
 * Created by kaiha on 09.04.2017.
 */
public class NotificationService extends NotificationListenerService {

    Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

        // TODO: NotificationManager.isNotificationPolicyAccessGranted(), to check if notifications access is granted

        // TODO: FIX BUG!!! Service is not using same Executor in Communicator.

    }

    @Override
    public void onNotificationPosted(StatusBarNotification statusBarNotification) {
        refreshLights();
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        refreshLights();
    }

    private void refreshLights() {
        Log.e("error", "uneneror11");
        HashMap<String, ColorObject> colorObjects = Notifications.loadSerializedColors(getBaseContext());

        SharedPreferences preferences = getSharedPreferences(Onboarding.PREFERENCES_KEY, MODE_PRIVATE);
        if(!preferences.getString(Onboarding.KEY_SERVER_IP, "").equals("")) {
            Helper.getCommunicator(this).setConnection(
                    preferences.getString(Onboarding.KEY_SERVER_IP, ""),
                    preferences.getInt(Onboarding.KEY_SERVER_PORT, 3636),
                    preferences.getString(Onboarding.KEY_API_KEY, ""));
        }
        Log.e("error", "uneneror1");
        HashMap<StatusBarNotification,Integer> occurence = new HashMap();
        boolean lightsOff = true;
        for (StatusBarNotification sbn : getActiveNotifications()) {
            ColorObject colorObject = colorObjects.get(sbn.getPackageName());
            if (colorObject != null && colorObject.regard) {
                Log.e("error", "uneneror1");

                lightsOff = false;
                Integer count = occurence.get(sbn);
                occurence.put(sbn, count != null ? count+1 : 0);
            }
        }
        Log.e("error", "uneneror2");
        // There are no notifications to display. So set lights off (Use prismatik default lights).
        if(lightsOff) {
            Helper.getCommunicator(this).unsetNotificationLight(null);
            return;
        }
        Log.e("error", "uneneror3");

        // Sort Map to occurence
        Object[] a = occurence.entrySet().toArray();
        Arrays.sort(a, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Map.Entry<StatusBarNotification, Integer>) o2).getValue()
                        .compareTo(((Map.Entry<StatusBarNotification, Integer>) o1).getValue());
            }
        });

        int lengthOfKeys = occurence.keySet().size();
        int ledCount = RemoteState.getInstance().getCountLeds();

        if (lengthOfKeys == 0) {
            return;
        }

        // Init colors.
        int[][] colors = new int[ledCount][3];
        if(lengthOfKeys < ledCount) {
            // "Stretch" them to always use all Leds.
            int stepSize = (ledCount/lengthOfKeys)+1;

            // TODO: Reduce to match. (Already done?)
            for(int i = 0; i < lengthOfKeys; i++) {
                int[] color = getColorForStatusBarNotification(
                        ((Map.Entry<StatusBarNotification, Integer>)a[i]).getKey()
                        , colorObjects);
                for(int j = 0; j < stepSize;j++) {
                    if(j+(i*stepSize) >= colors.length) {
                        break;
                    }
                    colors[j+(i*stepSize)] = color;
                }
            }
        } else {
            // Take the first matching ones.
            for (int i = 0; i < ledCount; i++) {
                colors[i] = getColorForStatusBarNotification(
                        ((Map.Entry<StatusBarNotification, Integer>) a[i]).getKey()
                        , colorObjects);
            }
        }

        Helper.getCommunicator(this).setNotificationLight(colors, null);
    }

    private int[] getColorForStatusBarNotification(StatusBarNotification sbn, HashMap<String, ColorObject> colors) {
        int[] color = new int[3];
        color[0] = 255;
        color[1] = 255;
        color[2] = 255;

        if(colors.get(sbn.getPackageName()) != null) {
            color = UiHelper.toColorInts(colors.get(sbn.getPackageName()).color);
        }
        return color;
    }

}

