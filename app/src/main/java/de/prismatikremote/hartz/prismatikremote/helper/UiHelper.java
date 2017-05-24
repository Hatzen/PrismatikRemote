package de.prismatikremote.hartz.prismatikremote.helper;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;

/**
 * Created by kaiha on 09.04.2017.
 */

public class UiHelper {

    public static void showAlert(Context context, String message) {
        new AlertDialog.Builder(context)
                .setTitle("Error")
                .setMessage(message)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Ok", null)
                .show();
    }

    public static void showYesNoAlert(Context context, String message, DialogInterface.OnClickListener dialogClickListener ) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setMessage(message).setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    /**
     * Calculate the average red, green, blue color values of a bitmap
     *
     * @param bitmap
     *            a {@link Bitmap}
     * @return
     */
    public static int[] getAverageColorRGB(Bitmap bitmap) {
        if(bitmap == null) {
            int[] color = {255,0,0};
            return color;
        }
        final int width = bitmap.getWidth();
        final int height = bitmap.getHeight();
        int size = width * height;
        int pixelColor;
        int r, g, b;
        r = g = b = 0;
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                pixelColor = bitmap.getPixel(x, y);
                if (pixelColor == 0) {
                    size--;
                    continue;
                }
                r += Color.red(pixelColor);
                g += Color.green(pixelColor);
                b += Color.blue(pixelColor);
            }
        }
        r /= size;
        g /= size;
        b /= size;
        return new int[] {
                r, g, b
        };
    }

    public static int toColorInt(int[] color) {
        int argb = 255; // No Alpha.
        argb = (argb << 8) + color[0];
        argb = (argb << 8) + color[1];
        argb = (argb << 8) + color[2];
        return argb;
    }

    public static int[] toColorInts(int color) {
        int rgb[] = new int[3];
        rgb[0] = (color >> 16) & 0x000000FF;
        rgb[1] = (color >> 8) & 0x000000FF;
        rgb[2] = color & 0x000000FF;
        return rgb;
    }
}
