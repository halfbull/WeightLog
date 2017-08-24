package com.github.halfbull.weightlog.weightlog;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
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
    private final MutableLiveData<Boolean> weightsChanged = new MutableLiveData<>();

    WeightsRecycleBinViewModel(WeightDao weightDao) {
        this.weightDao = weightDao;
    }

    @NonNull
    LiveData<Boolean> hasRecycledItems() {
        return Transformations.map(weightsChanged, new Function<Boolean, Boolean>() {
            @Override
            public Boolean apply(Boolean input) {
                return weights.size() > 0;
            }
        });
    }

    void clear() {
        weights.clear();
    }

    void add(Weight weight) {
        weights.add(weight);
        weightsChanged.postValue(null);
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
