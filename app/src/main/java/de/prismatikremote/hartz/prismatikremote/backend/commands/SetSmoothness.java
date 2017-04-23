package de.prismatikremote.hartz.prismatikremote.backend.commands;

/**
 * Created by kaiha on 08.04.2017.
 */
public class SetSmoothness extends Communication {

    private int smoothness = -1;

    public SetSmoothness(int smoothness) {
        this.smoothness = smoothness;
    }

    @Override
    public String getCommand() {
        return "setsmooth:" + smoothness;
    }

    @Override
    public boolean isDelayNeeded() {
        return true;
    }
}
