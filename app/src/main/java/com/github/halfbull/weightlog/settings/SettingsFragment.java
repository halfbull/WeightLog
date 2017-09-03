package com.github.halfbull.weightlog.settings;

import android.content.pm.PackageManager;
import android.os.Bundle;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.util.Log;

import com.github.halfbull.weightlog.R;
import com.github.pierry.simpletoast.SimpleToast;

public class SettingsFragment extends PreferenceFragmentCompat {

    private static final int PERMISSIONS_REQUEST_NONE = 0;
    private static final int PERMISSIONS_REQUEST_IMPORT_TASK = 1;
    private static final int PERMISSIONS_REQUEST_EXPORT_TASK = 2;

    private static final String PREF_KEY_IMPORT = "preference_key_import";
    private static final String PREF_KEY_EXPORT = "preference_key_export";

    private int grantedPermission;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.settings);

        if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            findPreference(PREF_KEY_IMPORT).setEnabled(false);
            findPreference(PREF_KEY_EXPORT).setEnabled(false);
        }
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {

        switch (preference.getKey()) {
            case "preference_key_import" :
                onImportLogSelected();
                break;
            case "preference_key_export" :
                onExportLogSelected();
                break;
        }

        return super.onPreferenceTreeClick(preference);
    }

    private void onImportLogSelected() {
        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            importLog();
        else
            requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_IMPORT_TASK);
    }

    private void importLog() {
        ImportDialog dialog = new ImportDialog();
        dialog.show(getFragmentManager(), "IMPORT_DIALOG");
        //SimpleToast.ok(getActivity(), "YEAH!");
    }

    private void onExportLogSelected() {
        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            exportLog();
        else
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_EXPORT_TASK);
    }

    private void exportLog() {
        ExportDialog dialog = new ExportDialog();
        dialog.show(getFragmentManager(), "EXPORT_DIALOG");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults[0] != PackageManager.PERMISSION_GRANTED)
            return;

        grantedPermission = requestCode;
    }

    @Override
    public void onResume() {
        super.onResume();

        switch (grantedPermission) {
            case PERMISSIONS_REQUEST_IMPORT_TASK :
                importLog();
                break;

            case PERMISSIONS_REQUEST_EXPORT_TASK :
                exportLog();
                break;
        }

        grantedPermission = PERMISSIONS_REQUEST_NONE;
    }
}
