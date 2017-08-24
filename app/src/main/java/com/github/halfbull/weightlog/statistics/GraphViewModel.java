package com.github.halfbull.weightlog.statistics;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.halfbull.weightlog.database.WeightDao;
import com.github.mikephil.charting.data.Entry;

import java.util.List;

public class GraphViewModel {

    @NonNull
    private final GraphModel graphModel;

    public GraphViewModel(WeightDao weightDao) {
        this.graphModel = new GraphModel(weightDao, new FloatDateConverter());
    }

    @NonNull
    LiveData<List<Entry>> getGraph() {
        final MutableLiveData<List<Entry>> liveData = new MutableLiveData<>();

        new AsyncTask<Void, Void, Void>() {
            @Nullable
            @Override
            protected Void doInBackground(Void... voids) {
                liveData.postValue(graphModel.getGraph());
                return null;
            }
        }.execute();

        return liveData;
    }
}
