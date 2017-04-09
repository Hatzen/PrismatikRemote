package de.prismatikremote.hartz.prismatikremote;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import de.prismatikremote.hartz.prismatikremote.backend.Communicator;
import de.prismatikremote.hartz.prismatikremote.helper.UiHelper;

public class Onboarding extends AppCompatActivity  implements View.OnClickListener  {

    private Button saveButton;
    private Button scanButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

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

        }
    }

    private void save() {
        try {
            EditText serverIpEditText = (EditText) findViewById(R.id.serverIpEditText);
            EditText serverPortEditText = (EditText) findViewById(R.id.serverPortEditText);

            Communicator.getInstance().setConnection(serverIpEditText.getText().toString(), Integer.valueOf(serverPortEditText.getText().toString()), null);
        } catch (Exception e) {
            UiHelper.showAlert(this, e.getMessage());
        }
    }
}
