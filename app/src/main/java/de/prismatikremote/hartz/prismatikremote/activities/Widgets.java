package de.prismatikremote.hartz.prismatikremote.activities;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        mDrawerLayout.addView(inflater.inflate(R.layout.activity_widgets, null));

        findViewById(R.id.apply_screens).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCanvas();
            }
        });

        updateCanvas();
    }

    private int getScreenWidth() {
        return Integer.valueOf(((EditText) findViewById(R.id.resolution_width)).getText().toString());
    }

    private int getScreenHeight() {
        return Integer.valueOf(((EditText) findViewById(R.id.resolution_height)).getText().toString());
    }

    private int getScreenCount() {
        return Integer.valueOf(((EditText) findViewById(R.id.screen_count)).getText().toString());
    }

    private int getCurrentScreen() {
        ArrayList<Rect> rects = RemoteState.getInstance().getLeds();

        if(rects.size() != 0) {
            //return (rects.get(0).top / getScreenHeight());
            return (rects.get(0).left / getScreenWidth());
        }
        return -1;
    }

    private void updateCanvas() {
        final Context context = this;

        final FrameLayout screenCanvas = (FrameLayout) findViewById(R.id.screen_canvas);
        screenCanvas.removeAllViews();

        screenCanvas.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                Log.e("Taggaasas", " &12412 " + screenCanvas.getWidth());

                double scaleX = (double) screenCanvas.getWidth() / getScreenWidth();
                double scaleY = (double) screenCanvas.getHeight() / getScreenHeight();

                //scaleX = 1;
                //scaleY = 1;


                //Log.e("Taggaasas", " &12412 " + getScreenWidth());
                //Log.e("Taggaasas", " &12412 " + scaleX);
                //Log.e("Taggaasas", " &12412 " + (screenCanvas.getWidth() / getScreenWidth()));

                double translateX = (double) getScreenWidth()*getCurrentScreen();
                // TODO: calculate..
                double translateY = 0;


                Log.e("Taggaasas", "ScaleX: " + scaleX + " & ScaleY: " + scaleY + " & translateX:" + translateX);

                ArrayList<Integer> colors = RemoteState.getInstance().getColors();
                ArrayList<Rect> rects = RemoteState.getInstance().getLeds();
                for(int i = 0; i < rects.size(); i++) {
                    TextView rect = new TextView(context);
                    rect.setText("" + i);
                    rect.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    rect.setBackgroundColor(colors.get(i));

                    Log.e("Taggaasas", rects.get(i).left + " ," + rects.get(i).top + " ," + rects.get(i).width() + " ," + rects.get(i).height() + " ,");

                    int width = (int) (rects.get(i).width() * scaleX);
                    int height = (int) (rects.get(i).height() *scaleY);
                    int left = (int) ((rects.get(i).left - translateX) * scaleX);
                    int top = (int) ((rects.get(i).top - translateY) * scaleY);

                    Log.e("Taggaasas", left + " ," + top + " ," + width + " ," + height + " ,");

                    FrameLayout.LayoutParams llp = new FrameLayout.LayoutParams(width, height);
                    llp.setMargins(left, top , 0, 0);
                    rect.setLayoutParams(llp);

                    screenCanvas.addView(rect);
                }
                screenCanvas.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        Log.e("Taggaasas", " & " + screenCanvas.getWidth());

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
