package com.github.halfbull.weightlog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.github.halfbull.weightlog.importexport.ImportExportFragment;
import com.github.halfbull.weightlog.statistics.StatisticsFragment;
import com.github.halfbull.weightlog.weightlog.WeightLogFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle actionBarToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.open_nav_drawer, R.string.close_nav_drawer) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                closeSoftKeyboard();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                closeSoftKeyboard();
            }
        };
        mDrawerLayout.addDrawerListener(actionBarToggle);
        actionBarToggle.syncState();

        NavigationView navView = (NavigationView) findViewById(R.id.navigation_view);
        navView.setNavigationItemSelectedListener(this);

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frame_layout);
        if (fragment == null) {
            MenuItem item = navView.getMenu().getItem(0);
            setFragment(item, new WeightLogFragment());
            item.setChecked(true);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frame_layout);

        switch (itemId) {
            case R.id.log_menu_item:
                if (!(fragment instanceof WeightLogFragment))
                    setFragment(item, new WeightLogFragment());
                break;

            case R.id.import_export_menu_item:
                if (!(fragment instanceof ImportExportFragment))
                    setFragment(item, new ImportExportFragment());
                break;

            case R.id.stats_menu_item:
                if (!(fragment instanceof StatisticsFragment))
                    setFragment(item, new StatisticsFragment());
                break;
        }

        mDrawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    private void setFragment(@NonNull MenuItem item, Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .commit();

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(item.getTitle());
    }

    private void closeSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View focusedView = getCurrentFocus();
        if (focusedView != null) {
            inputMethodManager.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
        }
    }
}
