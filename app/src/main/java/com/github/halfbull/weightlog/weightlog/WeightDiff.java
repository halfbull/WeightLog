package com.github.halfbull.weightlog.weightlog;

import java.util.Date;

class WeightDiff {

    private final float diff;
    private final float value;
    private final Date date;

    WeightDiff(float diff, float value, Date date) {
        this.diff = diff;
        this.value = value;
        this.date = date;
    }

    float getDiff() {
        return diff;
    }

    float getValue() {
        return value;
    }

    Date getDate() {
        return date;
    }
}
