package com.github.halfbull.weightlog.weightlog;

import android.support.annotation.NonNull;

import com.github.halfbull.weightlog.database.Weight;

import java.util.List;

class WeightDiffList {

    private final List<Weight> weights;

    WeightDiffList(List<Weight> weights) {
        this.weights = weights;
    }

    @NonNull
    WeightDiff getDiff(int i) {
        Weight current = weights.get(i);
        Weight previous = getPrevious(i);

        float diff;
        if (previous == null)
            diff = 0;
        else
            diff = current.getValue() - previous.getValue();

        return new WeightDiff(diff, current.getValue(), current.getDate());
    }

    Weight getWeight(int i) {
        return weights.get(i);
    }

    int size() {
        return weights.size();
    }

    private Weight getPrevious(int i) {
        if (i + 1 < weights.size())
            return weights.get(i + 1);

        return null;
    }
}
