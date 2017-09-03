package com.github.halfbull.weightlog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.github.halfbull.weightlog.settings.SettingsFragment;
import com.github.halfbull.weightlog.statistics.StatisticsFragment;
import com.github.halfbull.weightlog.weightlog.WeightLogFragment;
import com.mikepenz.iconics.typeface.IIcon;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

public class Main2Activity extends AppCompatActivity implements Drawer.OnDrawerListener, Drawer.OnDrawerItemClickListener {

    private Drawer drawer;
    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main2);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = new DrawerBuilder(this)
                .withActivity(this)
                .withHeader(R.layout.drawer_header)
                .withToolbar(toolbar)
                .withSavedInstance(savedInstanceState)
                .withActionBarDrawerToggleAnimated(true)
                .inflateMenu(R.menu.drawer_menu)
                .withOnDrawerItemClickListener(this)
                .withOnDrawerListener(this)
                .build();

        drawer.setSelection(R.id.log_menu_item);
    }

    @Override
    public void onDrawerOpened(View drawerView) {
        closeSoftKeyboard();
    }

    @Override
    public void onDrawerClosed(View drawerView) {
        closeSoftKeyboard();
    }

    @Override
    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
        int itemId = (int) drawerItem.getIdentifier();
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_layout);

        switch (itemId) {
            case R.id.log_menu_item:
                if (!(currentFragment instanceof WeightLogFragment))
                    setFragment(R.string.nav_drawer_menu_log, new WeightLogFragment());
                break;

            case R.id.stats_menu_item:
                if (!(currentFragment instanceof StatisticsFragment))
                    setFragment(R.string.nav_drawer_menu_stats, new StatisticsFragment());
                break;

            case R.id.settings_menu_item:
                if (!(currentFragment instanceof SettingsFragment))
                    setFragment(R.string.nav_drawer_menu_settings, new SettingsFragment());
                break;
        }

        drawer.closeDrawer();

        return true;
    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(drawer.saveInstanceState(outState));
    }

    private void setFragment(@android.support.annotation.StringRes int title, Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame, fragment)
                .commit();

        toolbar.setTitle(title);
    }

    private void closeSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View focusedView = getCurrentFocus();
        if (focusedView != null) {
            inputMethodManager.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
        }
    }
}
