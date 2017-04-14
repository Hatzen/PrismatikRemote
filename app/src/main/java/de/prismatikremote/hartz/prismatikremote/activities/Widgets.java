package de.prismatikremote.hartz.prismatikremote.activities;

import android.os.Bundle;
import android.view.LayoutInflater;

import de.prismatikremote.hartz.prismatikremote.R;
import de.prismatikremote.hartz.prismatikremote.backend.Communicator;
import de.prismatikremote.hartz.prismatikremote.backend.commands.Communication;

public class Widgets extends Drawer implements Communicator.OnCompleteListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        mDrawerLayout.addView(inflater.inflate(R.layout.activity_widgets, null));

    }

    @Override
    public void onError(String result) {

    }

    @Override
    public void onStepCompleted(Communication communication) {
    }

    @Override
    public void onSuccess() {
    }
}
