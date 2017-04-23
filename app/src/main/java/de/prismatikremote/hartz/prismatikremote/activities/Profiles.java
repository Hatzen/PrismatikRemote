package de.prismatikremote.hartz.prismatikremote.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import de.prismatikremote.hartz.prismatikremote.R;
import de.prismatikremote.hartz.prismatikremote.backend.Communicator;
import de.prismatikremote.hartz.prismatikremote.backend.RemoteState;
import de.prismatikremote.hartz.prismatikremote.backend.commands.Communication;
import de.prismatikremote.hartz.prismatikremote.helper.Helper;

public class Profiles extends Drawer implements AdapterView.OnItemClickListener, Communicator.OnCompleteListener {

    private ListView profilesListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        mDrawerLayout.addView(inflater.inflate(R.layout.activity_profiles, null));

        profilesListView = (ListView) findViewById(R.id.profilesListView);
        ArrayAdapter<String> itemsAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, RemoteState.getInstance().getProfiles()) {
                    public View getView(int position, View convertView, ViewGroup parent) {
                        super.getView(position, convertView, parent);
                        if (convertView == null) {
                            convertView = super.getView(position, convertView, parent);
                        }
                        if ( RemoteState.getInstance().getProfiles().indexOf(RemoteState.getInstance().getProfile()) == position) {
                            convertView.setBackgroundColor(Color.LTGRAY);
                        }else{
                            convertView.setBackgroundColor(0);;
                        }

                        return convertView;
                    }
                };
        profilesListView.setAdapter(itemsAdapter);
        profilesListView.setOnItemClickListener(this);

        setSelection();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        load();
        final String item = (String) parent.getItemAtPosition(position);
        Helper.getCommunicator(this).setProfile(item, this);
    }

    @Override
    public void onError(String result) {
        dialog.dismiss();
    }

    @Override
    public void onStepCompleted(Communication communication) {
        // TODO: Refresh listview, maybe profile is added.
    }

    @Override
    public void onSuccess() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setSelection();
                dialog.dismiss();
            }
        });
    }

    private void setSelection() {
        profilesListView.invalidateViews();
    }
}
