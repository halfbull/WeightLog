package com.github.halfbull.weightlog.weightlog;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.halfbull.weightlog.database.Weight;
import com.github.halfbull.weightlog.database.WeightDao;

import java.util.LinkedList;
import java.util.List;


class WeightsRecycleBinViewModel {

    private final WeightDao weightDao;

    private final List<Weight> weights = new LinkedList<>();
    private final MutableLiveData<List<Weight>> weightsLiveData = new MutableLiveData<>();

    WeightsRecycleBinViewModel(WeightDao weightDao) {
        this.weightDao = weightDao;
    }

    @NonNull
    LiveData<List<Weight>> getDeletedWeights() {
        return this.weightsLiveData;
    }

    void clear() {
        weights.clear();
    }

    void add(Weight weight) {
        weights.add(weight);
        weightsLiveData.postValue(weights);
    }

    int size() {
        return weights.size();
    }

    void restore() {
        new AsyncTask<Void, Void, Void>() {
            @Nullable
            @Override
            protected Void doInBackground(Void... voids) {
                weightDao.insertList(weights);
                clear();
                return null;
            }
        }.execute();
    }
}
