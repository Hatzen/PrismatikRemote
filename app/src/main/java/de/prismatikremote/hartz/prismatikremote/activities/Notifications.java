package de.prismatikremote.hartz.prismatikremote.activities;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jrummyapps.android.colorpicker.ColorPanelView;
import com.jrummyapps.android.colorpicker.ColorPickerDialog;
import com.jrummyapps.android.colorpicker.ColorPickerDialogListener;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.prismatikremote.hartz.prismatikremote.R;
import de.prismatikremote.hartz.prismatikremote.backend.Communicator;
import de.prismatikremote.hartz.prismatikremote.backend.commands.Communication;
import de.prismatikremote.hartz.prismatikremote.helper.UiHelper;
import de.prismatikremote.hartz.prismatikremote.model.ColorObject;
import de.prismatikremote.hartz.prismatikremote.services.NotificationService;

// TODO: Tidy up.
public class Notifications extends Drawer implements Communicator.OnCompleteListener, View.OnClickListener, ColorPickerDialogListener {
    public static final String NOTIFICATION_DATA_FILENAME = "NOTIFICATION_DATA_PRISMATIK";

    // TODO: Rename colors to something more describing.
    private HashMap<String,ColorObject> colors;
    private PackageManager packageManager = null;
    private List<ApplicationInfo> appList = null;
    private ApplicationAdapter listAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        mDrawerLayout.addView(inflater.inflate(R.layout.activity_notifications, null));

        Button notificationDummy = (Button) findViewById(R.id.notification_dummy);
        notificationDummy.setOnClickListener(this);

        final Activity activity = this;
        final ListView appListView = (ListView) findViewById(R.id.app_list);
        appListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int color = colors.get(appList.get(position).packageName).color;
                ColorPickerDialog.newBuilder()
                        .setDialogType(ColorPickerDialog.TYPE_CUSTOM)
                        .setAllowPresets(false)
                        .setColor(color)
                        .setDialogId(position)
                        .show(activity);
            }
        });

        packageManager = getPackageManager();
        colors = loadSerializedColors(this);

        new LoadApplications().execute();
    }

    @Override
    public void onResume() {
        super.onResume();
        checkNotificationAccess();

    }

    private void checkNotificationAccess() {
        ComponentName cn = new ComponentName(this, NotificationService.class);
        String flat = android.provider.Settings.Secure.getString(this.getContentResolver(), "enabled_notification_listeners");
        final boolean enabled = flat != null && flat.contains(cn.flattenToString());

        if (!enabled) {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
                            startActivity(intent);
                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            finish();
                            break;
                    }
                }
            };
            UiHelper.showYesNoAlert(this, "Notification access not granted yet. Do you want to?", dialogClickListener);
        }
    }

    @Override
    public void onClick(View view) {
        // TODO: Remove creation of dummy events.
        if( view == findViewById(R.id.notification_dummy)) {
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle("My notification")
                            .setContentText("Hello World!");
            // Creates an explicit intent for an Activity in your app
            Intent resultIntent = new Intent(this, Notifications.class);

            // The stack builder object will contain an artificial back stack for the
            // started Activity.
            // This ensures that navigating backward from the Activity leads out of
            // your application to the Home screen.
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            // Adds the back stack for the Intent (but not the Intent itself)
            stackBuilder.addParentStack(Notifications.class);
            // Adds the Intent that starts the Activity to the top of the stack
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            0,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            mBuilder.setContentIntent(resultPendingIntent);
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            // mId allows you to update the notification later on.
            mNotificationManager.notify(1241, mBuilder.build());
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

    private List<ApplicationInfo> checkForLaunchIntent(List<ApplicationInfo> list) {
        ArrayList<ApplicationInfo> applist = new ArrayList<ApplicationInfo>();
        for (ApplicationInfo info : list) {
            try {
                if (null != packageManager.getLaunchIntentForPackage(info.packageName)) {
                    applist.add(info);
                    if(colors.get(info.packageName) == null) {
                        ColorObject color = new ColorObject();
                        color.packageName = info.packageName;
                        Bitmap icon = ((BitmapDrawable)info.loadIcon(packageManager)).getBitmap();
                        int[] avgColor = UiHelper.getAverageColorRGB(icon);
                        color.color = UiHelper.toColorInt(avgColor);
                        colors.put(info.packageName, color);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return applist;
    }

    @Override
    public void onColorSelected(int dialogId, @ColorInt int color) {
        // Dialogid = position.
        colors.get(appList.get(dialogId).packageName).color = color;
        listAdapter.notifyDataSetChanged();
        saveColors();
    }

    @Override
    public void onDialogDismissed(int dialogId) {

    }

    public void saveColors() {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(openFileOutput(NOTIFICATION_DATA_FILENAME, Context.MODE_PRIVATE));

            ArrayList<ColorObject> valueList = new ArrayList(colors.values());
            oos.writeObject(valueList);

            oos.flush();
            oos.close();
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    public static HashMap<String, ColorObject> loadSerializedColors(Context context) {
        HashMap<String,ColorObject> colors = new HashMap<>();
        try {
            ObjectInputStream ois = new ObjectInputStream(context.openFileInput(NOTIFICATION_DATA_FILENAME));
            Object o = ois.readObject();
            ArrayList<ColorObject> valueList = (ArrayList<ColorObject>) o;
            for(ColorObject colorObject: valueList) {
                colors.put(colorObject.packageName, colorObject);
            }
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
        return colors;
    }

    /**
     * Adapter class for the list view.
     */
    private class ApplicationAdapter extends ArrayAdapter<ApplicationInfo> {
        private List<ApplicationInfo> appsList = null;
        private Context context;
        private PackageManager packageManager;

        public ApplicationAdapter(Context context, int textViewResourceId,
                                  List<ApplicationInfo> appsList) {
            super(context, textViewResourceId, appsList);
            this.context = context;
            this.appsList = appsList;
            packageManager = context.getPackageManager();
        }

        @Override
        public int getCount() {
            return ((null != appsList) ? appsList.size() : 0);
        }

        @Override
        public ApplicationInfo getItem(int position) {
            return ((null != appsList) ? appsList.get(position) : null);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (null == view) {
                LayoutInflater layoutInflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = layoutInflater.inflate(R.layout.snippet_list_row, null);
            }

            ApplicationInfo applicationInfo = appsList.get(position);
            if (null != applicationInfo) {
                TextView appName = (TextView) view.findViewById(R.id.app_name);
                TextView packageName = (TextView) view.findViewById(R.id.app_paackage);
                ImageView iconview = (ImageView) view.findViewById(R.id.app_icon);
                ColorPanelView lightColor = (ColorPanelView) view.findViewById(R.id.light_color);
                CheckBox regardCheckbox = (CheckBox) view.findViewById(R.id.regard_checkbox);
                regardCheckbox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        colors.get(appList.get(position).packageName).regard = ((CheckBox) view).isChecked();
                        saveColors();
                    }
                });

                appName.setText(applicationInfo.loadLabel(packageManager));
                packageName.setText(applicationInfo.packageName);
                iconview.setImageDrawable(applicationInfo.loadIcon(packageManager));

                regardCheckbox.setChecked(colors.get(applicationInfo.packageName).regard);
                lightColor.setColor(colors.get(applicationInfo.packageName).color);
            }
            return view;
        }
    }

    /**
     * Class that loads all applications data.
     */
    private class LoadApplications extends AsyncTask<Void, Void, Void> {
        private ProgressDialog progress = null;

        @Override
        protected Void doInBackground(Void... params) {
            appList = checkForLaunchIntent(packageManager.getInstalledApplications(PackageManager.GET_META_DATA));
            listAdapter = new ApplicationAdapter(Notifications.this,
                    R.layout.snippet_list_row, appList);

            return null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPostExecute(Void result) {
            progress.dismiss();
            super.onPostExecute(result);

            ListView list = (ListView) findViewById(R.id.app_list);
            list.setAdapter(listAdapter);
        }

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(Notifications.this, null,
                    "Loading application info...");
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }

}