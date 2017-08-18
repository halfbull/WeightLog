package com.github.halfbull.weightlog.weightlog;

import com.github.halfbull.weightlog.database.Weight;

class DeleteWeightCommand {

    private final WeightDiffList weightDiffs;
    private final WeightLogAdapter adapter;
    private final WeightLogViewModel model;
    private final int position;

    private Weight weight;
    private boolean cancelled;

    DeleteWeightCommand(WeightDiffList weightDiffs, WeightLogAdapter adapter, WeightLogViewModel model, int position) {
        this.weightDiffs = weightDiffs;
        this.adapter = adapter;
        this.model = model;
        this.position = position;
    }

    boolean isNotCancelled() {
        return !cancelled;
    }

    void execute() {
        weight = weightDiffs.getWeight(position);
        weightDiffs.remove(position);
        adapter.notifyDataSetChanged();
    }

    void commit() {
        model.delWeight(weight);
    }

    void cancel() {
        weightDiffs.insert(position, weight);
        adapter.notifyDataSetChanged();
        cancelled = true;
    }
}
