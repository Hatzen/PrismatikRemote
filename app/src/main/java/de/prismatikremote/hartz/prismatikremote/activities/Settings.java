package de.prismatikremote.hartz.prismatikremote.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import de.prismatikremote.hartz.prismatikremote.R;

public class Settings extends Drawer implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        mDrawerLayout.addView(inflater.inflate(R.layout.activity_settings, null));

        Button allowNotifications = (Button) findViewById(R.id.allowNotifications);
        allowNotifications.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
        startActivity(intent);
    }
}
