package com.github.halfbull.weightlog.settings;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.widget.Toast;

import com.github.halfbull.weightlog.AppViewModel;
import com.github.halfbull.weightlog.R;
import com.github.halfbull.weightlog.database.Weight;
import com.github.pierry.simpletoast.SimpleToast;

import net.bytebuddy.agent.builder.AgentBuilder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;

public class SettingsFragment extends PreferenceFragmentCompat {

    private static final int PERMISSIONS_REQUEST_NONE = 0;
    private static final int PERMISSIONS_REQUEST_IMPORT_TASK = 1;
    private static final int PERMISSIONS_REQUEST_EXPORT_TASK = 2;

    private static final String PREF_KEY_IMPORT = "preference_key_import";
    private static final String PREF_KEY_EXPORT = "preference_key_export";

    private int grantedPermission;
    private boolean fragmentAlive = true;

    CsvConverter csvConverter = new CsvConverter();
    AppViewModel model;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = ViewModelProviders.of(getActivity()).get(AppViewModel.class);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.settings);
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {

        switch (preference.getKey()) {
            case "preference_key_import":
                onImportLogSelected();
                break;
            case "preference_key_export":
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
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            SimpleToast.error(getActivity(), getActivity().getResources().getString(R.string.import_export_external_storage_unmounted));
            return;
        }

        final Preference preference = findPreference(PREF_KEY_IMPORT);
        final Activity activity = getActivity();

        new AsyncTask<Void, Void, Runnable>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                preference.setEnabled(false);
                preference.setSummary(R.string.preference_import_progress);
            }

            @Override
            protected Runnable doInBackground(Void... voids) {
                File csvFile = getCsvFile();
                try (BufferedReader reader = new BufferedReader(new FileReader(csvFile))) {
                    List<Weight> weights = new LinkedList<>();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        try {
                            weights.add(csvConverter.deserialize(line));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }

                    int beforeCount = model.getWeightDao().size();
                    model.getWeightDao().insertList(weights);

                    final int importedRecords = model.getWeightDao().size() - beforeCount;

                    return new Runnable() {
                        @Override
                        public void run() {
                            String toastText = activity.getResources().getQuantityString(R.plurals.import_notification_imported, importedRecords, importedRecords);
                            SimpleToast.info(activity, toastText);
                        }
                    };
                } catch (FileNotFoundException ex) {
                    return new Runnable() {
                        @Override
                        public void run() {
                            String toastText = activity.getString(R.string.import_notification_file_not_found_exception);
                            SimpleToast.error(activity, toastText);
                        }
                    };
                } catch (IOException ex) {

                    return new Runnable() {
                        @Override
                        public void run() {
                            String toastText = activity.getResources().getString(R.string.import_notification_io_exception);
                            SimpleToast.error(activity, toastText);
                        }
                    };
                }
            }

            @Override
            protected void onPostExecute(Runnable runnable) {
                super.onPostExecute(runnable);
                if (fragmentAlive) {
                    preference.setSummary(R.string.preference_import_summary);
                    preference.setEnabled(true);
                }
                runnable.run();
            }
        }.execute();
    }

    private void onExportLogSelected() {
        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            exportLog();
        else
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_EXPORT_TASK);
    }

    private void exportLog() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            SimpleToast.error(getActivity(), getActivity().getResources().getString(R.string.import_export_external_storage_unmounted));
            return;
        }

        final Preference preference = findPreference(PREF_KEY_EXPORT);
        final Activity activity = getActivity();

        new AsyncTask<Void, Void, Runnable>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                preference.setEnabled(false);
                preference.setSummary(R.string.preference_export_progress);
            }

            @Override
            protected Runnable doInBackground(Void... voids) {
                File csvFile = getCsvFile();
                try(BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile))){
                    List<Weight> weights = model.getWeightDao().getAll();
                    for(Weight weight : weights) {
                        csvConverter.serialize(writer, weight);
                    }
                    final int exportedRecords = weights.size();

                    return new Runnable() {
                        @Override
                        public void run() {
                            String toastText = activity.getResources().getQuantityString(R.plurals.export_notification_exported, exportedRecords, exportedRecords);
                            SimpleToast.info(activity, toastText);
                        }
                    };
                } catch (IOException e) {
                    return new Runnable() {
                        @Override
                        public void run() {
                            String toastText = activity.getResources().getString(R.string.export_notification_io_exception);
                            SimpleToast.error(activity, toastText);
                        }
                    };
                }
            }

            @Override
            protected void onPostExecute(Runnable runnable) {
                super.onPostExecute(runnable);
                if (fragmentAlive) {
                    preference.setSummary(R.string.preference_export_summary);
                    preference.setEnabled(true);
                }
                runnable.run();
            }
        }.execute();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults[0] != PackageManager.PERMISSION_GRANTED)
            return;

        grantedPermission = requestCode;
    }

    private File getCsvFile() {
        return new File(Environment.getExternalStorageDirectory(), "weight_log.csv");
    }

    @Override
    public void onResume() {
        super.onResume();

        switch (grantedPermission) {
            case PERMISSIONS_REQUEST_IMPORT_TASK:
                importLog();
                break;

            case PERMISSIONS_REQUEST_EXPORT_TASK:
                exportLog();
                break;
        }

        grantedPermission = PERMISSIONS_REQUEST_NONE;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        fragmentAlive = false;
    }
}
