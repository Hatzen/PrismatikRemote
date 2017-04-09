package de.prismatikremote.hartz.prismatikremote;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import de.prismatikremote.hartz.prismatikremote.backend.Communicator;
import de.prismatikremote.hartz.prismatikremote.backend.RemoteState;
import de.prismatikremote.hartz.prismatikremote.backend.commands.Communication;
import de.prismatikremote.hartz.prismatikremote.backend.commands.GetStatus;
import de.prismatikremote.hartz.prismatikremote.helper.UiHelper;

//TODO: Main-feature http://www.androiddevelopersolutions.com/2015/05/android-read-status-bar-notification.html
public class MainActivity extends AppCompatActivity implements Communicator.OnCompleteListener, View.OnClickListener {

    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //TODO: Replace with Data from OnBoarding Screen.
        Communicator.getInstance().setConnection("192.168.2.118", 3636, this);

        Button powerButton = (Button) findViewById(R.id.toggle_power);
        powerButton.setOnClickListener(this);
        load();
    }

    @Override
    public void onError(String result) {
        //TODO: add param to identify source.
        dialog.dismiss();

        UiHelper.showAlert(this, result);
    }

    @Override
    public void onStepCompletet(Communication communication) {
        if( communication instanceof GetStatus) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Button powerButton = (Button) findViewById(R.id.toggle_power);
                    powerButton.setText(RemoteState.getInstance().getStatus() == RemoteState.Status.ON ? "Off" : "On");
                }
            });
        }
    }

    @Override
    public void onSuccess() {
        //TODO: add param to identify source.
        //dialog.dismiss();
    }

    @Override
    public void onClick(View v) {
        load();
        Communicator.getInstance().togglePower(this);
    }

    public void load() {
        //dialog = ProgressDialog.show(this, "", "Loading. Please wait..", true);
    }
}
