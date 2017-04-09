package de.prismatikremote.hartz.prismatikremote;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import de.prismatikremote.hartz.prismatikremote.backend.Communicator;
import de.prismatikremote.hartz.prismatikremote.backend.RemoteState;
import de.prismatikremote.hartz.prismatikremote.backend.commands.Communication;

public class Profiles extends Drawer implements AdapterView.OnItemClickListener, Communicator.OnCompleteListener {

    private ListView profilesListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mDrawerLayout.addView(inflater.inflate(R.layout.activity_profiles, null));

        profilesListView = (ListView) findViewById(R.id.profilesListView);
        ArrayAdapter<String> itemsAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, RemoteState.getInstance().getProfiles());
        profilesListView.setAdapter(itemsAdapter);

        int selection = RemoteState.getInstance().getProfiles().indexOf(RemoteState.getInstance().getProfile());
        profilesListView.setSelection(selection);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final String item = (String) parent.getItemAtPosition(position);
        Communicator.getInstance().setProfile(item, this);
    }

    @Override
    public void onError(String result) {

    }

    @Override
    public void onStepCompletet(Communication communication) {

    }

    @Override
    public void onSuccess() {
        int selection = RemoteState.getInstance().getProfiles().indexOf(RemoteState.getInstance().getProfile());
        profilesListView.setSelection(selection);
    }
}
