package com.github.halfbull.weightlog.settings;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.widget.EditText;

import com.github.halfbull.weightlog.AppViewModel;
import com.github.halfbull.weightlog.R;
import com.github.halfbull.weightlog.database.Weight;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ExportDialog extends DialogFragment {

    private final CsvConverter csvConverter = new CsvConverter();
    private AppViewModel model;
    private EditText locationEditText;
    private Activity activity;

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
        activity = getActivity();

        setFileLocation();

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.export_dialog_title)
                .setView(v)
                .setPositiveButton(R.string.export_dialog_ok_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                exportLog();
                            }
                        }).start();
                    }
                })
                .setNegativeButton(R.string.export_dialog_cancel_button, new DialogInterface.OnClickListener() {
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

    private void exportLog() {
        NotificationManager notificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(activity);
        notificationBuilder
                .setContentTitle(activity.getResources().getString(R.string.export_notification_title))
                .setSmallIcon(R.drawable.ic_csv)
                .setProgress(0, 0, true);
        notificationManager.notify(0, notificationBuilder.build());

        String notificationText;
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(locationEditText.getText().toString()))){
            List<Weight> weights = model.getWeightDao().getAll();
            for(Weight weight : weights) {
                csvConverter.serialize(writer, weight);
            }
            int exportedRecords = weights.size();
            notificationText = activity.getResources().getQuantityString(R.plurals.export_notification_exported, exportedRecords, exportedRecords);
        } catch (IOException e) {
            notificationText = activity.getResources().getString(R.string.export_notification_io_exception);
        }

        notificationBuilder.setContentText(notificationText);
        notificationBuilder.setProgress(0, 0, false);
        notificationManager.notify(0, notificationBuilder.build());
    }
}
