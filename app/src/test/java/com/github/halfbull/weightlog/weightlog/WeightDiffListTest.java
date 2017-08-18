package com.github.halfbull.weightlog.weightlog;

import android.support.annotation.NonNull;

import com.github.halfbull.weightlog.database.Weight;

import org.junit.Test;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;


public class WeightDiffListTest {
    @Test
    public void getDiff() throws Exception {
        List<Weight> weights = new LinkedList<>();

        weights.add(builder().day(4).value(1).weight());
        weights.add(builder().day(3).value(1).weight());
        weights.add(builder().day(2).value(2).weight());
        weights.add(builder().day(1).value(1).weight());

        WeightDiffList model = new WeightDiffList(weights);

        List<WeightDiff> expected = new LinkedList<>();
        expected.add(builder().day(4).value(1).diff(0).weightDiff());
        expected.add(builder().day(3).value(1).diff(-1).weightDiff());
        expected.add(builder().day(2).value(2).diff(1).weightDiff());
        expected.add(builder().day(1).value(1).diff(0).weightDiff());

        List<WeightDiff> actual = new LinkedList<>();
        actual.add(model.getDiff(0));
        actual.add(model.getDiff(1));
        actual.add(model.getDiff(2));
        actual.add(model.getDiff(3));

        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i).getDate(), actual.get(i).getDate());
            assertEquals(expected.get(i).getValue(), actual.get(i).getValue(), 0);
            assertEquals(expected.get(i).getDiff(), actual.get(i).getDiff(), 0);
        }
    }

    @Test
    public void getWeight() throws Exception {
        List<Weight> weights = new LinkedList<>();
        for (int i = 0; i < 3; i++) {
            weights.add(builder().day(1).value(1).weight());
        }

        WeightDiffList model = new WeightDiffList(weights);

        Weight expectedWeight = weights.get(1);
        Weight actualWeight = model.getWeight(1);
        assertEquals(expectedWeight, actualWeight);
    }

    @Test
    public void size() throws Exception {

        List<Weight> weights = new LinkedList<>();
        for (int i = 0; i < 3; i++) {
            weights.add(builder().day(1).value(0).weight());
        }

        WeightDiffList model = new WeightDiffList(weights);

        assertEquals(3, model.size());
    }

    @NonNull
    private Builder builder() {
        return new Builder();
    }

    private class Builder {
        private Date date;
        private float value;
        private float diff;

        @NonNull
        Builder day(int day) {
            date = new GregorianCalendar(2017, 0, day).getTime();
            return this;
        }

        @NonNull
        Builder value(float value) {
            this.value = value;
            return this;
        }

        @NonNull
        Builder diff(float diff) {
            this.diff = diff;
            return this;
        }

        @NonNull
        Weight weight() {
            Weight w = new Weight();
            w.setDate(date);
            w.setValue(value);
            return w;
        }

        @NonNull
        WeightDiff weightDiff() {
            return new WeightDiff(diff, value, date);
        }
    }
}
