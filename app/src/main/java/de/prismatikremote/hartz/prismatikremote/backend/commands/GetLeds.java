package de.prismatikremote.hartz.prismatikremote.backend.commands;

import android.graphics.Rect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.prismatikremote.hartz.prismatikremote.backend.Communicator;
import de.prismatikremote.hartz.prismatikremote.backend.RemoteState;

/**
 * Created by kaiha on 08.04.2017.
 */
public class GetLeds extends Communication {
    @Override
    public String getCommand() {
        return "getleds";
    }

    @Override
    public boolean onRespond(String response, Communicator.OnCompleteListener listener ) {
        String prefix = "leds:";
        if (response.contains(prefix)) {
            response = response.replaceAll("[^0-9]+", " ");
            List<String> allNumbers = Arrays.asList(response.trim().split(" "));

            ArrayList<Rect> leds = new ArrayList<>();
            for (int i = 0; i < allNumbers.size(); i += 5) {
                Rect r = new Rect(Integer.valueOf(allNumbers.get(i+1)),
                        Integer.valueOf(allNumbers.get(i+2)),
                        Integer.valueOf(allNumbers.get(i+3)),
                        Integer.valueOf(allNumbers.get(i+4)));
                leds.add(r);
            }
            RemoteState.getInstance().setLeds(leds);
            return true;
        }
        return false;
    }
}
