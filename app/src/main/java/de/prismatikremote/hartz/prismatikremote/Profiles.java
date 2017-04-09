package de.prismatikremote.hartz.prismatikremote;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
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
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, RemoteState.getInstance().getProfiles()) {
                    public View getView(int position, View convertView, ViewGroup parent) {
                        super.getView(position, convertView, parent);
                        if(convertView!=null){
                            //ImageView img = (ImageView)convertView.findViewById(R.id.imageView1);
                            if(profilesListView.isItemChecked(position)){
                                convertView.setBackgroundColor(Color.GRAY);// here you can set any color.
                                //img.setImageResource(R.drawable.img1);//img1 is stored in your rawable folder.
                            }else{
                                convertView.setBackgroundColor(0);
                                //img.setImageResource(R.drawable.img2);
                            }
                        }
                        return super.getView(position, convertView, parent);
                    }
                };
        profilesListView.setAdapter(itemsAdapter);
        profilesListView.setOnItemClickListener(this);

        setSelection();
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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setSelection();
            }
        });
    }

    private void setSelection() {
        // TODO: Get selected working.
        ((BaseAdapter) profilesListView.getAdapter()).notifyDataSetChanged();
        //profilesListView.requestFocusFromTouch();
        int selection = RemoteState.getInstance().getProfiles().indexOf(RemoteState.getInstance().getProfile());
        Log.d("Test124124", "selection" + selection);
        profilesListView.setSelection(selection);
    }
}
