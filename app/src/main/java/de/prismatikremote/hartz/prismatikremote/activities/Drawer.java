package de.prismatikremote.hartz.prismatikremote.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import de.prismatikremote.hartz.prismatikremote.R;
import de.prismatikremote.hartz.prismatikremote.backend.Communicator;

/**
 * Created by kaiha on 09.04.2017.
 */

public class Drawer extends AppCompatActivity {
    protected ProgressDialog dialog;

    private ListView mDrawerList;
    private ArrayAdapter<String> mAdapter;

    private ActionBarDrawerToggle mDrawerToggle;
    protected DrawerLayout mDrawerLayout;
    private String mActivityTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer);

        setupDrawer();


        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);
        getSupportActionBar().setCustomView(R.layout.tab_header);

        ImageView lockView = (ImageView) findViewById(R.id.lock_image);
        int color = Color.parseColor("#FFFFFF");
        lockView.setColorFilter(color);
        setLockVisible(Communicator.getInstance().hasBlocker());
    }

    protected void setLockVisible(boolean visible) {
        ImageView lockView = (ImageView) findViewById(R.id.lock_image);
        if (visible) {
            lockView.setVisibility(View.VISIBLE);
        } else {
            lockView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    private void setupDrawer() {
        mDrawerList = (ListView)findViewById(R.id.navList);
        String[] osArray = { "Start", "Profiles","Notifications", "Widgets", "Console", "Settings"};
        mAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, osArray);
        mDrawerList.setAdapter(mAdapter);
        final Context context = this;
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch(position) {
                    case 0:
                        startActivity(new Intent(context, MainActivity.class));
                        break;
                    case 1:
                        startActivity(new Intent(context, Profiles.class));
                        break;
                    case 2:
                        startActivity(new Intent(context, Notifications.class));
                        break;
                    case 3:
                        startActivity(new Intent(context, Widgets.class));
                        break;
                    case 4:
                        startActivity(new Intent(context, Console.class));
                        break;
                    case 5:
                        startActivity(new Intent(context, Settings.class));
                        break;
                }
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Navigation");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                drawerView.bringToFront();
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    protected void load() {
        dialog = ProgressDialog.show(this, "", "Loading. Please wait..", true);
    }
}
