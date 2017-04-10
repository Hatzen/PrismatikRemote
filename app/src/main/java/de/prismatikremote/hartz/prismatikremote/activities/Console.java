package de.prismatikremote.hartz.prismatikremote.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;

import de.prismatikremote.hartz.prismatikremote.R;

public class Console extends Drawer {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mDrawerLayout.addView(inflater.inflate(R.layout.activity_console, null, false));
    }
}
