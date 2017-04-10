package de.prismatikremote.hartz.prismatikremote.backend.commands;

/**
 * Created by kaiha on 08.04.2017.
 */
public class SetColor extends Communication {

    private int[][] colors;
    private int singleTarget = -1;

    public SetColor(int[][] colors) {
        this.colors = colors;
    }

    public SetColor(int singleTarget, int[] color) {
        this.singleTarget = singleTarget;
        this.colors[singleTarget] = color;
    }

    @Override
    public String getCommand() {
        String command = "setcolor:";
        if (singleTarget != -1)
            return command + getSingleLedString(singleTarget);

        for (int i = 0; i < colors.length;i++) {
            command = command + getSingleLedString(i);
        }
        return command;
    }

    private String getSingleLedString(int pos) {
        return (pos+1) + "-" + colors[pos][0] + "," + colors[pos][1] + "," + colors[pos][2] + ";";
    }

}
