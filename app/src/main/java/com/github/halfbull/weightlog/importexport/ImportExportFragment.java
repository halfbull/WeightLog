package com.github.halfbull.weightlog.importexport;

import android.arch.lifecycle.LifecycleFragment;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.halfbull.weightlog.ViewModelHost;
import com.github.halfbull.weightlog.R;

import java.io.File;

public class ImportExportFragment extends LifecycleFragment implements View.OnClickListener {

    private static final int PERMISSIONS_REQUEST_IMPORT_TASK = 0;
    private static final int PERMISSIONS_REQUEST_EXPORT_TASK = 1;

    private ImportExportViewModel model;

    private EditText location;
    private Button importButton;
    private Button exportButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = ViewModelProviders.of(getActivity()).get(ViewModelHost.class).getImportExportModel();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_import_export, container, false);

        location = v.findViewById(R.id.location_et);

        importButton = v.findViewById(R.id.importButton);
        importButton.setOnClickListener(this);

        exportButton = v.findViewById(R.id.exportButton);
        exportButton.setOnClickListener(this);

        initializeUI();

        return v;
    }

    private void initializeUI() {
        String state = Environment.getExternalStorageState();
        switch (state) {
            case Environment.MEDIA_MOUNTED:
                File file = new File(Environment.getExternalStorageDirectory(), "weight_log.csv");
                location.setText(file.getPath());
                break;

            case Environment.MEDIA_UNMOUNTED:
                location.setError(getResources().getString(R.string.import_export_external_storage_unmounted));
                location.setEnabled(false);
                importButton.setEnabled(false);
                exportButton.setEnabled(false);
                break;
        }
    }

    @Override
    public void onClick(@NonNull View view) {
        switch (view.getId()) {
            case R.id.importButton:
                if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                    importLog();
                else
                    requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_IMPORT_TASK);
                break;

            case R.id.exportButton:
                if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                    exportLog();
                else
                    requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_EXPORT_TASK);
                break;
        }
    }

    private void importLog() {
        final String filePath = location.getText().toString();
        model.importLog(new File(filePath)).observe(this, new Observer<ImportExportViewModel.Result>() {
            @Override
            public void onChanged(@Nullable ImportExportViewModel.Result result) {
                if (result != null) {
                    String toastText = "";
                    switch (result.getResult()) {
                        case ImportExportViewModel.Result.SUCCESS:
                            toastText = getResources().getQuantityString(R.plurals.import_export_imported, result.getRecordsProcessed(), result.getRecordsProcessed());
                            break;

                        case ImportExportViewModel.Result.FILE_NOT_FOUND_EXCEPTION:
                            toastText = getResources().getString(R.string.import_export_file_not_found_exception);
                            break;

                        case ImportExportViewModel.Result.IO_EXCEPTION:
                            toastText = getResources().getString(R.string.import_export_read_io_exception);
                            break;
                    }

                    Toast t = Toast.makeText(ImportExportFragment.this.getActivity(), toastText, Toast.LENGTH_SHORT);
                    t.setGravity(Gravity.BOTTOM, 0, 10);
                    t.show();
                }
            }
        });
    }

    private void exportLog() {
        String filePath = location.getText().toString();
        model.exportLog(new File(filePath)).observe(this, new Observer<ImportExportViewModel.Result>() {
            @Override
            public void onChanged(@Nullable ImportExportViewModel.Result result) {
                if (result != null) {
                    String toastText = "";
                    switch (result.getResult()) {
                        case ImportExportViewModel.Result.SUCCESS:
                            toastText = getResources().getQuantityString(R.plurals.import_export_exported, result.getRecordsProcessed(), result.getRecordsProcessed());
                            break;

                        case ImportExportViewModel.Result.IO_EXCEPTION:
                            toastText = getResources().getString(R.string.import_export_write_io_exception);
                            break;
                    }

                    Toast t = Toast.makeText(ImportExportFragment.this.getActivity(), toastText, Toast.LENGTH_SHORT);
                    t.setGravity(Gravity.BOTTOM, 0, 10);
                    t.show();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults[0] != PackageManager.PERMISSION_GRANTED)
            return;

        switch (requestCode) {
            case PERMISSIONS_REQUEST_IMPORT_TASK:
                importLog();
                break;

            case PERMISSIONS_REQUEST_EXPORT_TASK:
                exportLog();
                break;
        }
    }
}
