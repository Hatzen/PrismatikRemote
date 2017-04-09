package de.prismatikremote.hartz.prismatikremote.backend.commands;

import de.prismatikremote.hartz.prismatikremote.backend.Communicator;
import de.prismatikremote.hartz.prismatikremote.backend.RemoteState;

/**
 * Created by kaiha on 08.04.2017.
 */
public class GetStatus extends Communication {
    private static String TAG = "GetStatus";
    public static final String RESPOND_ON = "status:on";

    @Override
    public String getCommand() {
        return "getstatus";
    }

    @Override
    public boolean onRespond(String response, Communicator.OnCompleteListener listener ) {
        RemoteState.Status currentStatus;
        switch(response) {
            case RESPOND_ON:
                currentStatus = RemoteState.Status.ON;
                break;
            case "status:off":
                currentStatus = RemoteState.Status.OFF;
                break;
            case "status:device error":
                currentStatus = RemoteState.Status.DEVICE_ERROR;
                break;
            case "status:unknown":
            default:
                currentStatus = RemoteState.Status.UNKNOWN;
        }
        RemoteState.getInstance().setStatus(currentStatus);

        return response.equals(GetStatus.RESPOND_ON);
    }
}
