package de.prismatikremote.hartz.prismatikremote.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import de.prismatikremote.hartz.prismatikremote.R;
import de.prismatikremote.hartz.prismatikremote.backend.Communicator;
import de.prismatikremote.hartz.prismatikremote.backend.commands.Communication;
import de.prismatikremote.hartz.prismatikremote.helper.NetworkHelper;
import de.prismatikremote.hartz.prismatikremote.helper.UiHelper;

public class Onboarding extends AppCompatActivity  implements Communicator.OnCompleteListener, View.OnClickListener  {
    private ProgressDialog dialog;

    public final static String PREFERENCES_KEY = "CONNECTION";

    public final static String KEY_SERVER_IP = "SERVER_IP";
    public final static String KEY_SERVER_PORT = "SERVER_PORT";
    public final static String KEY_API_KEY = "API_KEY";

    private Button saveButton;
    private Button scanButton;

    private EditText serverIpEditText;
    private EditText serverPortEditText;
    private EditText apiKeyEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        serverIpEditText = (EditText) findViewById(R.id.serverIpEditText);
        serverPortEditText = (EditText) findViewById(R.id.serverPortEditText);
        apiKeyEditText = (EditText) findViewById(R.id.apiKeyEditText);

        serverIpEditText.setText(NetworkHelper.getNetPartOfIpAddress(this));
        serverPortEditText.setText("" + NetworkHelper.DEFAULT_PORT);

        SharedPreferences preferences = getSharedPreferences(PREFERENCES_KEY, MODE_PRIVATE);
        if(!preferences.getString(KEY_SERVER_IP, "").equals("")) {
            serverIpEditText.setText(preferences.getString(KEY_SERVER_IP, ""));
            serverPortEditText.setText("" + preferences.getInt(KEY_SERVER_PORT, NetworkHelper.DEFAULT_PORT));
            apiKeyEditText.setText(preferences.getString(KEY_API_KEY, ""));
            save();
        }

        saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(this);
        scanButton = (Button) findViewById(R.id.scanButton);
        scanButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(saveButton == view) {
            save();
        }
        else if (scanButton == view) {
            final ProgressDialog dialog = ProgressDialog.show(this, "", "Loading. Please wait...", true);
            dialog.setCanceledOnTouchOutside(false);


            new AsyncTask<Object,Object,String>() {

                @Override
                protected String doInBackground(Object[] params) {
                    String resultIp =  NetworkHelper.getAvaiableConnection( Onboarding.this );
                    return resultIp;
                }

                @Override
                protected void onPostExecute(String o) {
                    super.onPostExecute(o);
                    dialog.dismiss();
                    if (o == null) {
                        UiHelper.showAlert(Onboarding.this, "No server found.");
                    } else {
                        serverIpEditText.setText(o);
                        save();
                    }
                }
            }.execute();

        }
    }

    private void save() {
        if (!NetworkHelper.isValidNetwork(this)) {
            UiHelper.showAlert(this, "No Wifi connection.");
            return;
        }

        load();

        // (TODO: Replace this with working code. Timeout and Exeption doesnt work.)
        NetworkHelper.getCommunicator(this).setConnection(
                getServerIp(),
                getServerPort(),
                getApiKey());
        NetworkHelper.getCommunicator(this).refreshState(this);
    }

    @Override
    public void onError(final String result) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
                UiHelper.showAlert(Onboarding.this, "Cannot establish connection: " + result);
            }
        });
    }

    @Override
    public void onStepCompleted(Communication communication) {

    }

    @Override
    public void onSuccess() {
        SharedPreferences preferences = getSharedPreferences(PREFERENCES_KEY, MODE_PRIVATE);

        preferences.edit().putString(KEY_SERVER_IP, getServerIp()).apply();
        preferences.edit().putInt(KEY_SERVER_PORT, getServerPort()).apply();
        preferences.edit().putString(KEY_API_KEY, getApiKey()).apply();

        dialog.dismiss();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private String getServerIp() {
        return serverIpEditText.getText().toString();
    }

    private int getServerPort() {
        return Integer.valueOf(serverPortEditText.getText().toString());
    }

    private String getApiKey() {
        return apiKeyEditText.getText().toString();
    }

    private void load() {
        dialog = ProgressDialog.show(this, "", "Loading. Please wait..", true);
    }
}
