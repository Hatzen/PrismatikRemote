package de.prismatikremote.hartz.prismatikremote.services;


import android.content.Context;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import de.prismatikremote.hartz.prismatikremote.backend.Communicator;
import de.prismatikremote.hartz.prismatikremote.backend.RemoteState;
import de.prismatikremote.hartz.prismatikremote.helper.UiHelper;

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

    }

    @Override
    public void onNotificationPosted(StatusBarNotification statusBarNotification) {
        refreshLights();
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i("Msg","Notification Removed");
        refreshLights();

    }

    private void refreshLights() {
        HashMap<StatusBarNotification,Integer> occurence = new HashMap();

        for (StatusBarNotification sbn : getActiveNotifications()) {

            Integer count = occurence.get(sbn);
            occurence.put(sbn, count != null ? count+1 : 0);


            Log.d("NotificationServce", "" + sbn.getPackageName());

        }

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
                int[] color = UiHelper.getAverageColorRGB(
                        ((Map.Entry<StatusBarNotification, Integer>)a[i]).getKey().getNotification().largeIcon);
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
                colors[i] = UiHelper.getAverageColorRGB(
                        ((Map.Entry<StatusBarNotification, Integer>) a[i]).getKey().getNotification().largeIcon);
            }
        }

        Communicator.getInstance().setNotificationLight(colors);
    }


}

