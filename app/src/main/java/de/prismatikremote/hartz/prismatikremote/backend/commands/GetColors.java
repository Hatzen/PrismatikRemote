package de.prismatikremote.hartz.prismatikremote.backend.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.prismatikremote.hartz.prismatikremote.backend.Communicator;
import de.prismatikremote.hartz.prismatikremote.backend.RemoteState;
import de.prismatikremote.hartz.prismatikremote.helper.UiHelper;

/**
 * Created by kaiha on 08.04.2017.
 */
public class GetColors extends Communication {
    @Override
    public String getCommand() {
        return "getcolors";
    }

    @Override
    public boolean onRespond(String response, Communicator.OnCompleteListener listener ) {
        String prefix = "colors:";
        if (response.contains(prefix)) {
            response = response.replaceAll("[^0-9]+", " ");
            List<String> allNumbers = Arrays.asList(response.trim().split(" "));

            ArrayList<Integer> colors = new ArrayList<>();
            int[] color = new int[3];
            for (int i = 0; i < allNumbers.size(); i += 4) {
                color[0] = Integer.valueOf(allNumbers.get(i+1));
                color[1] = Integer.valueOf(allNumbers.get(i+2));
                color[2] = Integer.valueOf(allNumbers.get(i+3));

                colors.add(UiHelper.toColorInt(color));
            }
            RemoteState.getInstance().setColors(colors);
            return true;
        }
        return false;
    }
}
