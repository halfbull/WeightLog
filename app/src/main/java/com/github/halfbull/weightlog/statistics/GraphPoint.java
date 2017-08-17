package com.github.halfbull.weightlog.statistics;

class GraphPoint {

    private final float floatMonth;
    private final float value;

    GraphPoint(float month, float value) {
        this.floatMonth = month;
        this.value = value;
    }

    float getFloatMonth() {
        return floatMonth;
    }

    public float getValue() {
        return value;
    }
}
