package de.prismatikremote.hartz.prismatikremote.helper;

import android.content.Context;
import android.support.v7.app.AlertDialog;

/**
 * Created by kaiha on 09.04.2017.
 */

public class UiHelper {

    public static void showAlert(Context context, String message) {
        new AlertDialog.Builder(context)
                .setTitle("Error")
                .setMessage(message)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

}
