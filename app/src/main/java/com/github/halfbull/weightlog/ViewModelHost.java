package com.github.halfbull.weightlog;

import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.animation.Transformation;

import com.github.halfbull.weightlog.database.AppDatabase;
import com.github.halfbull.weightlog.database.Weight;
import com.github.halfbull.weightlog.database.WeightDao;
import com.github.halfbull.weightlog.importexport.ImportExportViewModel;
import com.github.halfbull.weightlog.statistics.GraphViewModel;
import com.github.halfbull.weightlog.weightlog.WeightLogViewModel;

import java.util.List;

public class ViewModelHost extends AndroidViewModel {

    @NonNull
    private final ImportExportViewModel importExportViewModel;
    @NonNull
    private final GraphViewModel graphViewModel;
    @NonNull
    private final WeightLogViewModel weightLogViewModel;

    private final WeightDao weightDao;

    public ViewModelHost(@NonNull Application application) {
        super(application);

        AppDatabase database = AppDatabase.build(application.getApplicationContext());
        importExportViewModel = new ImportExportViewModel(database.weightDao());
        graphViewModel = new GraphViewModel(database.weightDao());
        weightLogViewModel = new WeightLogViewModel(database.weightDao());

        weightDao = database.weightDao();
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
