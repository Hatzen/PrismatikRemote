package de.prismatikremote.hartz.prismatikremote.backend.commands;

/**
 * Created by kaiha on 08.04.2017.
 */
public class SetProfile extends Communication {

    private String profile = "";

    public SetProfile(String profile) {
        this.profile = profile;
    }

    @Override
    public String getCommand() {
        return "setprofile:" + profile;
    }

}
