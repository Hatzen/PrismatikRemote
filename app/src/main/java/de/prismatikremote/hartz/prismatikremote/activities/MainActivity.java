package de.prismatikremote.hartz.prismatikremote.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import de.prismatikremote.hartz.prismatikremote.R;
import de.prismatikremote.hartz.prismatikremote.backend.Communicator;
import de.prismatikremote.hartz.prismatikremote.backend.RemoteState;
import de.prismatikremote.hartz.prismatikremote.backend.commands.Communication;
import de.prismatikremote.hartz.prismatikremote.backend.commands.GetMode;
import de.prismatikremote.hartz.prismatikremote.backend.commands.GetStatus;
import de.prismatikremote.hartz.prismatikremote.backend.commands.SetColor;
import de.prismatikremote.hartz.prismatikremote.helper.UiHelper;

public class MainActivity extends Drawer implements Communicator.OnCompleteListener, View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        mDrawerLayout.addView(inflater.inflate(R.layout.activity_main, null));

        Button powerButton = (Button) findViewById(R.id.toggle_power);
        powerButton.setText(RemoteState.getInstance().getStatus() == RemoteState.Status.ON ? "On" : "Off");
        powerButton.setOnClickListener(this);

        Button toggleMode = (Button) findViewById(R.id.toggle_mode);
        toggleMode.setText(RemoteState.getInstance().getMode() == RemoteState.Mode.AMBILIGHT ? "Ambilight" : "Moodlamp");
        toggleMode.setOnClickListener(this);

        Button refreshState = (Button) findViewById(R.id.refresh_state);
        refreshState.setOnClickListener(this);

        Button redLights = (Button) findViewById(R.id.red_lights);
        redLights.setOnClickListener(this);

        Button unsetLights = (Button) findViewById(R.id.unset_lights);
        unsetLights.setOnClickListener(this);
    }

    @Override
    public void onError(String result) {
        //TODO: add param to identify source.
        dialog.dismiss();

        UiHelper.showAlert(this, result);
    }

    @Override
    public void onStepCompleted(Communication communication) {
        if( communication instanceof GetStatus) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Button powerButton = (Button) findViewById(R.id.toggle_power);
                    powerButton.setText(RemoteState.getInstance().getStatus() == RemoteState.Status.ON ? "On" : "Off");
                }
            });
        } else if (communication instanceof GetMode) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Button toggleMode = (Button) findViewById(R.id.toggle_mode);
                    toggleMode.setText(RemoteState.getInstance().getMode() == RemoteState.Mode.AMBILIGHT ? "Ambilight" : "Moodlamp");
                }
            });
        } else if (communication instanceof SetColor) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                }
            });
        }
    }

    @Override
    public void onSuccess() {
        //TODO: add param to identify source.
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onClick(View view) {
        load();
        if( view == findViewById(R.id.toggle_power)) {
            Communicator.getInstance().togglePower(this);
        } else if ( view == findViewById(R.id.toggle_mode)) {
            Communicator.getInstance().toggleMode(this);
        } else if ( view == findViewById(R.id.refresh_state)) {
            Communicator.getInstance().refreshState(this);
        } else if ( view == findViewById(R.id.red_lights)) {
            int[][] colors = new int[10][3];
            for (int i = 0; i < colors.length; i++) {
                colors[i][0] = 255;
                colors[i][1] = 0;
                colors[i][2] = 0;
            }
            Communicator.getInstance().setNotificationLight(colors, this);
        } else if ( view == findViewById(R.id.unset_lights)) {
            Communicator.getInstance().unsetNotificationLight(this);
            dialog.dismiss();
        }
    }

}
