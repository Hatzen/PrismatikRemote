package de.prismatikremote.hartz.prismatikremote.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;

import de.prismatikremote.hartz.prismatikremote.R;
import de.prismatikremote.hartz.prismatikremote.backend.Communicator;
import de.prismatikremote.hartz.prismatikremote.backend.RemoteState;
import de.prismatikremote.hartz.prismatikremote.backend.commands.Communication;

public class Widgets extends Drawer implements Communicator.OnCompleteListener {

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

    @Override
    public void onError(String result) {

    }

    @Override
    public void onStepCompleted(Communication communication) {
    }

    @Override
    public void onSuccess() {
    }
}
