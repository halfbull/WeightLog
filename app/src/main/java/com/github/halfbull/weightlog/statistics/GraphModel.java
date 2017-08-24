package com.github.halfbull.weightlog.statistics;

import android.support.annotation.NonNull;
import android.util.Log;
import android.util.TimingLogger;

import com.github.halfbull.weightlog.database.Weight;
import com.github.halfbull.weightlog.database.WeightDao;
import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

class GraphModel {

    private final WeightDao weightDao;
    private final FloatDateConverter floatDateConverter;

    GraphModel(WeightDao weightDao, FloatDateConverter floatDateConverter) {
        this.weightDao = weightDao;
        this.floatDateConverter = floatDateConverter;
    }

    @NonNull
    List<Entry> getGraph() {
        List<Weight> weights = weightDao.getAll();

        List<Entry> graphPoints = new ArrayList<>(weights.size());
        for (Weight w : weights) {
            float floatMonth = floatDateConverter.dateToFloatMonth(w.getDate());
            graphPoints.add(new Entry(floatMonth, w.getValue()));
        }

        return graphPoints;
    }
}
