package de.prismatikremote.hartz.prismatikremote;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import de.prismatikremote.hartz.prismatikremote.backend.Communicator;
import de.prismatikremote.hartz.prismatikremote.backend.RemoteState;
import de.prismatikremote.hartz.prismatikremote.backend.commands.Communication;

public class Profiles extends AppCompatActivity implements AdapterView.OnItemClickListener, Communicator.OnCompleteListener {

    private ListView profilesListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profiles);

        profilesListView = (ListView) findViewById(R.id.profilesListView);
        ArrayAdapter<String> itemsAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, RemoteState.getInstance().getProfiles());
        profilesListView.setAdapter(itemsAdapter);
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

    }
}
