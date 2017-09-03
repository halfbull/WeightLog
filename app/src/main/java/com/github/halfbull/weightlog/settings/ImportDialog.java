package com.github.halfbull.weightlog.settings;

import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.widget.EditText;

import com.github.halfbull.weightlog.AppViewModel;
import com.github.halfbull.weightlog.R;
import com.github.halfbull.weightlog.database.Weight;
import com.github.pierry.simpletoast.SimpleToast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;

public class ImportDialog extends DialogFragment {

    private final CsvConverter csvConverter = new CsvConverter();
    private AppViewModel model;
    private EditText locationEditText;
    Context context;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = ViewModelProviders.of(getActivity()).get(AppViewModel.class);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_location, null);
        locationEditText = v.findViewById(R.id.location_edit_text);

        context = getActivity();

        setFileLocation();

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.import_dialog_title)
                .setView(v)
                .setPositiveButton(R.string.import_dialog_ok_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                importLog();
                            }
                        }).start();
                    }
                })
                .setNegativeButton(R.string.import_dialog_cancel_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create();
    }

    private void setFileLocation() {
        File file = new File(Environment.getExternalStorageDirectory(), "weight_log.csv");
        locationEditText.setText(file.getPath());
    }

    private void importLog() {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context);
        notificationBuilder
                .setContentTitle(context.getResources().getString(R.string.import_notification_title))
                .setSmallIcon(R.drawable.ic_csv)
                .setProgress(0, 0, true);
        notificationManager.notify(0, notificationBuilder.build());

        String notificationText;
        try (BufferedReader reader = new BufferedReader(new FileReader(locationEditText.getText().toString()))) {
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

            int importedRecords = model.getWeightDao().size() - beforeCount;
            notificationText = context.getResources().getQuantityString(R.plurals.import_notification_imported, importedRecords, importedRecords);
        } catch (FileNotFoundException ex) {
            notificationText = context.getResources().getString(R.string.import_notification_file_not_found_exception);
        } catch (IOException ex) {
            notificationText = context.getResources().getString(R.string.import_notification_io_exception);
        }

        notificationBuilder.setContentText(notificationText);
        notificationBuilder.setProgress(0, 0, false);
        notificationManager.notify(0, notificationBuilder.build());
    }
}
