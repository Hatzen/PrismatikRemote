package de.prismatikremote.hartz.prismatikremote.backend.commands;

/**
 * Created by kaiha on 08.04.2017.
 */
public class SetBrightness extends Communication {

    private int brightness = -1;

    public SetBrightness(int brightness) {
        this.brightness = brightness;
    }

    @Override
    public String getCommand() {
        return "setbrightness:" + brightness;
    }

    @Override
    public boolean isDelayNeeded() {
        return true;
    }
}
