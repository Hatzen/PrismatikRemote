package de.prismatikremote.hartz.prismatikremote.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import de.prismatikremote.hartz.prismatikremote.R;
import de.prismatikremote.hartz.prismatikremote.backend.Communicator;
import de.prismatikremote.hartz.prismatikremote.backend.RemoteState;
import de.prismatikremote.hartz.prismatikremote.backend.commands.Communication;
import de.prismatikremote.hartz.prismatikremote.helper.Helper;
import de.prismatikremote.hartz.prismatikremote.helper.UiHelper;

public class Widgets extends Drawer implements Communicator.OnCompleteListener {

    public enum Schema {
        ANDROMEDA,
        CASSIOPEIA,
        PEGASUS
    }

    public final static String PREFERENCES_WIDGETS_KEY = "CONNECTION";

    public final static String KEY_SCREEN_COUNT = "SCREEN_COUNT";
    public final static String KEY_SCREEN_HEIGHT = "KEY_SCREEN_HEIGHT";
    public final static String KEY_SCREEN_WIDTH = "KEY_SCREEN_WIDTH";

    private EditText screenCountEditText;
    private EditText screenWidthEditText;
    private EditText screenHeightEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        mDrawerLayout.addView(inflater.inflate(R.layout.activity_widgets, null));

        screenCountEditText = (EditText) findViewById(R.id.screen_count);
        screenWidthEditText = (EditText) findViewById(R.id.resolution_width);
        screenHeightEditText = (EditText) findViewById(R.id.resolution_height);

        SharedPreferences preferences = getSharedPreferences(PREFERENCES_WIDGETS_KEY, MODE_PRIVATE);
        int screenCount = preferences.getInt(KEY_SCREEN_COUNT, 1);
        if(screenCount != 1) {
            screenCountEditText.setText("" + screenCount);
            screenWidthEditText.setText("" + preferences.getInt(KEY_SCREEN_WIDTH, 1920));
            screenHeightEditText.setText("" + preferences.getInt(KEY_SCREEN_HEIGHT, 1080));
        }

        findViewById(R.id.apply_screens).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences preferences = getSharedPreferences(PREFERENCES_WIDGETS_KEY, MODE_PRIVATE);

                preferences.edit().putInt(KEY_SCREEN_COUNT, getScreenCount()).apply();
                preferences.edit().putInt(KEY_SCREEN_HEIGHT, getScreenHeight()).apply();
                preferences.edit().putInt(KEY_SCREEN_WIDTH, getScreenWidth()).apply();

                setupScreens();
            }
        });

        setupScreens();
    }

    private int getScreenWidth() {
        return Integer.valueOf(screenWidthEditText.getText().toString());
    }

    private int getScreenHeight() {
        return Integer.valueOf(screenHeightEditText.getText().toString());
    }

    private int getScreenCount() {
        return Integer.valueOf(screenCountEditText.getText().toString());
    }

    private double getScreenHeightFactor() {
        return (double) getScreenHeight()/getScreenWidth();
    }

    private int getCurrentScreen() {
        ArrayList<Rect> rects = RemoteState.getInstance().getLeds();

        if(rects.size() != 0) {
            //return (rects.get(0).top / getScreenHeight());
            return (rects.get(0).left / getScreenWidth());
        }
        return -1;
    }

    private void setupScreens() {
        final Context context = this;
        final FrameLayout screenCanvas = (FrameLayout) findViewById(R.id.screen_canvas);
        screenCanvas.removeAllViews();

        screenCanvas.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                double screenWidth = (double) screenCanvas.getWidth() / getScreenCount();
                double screenHeight = screenWidth * getScreenHeightFactor();

                for(int i = 0; i < getScreenCount(); i++) {
                    final FrameLayout screen = new FrameLayout(context);
                    int width = (int) (screenWidth);
                    int height = (int) (screenHeight);
                    int left = (int) (i * screenWidth);
                    int top = (screenCanvas.getHeight() / 2) - (height / 2);

                    FrameLayout.LayoutParams llp = new FrameLayout.LayoutParams(width, height);
                    llp.setMargins(left, top , 0, 0);
                    screen.setLayoutParams(llp);
                    screenCanvas.addView(screen);

                    TextView rect = new TextView(context);
                    final int screenNumber = i;
                    rect.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                            vibe.vibrate(100);
                            final CharSequence[] items = {"ANDROMEDA",
                                    "CASSIOPEIA",
                                    "PEGASUS"};

                            AlertDialog.Builder builder = new AlertDialog.Builder(Widgets.this);
                            builder.setTitle("Choose schema:");
                            builder.setItems(items, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int item) {
                                    if ( item == 0) {
                                        setSchema(Schema.ANDROMEDA, screenNumber);
                                    } else if (item == 1) {
                                        setSchema(Schema.CASSIOPEIA, screenNumber);
                                    } else if (item == 2) {
                                        setSchema(Schema.PEGASUS, screenNumber);
                                    }
                                }
                            });
                            AlertDialog alert = builder.create();
                            alert.show();
                        }
                    });
                    rect.setText("" + (i+1));
                    rect.setGravity(Gravity.CENTER);
                    rect.setTextColor(getResources().getColor(R.color.colorWhite));
                    rect.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    FrameLayout.LayoutParams llp2 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    rect.setLayoutParams(llp2);
                    screen.addView(rect);

                    if(i == getCurrentScreen()) {
                        screen.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                            @Override
                            public void onGlobalLayout() {
                                updateCanvas(screen);
                                screen.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            }
                        });
                    }
                }
                screenCanvas.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    /**
     * Draws the leds on the screen.
     * @param screen
     */
    private void updateCanvas(ViewGroup screen) {
        double scaleX = (double) screen.getWidth() / getScreenWidth();
        double scaleY = (double) screen.getHeight() / getScreenHeight();

        double translateX = (double) getScreenWidth()*getCurrentScreen();
        // TODO: calculate..
        double translateY = 0;

        ArrayList<Integer> colors = RemoteState.getInstance().getColors();
        ArrayList<Rect> rects = RemoteState.getInstance().getLeds();
        for(int i = 0; i < rects.size(); i++) {
            TextView rect = new TextView(this);
            rect.setText("" + (i+1));

            rect.setGravity(Gravity.CENTER);
            rect.setBackgroundColor(colors.get(i));

            int width = (int) (rects.get(i).width() * scaleX);
            int height = (int) (rects.get(i).height() *scaleY);
            int left = (int) ((rects.get(i).left - translateX) * scaleX);
            int top = (int) ((rects.get(i).top - translateY) * scaleY);

            FrameLayout.LayoutParams llp = new FrameLayout.LayoutParams(width, height);
            llp.setMargins(left, top , 0, 0);
            rect.setLayoutParams(llp);

            screen.addView(rect);
        }
    }

    private void setSchema(Schema schema, int screen) {
        Rect[] leds = new Rect[RemoteState.getInstance().getCountLeds()];

        int offsetX = screen*getScreenWidth();
        int offsetY = 0; // TODO: Do calculation here.
        int percentageOfScreen = 10;

        switch (schema) {
            case ANDROMEDA:
                if (leds.length == 10) {
                    // top and bottom rects.
                    int longerRectWidth = getScreenWidth()/4;
                    int longerRectHeight = getScreenHeight()/percentageOfScreen;
                    // left and right rects.
                    int widerRectWidth = getScreenWidth()/percentageOfScreen;
                    int widerRectHeight = getScreenHeight()/2;

                    // Bottom right.
                    int rectId = 0;
                    int x = getScreenWidth()-longerRectWidth + offsetX;
                    int y = getScreenHeight()-longerRectHeight + offsetY;
                    leds[rectId] = new Rect(x, y, x + longerRectWidth, y + longerRectHeight);

                    // Right.
                    rectId++;
                    x = getScreenWidth()-widerRectWidth + offsetX;
                    y = getScreenHeight()-widerRectHeight + offsetY;
                    leds[rectId] = new Rect(x, y, x + widerRectWidth, y + widerRectHeight);
                    rectId++;
                    x = getScreenWidth()-widerRectWidth + offsetX;
                    y = getScreenHeight()-(2*widerRectHeight) + offsetY;
                    leds[rectId] = new Rect(x, y, x + widerRectWidth, y + widerRectHeight);

                    // Top.
                    rectId++;
                    x = getScreenWidth()-longerRectWidth + offsetX;
                    y = offsetY;
                    leds[rectId] = new Rect(x, y, x + longerRectWidth, y + longerRectHeight);
                    rectId++;
                    x = getScreenWidth()-2*longerRectWidth + offsetX;
                    y = offsetY;
                    leds[rectId] = new Rect(x, y, x + longerRectWidth, y + longerRectHeight);
                    rectId++;
                    x = getScreenWidth()-3*longerRectWidth + offsetX;
                    y = offsetY;
                    leds[rectId] = new Rect(x, y, x + longerRectWidth, y + longerRectHeight);
                    rectId++;
                    x = getScreenWidth()-4*longerRectWidth + offsetX;
                    y = offsetY;
                    leds[rectId] = new Rect(x, y, x + longerRectWidth, y + longerRectHeight);

                    // Left.
                    rectId++;
                    x = offsetX;
                    y = getScreenHeight()-2*widerRectHeight + offsetY;
                    leds[rectId] = new Rect(x, y, x + widerRectWidth, y + widerRectHeight);
                    rectId++;
                    x = offsetX;
                    y = getScreenHeight()-widerRectHeight + offsetY;
                    leds[rectId] = new Rect(x, y, x + widerRectWidth, y + widerRectHeight);

                    // Botttom left.
                    rectId++;
                    x = offsetX;
                    y = getScreenHeight()-longerRectHeight + offsetY;
                    leds[rectId] = new Rect(x, y, x + longerRectWidth, y + longerRectHeight);
                }
                break;
            // Has no bottom leds.
            case CASSIOPEIA:
                // TODO: Optimize/support all amount of leds and move to helper class.
                if (leds.length == 10) {
                    // top and bottom rects.
                    int longerRectWidth = getScreenWidth()/4;
                    int longerRectHeight = getScreenHeight()/percentageOfScreen;
                    // left and right rects.
                    int widerRectWidth = getScreenWidth()/percentageOfScreen;
                    int widerRectHeight = getScreenHeight()/3;

                    int rectId = 0;
                    int x;
                    int y;

                    x = getScreenWidth()-widerRectWidth + offsetX;
                    y = getScreenHeight()-widerRectHeight + offsetY;
                    leds[rectId] = new Rect(x, y, x + widerRectWidth, y + widerRectHeight);
                    rectId++;
                    x = getScreenWidth()-widerRectWidth + offsetX;
                    y = getScreenHeight()-(2*widerRectHeight) + offsetY;
                    leds[rectId] = new Rect(x, y, x + widerRectWidth, y + widerRectHeight);
                    rectId++;
                    x = getScreenWidth()-widerRectWidth + offsetX;
                    y = getScreenHeight()-(3*widerRectHeight) + offsetY;
                    leds[rectId] = new Rect(x, y, x + widerRectWidth, y + widerRectHeight);

                    rectId++;
                    x = getScreenWidth()-longerRectWidth + offsetX;
                    y = offsetY;
                    leds[rectId] = new Rect(x, y, x + longerRectWidth, y + longerRectHeight);
                    rectId++;
                    x = getScreenWidth()-2*longerRectWidth + offsetX;
                    y = offsetY;
                    leds[rectId] = new Rect(x, y, x + longerRectWidth, y + longerRectHeight);
                    rectId++;
                    x = getScreenWidth()-3*longerRectWidth + offsetX;
                    y = offsetY;
                    leds[rectId] = new Rect(x, y, x + longerRectWidth, y + longerRectHeight);
                    rectId++;
                    x = getScreenWidth()-4*longerRectWidth + offsetX;
                    y = offsetY;
                    leds[rectId] = new Rect(x, y, x + longerRectWidth, y + longerRectHeight);

                    rectId++;
                    x = offsetX;
                    y = getScreenHeight()-3*widerRectHeight + offsetY;
                    leds[rectId] = new Rect(x, y, x + widerRectWidth, y + widerRectHeight);
                    rectId++;
                    x = offsetX;
                    y = getScreenHeight()-2*widerRectHeight + offsetY;
                    leds[rectId] = new Rect(x, y, x + widerRectWidth, y + widerRectHeight);
                    rectId++;
                    x = offsetX;
                    y = getScreenHeight()-widerRectHeight + offsetY;
                    leds[rectId] = new Rect(x, y, x + widerRectWidth, y + widerRectHeight);
                }
                break;
            // Has only side leds.
            case PEGASUS:
                // left and right rects.
                int widerRectWidth = getScreenWidth()/percentageOfScreen;
                int widerRectHeight = getScreenHeight()/(leds.length/2);

                int rectId = 0;
                int x = getScreenWidth()-widerRectWidth + offsetX;
                int y = getScreenHeight() + offsetY;

                // 1 from bottom to top, -1 from top to bottom.
                int direction = 1;

                for(int z = 0; z < 2; z++) {
                    for(int i = 0; i < (leds.length/2); i++) {
                        y -= direction*widerRectHeight;
                        leds[rectId] = new Rect(x, y, x + widerRectWidth, y + widerRectHeight);
                        rectId++;
                    }
                    x = offsetX;
                    y -= direction*widerRectHeight;
                    direction = -1;
                }
                break;
        }
        if(leds[0] == null) {
            UiHelper.showAlert(this, "Unsupported amount of leds for that schema.");
            return;
        }

        Helper.getCommunicator(this).setLeds(leds, this);
        load();

    }

    @Override
    public void onError(String result) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onStepCompleted(Communication communication) {
    }

    @Override
    public void onSuccess() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
                Toast.makeText(Widgets.this, "Successfully set leds!", Toast.LENGTH_LONG);
                setupScreens();
            }
        });
    }
}
