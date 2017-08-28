package com.github.halfbull.weightlog;

import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.animation.Transformation;

import com.github.halfbull.weightlog.database.AppDatabase;
import com.github.halfbull.weightlog.database.Weight;
import com.github.halfbull.weightlog.database.WeightDao;
import com.github.halfbull.weightlog.statistics.GraphViewModel;
import com.github.halfbull.weightlog.weightlog.WeightLogViewModel;

import java.util.List;

public class AppViewModel extends AndroidViewModel {

    @NonNull
    private final GraphViewModel graphViewModel;
    @NonNull
    private final WeightLogViewModel weightLogViewModel;

    private WeightDao weightDao;

    private LiveData<List<Weight>> weights;

    public AppViewModel(@NonNull Application application) {
        super(application);

        AppDatabase database = AppDatabase.build(application.getApplicationContext());
        graphViewModel = new GraphViewModel(database.weightDao());
        weightLogViewModel = new WeightLogViewModel(database.weightDao());

        weightDao = database.weightDao();
        weights = database.weightDao().getAll2();
    }

    public LiveData<List<Weight>> getWeights() {
        return weights;
    }

    public WeightDao getWeightDao(){
        return weightDao;
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
