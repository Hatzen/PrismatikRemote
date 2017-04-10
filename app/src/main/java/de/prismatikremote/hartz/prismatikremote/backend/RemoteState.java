package de.prismatikremote.hartz.prismatikremote.backend;

import java.util.ArrayList;

/**
 * Created by kaiha on 09.04.2017.
 */

public class RemoteState {

    public enum Status {
        ON,
        OFF,
        DEVICE_ERROR,
        UNKNOWN
    }

    public enum Mode {
        AMBILIGHT,
        MOODLAMP
    }

    private Status status = Status.UNKNOWN;
    private Mode mode = Mode.AMBILIGHT;
    private boolean statusApi;
    private ArrayList<String> profiles;
    private String profile;
    private int countLeds;

    private static final RemoteState INSTANCE = new RemoteState();

    private RemoteState() {}

    public static RemoteState getInstance() {
        return INSTANCE;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public boolean isStatusApi() {
        return statusApi;
    }

    public void setStatusApi(boolean statusApi) {
        this.statusApi = statusApi;
    }

    public ArrayList<String> getProfiles() {
        return profiles;
    }

    public void setProfiles(ArrayList<String> profiles) {
        this.profiles = profiles;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public int getCountLeds() {
        return countLeds;
    }

    public void setCountLeds(int countLeds) {
        this.countLeds = countLeds;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }
}
