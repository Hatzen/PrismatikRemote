package de.prismatikremote.hartz.prismatikremote.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import de.prismatikremote.hartz.prismatikremote.R;
import de.prismatikremote.hartz.prismatikremote.backend.Communicator;
import de.prismatikremote.hartz.prismatikremote.backend.RemoteState;
import de.prismatikremote.hartz.prismatikremote.backend.commands.Communication;
import de.prismatikremote.hartz.prismatikremote.helper.Helper;
import de.prismatikremote.hartz.prismatikremote.helper.UiHelper;

public class Settings extends Drawer implements View.OnClickListener, Communicator.OnCompleteListener  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        mDrawerLayout.addView(inflater.inflate(R.layout.activity_settings, null));

        Button allowNotifications = (Button) findViewById(R.id.allowNotifications);
        allowNotifications.setOnClickListener(this);
        Button applySettings = (Button) findViewById(R.id.apply_settings);
        applySettings.setOnClickListener(this);
        updateUi();
    }

    private void updateUi() {
        ((SeekBar) findViewById(R.id.gamma)).setProgress((int) (RemoteState.getInstance().getGamma() * 100));
        ((SeekBar) findViewById(R.id.brightness)).setProgress(RemoteState.getInstance().getBrightness());
        ((SeekBar) findViewById(R.id.smoothness)).setProgress(RemoteState.getInstance().getSmoothness());
    }

    @Override
    public void onClick(View v) {
        if(v == findViewById(R.id.allowNotifications)) {
            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            startActivity(intent);
        } else if(v == findViewById(R.id.apply_settings)) {
            double gamma = ((SeekBar) findViewById(R.id.gamma)).getProgress() / 100;
            int brightness = ((SeekBar) findViewById(R.id.brightness)).getProgress();
            int smoothness = ((SeekBar) findViewById(R.id.smoothness)).getProgress();

            Helper.getCommunicator(this).setSettings(gamma, brightness, smoothness, this);
            load();
        }
    }

    @Override
    public void onError(final String result) {
        final Context context = this;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
                UiHelper.showAlert(context, result);
            }
        });
    }

    @Override
    public void onStepCompleted(Communication communication) {

    }

    @Override
    public void onSuccess() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateUi();
                dialog.dismiss();
            }
        });
    }
}
