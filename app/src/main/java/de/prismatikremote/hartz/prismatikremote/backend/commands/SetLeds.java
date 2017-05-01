package de.prismatikremote.hartz.prismatikremote.backend.commands;

import android.graphics.Rect;

/**
 * Created by kaiha on 08.04.2017.
 */
public class SetLeds extends Communication {

    private Rect[] rects;

    public SetLeds(Rect[] rects) {
        this.rects = rects;
    }

    @Override
    public String getCommand() {
        String command = "setleds:";

        for (int i = 0; i < rects.length; i++) {
            command = command + getSingleLedString(i);
        }
        return command;
    }

    private String getSingleLedString(int pos) {
        return (pos+1) + "-" + rects[pos].left + "," + rects[pos].top + "," + rects[pos].width() + "," + rects[pos].height() + ";";
    }

    @Override
    public boolean isDelayNeeded() {
        return true;
    }
}
