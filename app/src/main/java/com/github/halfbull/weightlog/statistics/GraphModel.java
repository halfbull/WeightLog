package com.github.halfbull.weightlog.statistics;

import android.support.annotation.NonNull;

import com.github.halfbull.weightlog.database.Weight;
import com.github.halfbull.weightlog.database.WeightDao;

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
    List<GraphPoint> getGraph() {
        List<Weight> weights = weightDao.getAll();

        List<GraphPoint> graphPoints = new LinkedList<>();
        for (Weight w : weights) {
            float floatMonth = floatDateConverter.dateToFloatMonth(w.getDate());
            graphPoints.add(new GraphPoint(floatMonth, w.getValue()));
        }

        return graphPoints;
    }
}
