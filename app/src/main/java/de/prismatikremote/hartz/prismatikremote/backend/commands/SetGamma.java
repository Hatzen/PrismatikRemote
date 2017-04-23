package de.prismatikremote.hartz.prismatikremote.backend.commands;

/**
 * Created by kaiha on 08.04.2017.
 */
public class SetGamma extends Communication {

    private double gamma = -1.0;

    public SetGamma(double gamma) {
        this.gamma = gamma;
    }

    @Override
    public String getCommand() {
        return "setgamma:" + gamma;
    }

    @Override
    public boolean isDelayNeeded() {
        return true;
    }
}
