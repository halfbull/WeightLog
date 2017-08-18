package com.github.halfbull.weightlog;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.github.halfbull.weightlog.database.AppDatabase;
import com.github.halfbull.weightlog.importexport.ImportExportViewModel;
import com.github.halfbull.weightlog.statistics.GraphViewModel;
import com.github.halfbull.weightlog.weightlog.WeightLogViewModel;

public class ViewModelHost extends AndroidViewModel {

    @NonNull
    private final ImportExportViewModel importExportViewModel;
    @NonNull
    private final GraphViewModel graphViewModel;
    @NonNull
    private final WeightLogViewModel weightLogViewModel;

    public ViewModelHost(@NonNull Application application) {
        super(application);

        AppDatabase database = AppDatabase.build(application.getApplicationContext());
        importExportViewModel = new ImportExportViewModel(database.weightDao());
        graphViewModel = new GraphViewModel(database.weightDao());
        weightLogViewModel = new WeightLogViewModel(database.weightDao());
    }

    @NonNull
    public ImportExportViewModel getImportExportModel() {
        return importExportViewModel;
    }

    @NonNull
    public GraphViewModel getGraphModel() {
        return graphViewModel;
    }

    @NonNull
    public WeightLogViewModel getWeightLogModel() {
        return weightLogViewModel;
    }
}
