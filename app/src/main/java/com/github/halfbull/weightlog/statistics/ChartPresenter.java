package com.github.halfbull.weightlog.statistics;

import android.app.Activity;
import android.util.TypedValue;
import android.view.MotionEvent;

import com.github.halfbull.weightlog.R;
import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.utils.MPPointD;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

class ChartPresenter implements OnChartGestureListener {

    private final Activity activity;
    private final ScatterChart chart;
    private List<Entry> points;

    private int fragmentIterator;
    private float renderedFragmentCenter = 0;
    private float fragmentPadding = 0;

    private float visibleXAxisStart = 0;
    private float visibleXAxisEnd = 0;
    private float visibleYAxisCenter = 0;

    private int visibleXAxisSize = 0;
    private int visibleYAxisSize = 0;

    ChartPresenter(Activity activity, ScatterChart chart) {
        this.activity = activity;
        this.chart = chart;
    }

    void initialize(List<Entry> points) {
        this.points = points;
        fragmentIterator = points.size() - 1;

        chart.getDescription().setEnabled(false);
        chart.setTouchEnabled(true);
        chart.setScaleEnabled(false);
        chart.setDragEnabled(true);

        chart.getXAxis().setGranularity(1);
        chart.getXAxis().setAxisMinimum(0);
        chart.getXAxis().setAxisMaximum(Float.MAX_VALUE);
        chart.getXAxis().setValueFormatter(new MonthYearDateFormatter());

        chart.getAxisLeft().setGranularity(1);
        chart.getAxisLeft().setAxisMinimum(0);
        chart.getAxisLeft().setAxisMaximum(Float.MAX_VALUE);
        chart.getAxisLeft().setValueFormatter(new WeightFormatter());

        chart.getAxisRight().setEnabled(false);

        visibleXAxisSize = activity.getResources().getInteger(R.integer.visible_x_axis_size);
        visibleYAxisSize = activity.getResources().getInteger(R.integer.visible_y_axis_size);
        chart.setScaleMinima(Float.MAX_VALUE / visibleXAxisSize, Float.MAX_VALUE / visibleYAxisSize);

        fragmentPadding = (float) visibleXAxisSize / 2;

        chart.setOnChartGestureListener(this);

        initializeDataSet();
    }

    private void initializeDataSet() {
        ScatterDataSet weightsDataSet = buildScatterDataSet();
        ScatterData scatterData = new ScatterData();
        scatterData.addDataSet(weightsDataSet);
        chart.setData(scatterData);
    }

    private ScatterDataSet buildScatterDataSet() {
        List<Entry> pts = new LinkedList<>();

        String title = activity.getResources().getString(R.string.graph_weight_series_title);
        ScatterDataSet set = new ScatterDataSet(pts, title);
        set.setScatterShape(ScatterChart.ScatterShape.CIRCLE);
        set.setScatterShapeSize(8);

        set.setDrawVerticalHighlightIndicator(false);
        set.setDrawHorizontalHighlightIndicator(false);
        set.setDrawValues(false);

        int color = activity.getResources().getColor(R.color.colorAccent, activity.getTheme());
        set.setColor(color);
        return set;
    }

    void drawLastSegment() {
        Entry lastPoint = points.get(points.size() - 1);
        computeVisibleXAxisRange(lastPoint.getX());
        visibleYAxisCenter = lastPoint.getY();

        alignFocusPoint();
        centerCameraOnVisibleRegion();
        updateChart();
    }

    private void alignFocusPoint() {
        TypedValue tVal = new TypedValue();
        activity.getResources().getValue(R.integer.graph_last_point_x_axis_screen_position, tVal, false);
        float xShift = visibleXAxisSize * tVal.getFloat();
        visibleXAxisStart -= xShift;
        visibleXAxisEnd -= xShift;

        activity.getResources().getValue(R.integer.graph_last_point_y_axis_screen_position, tVal, false);
        float yShift = visibleYAxisSize * (0.5f - tVal.getFloat());
        visibleYAxisCenter += yShift;
    }

    private void centerCameraOnVisibleRegion() {
        chart.moveViewTo(visibleXAxisStart, visibleYAxisCenter, YAxis.AxisDependency.LEFT);
    }

    private void updateChart() {
        clearScatterData();
        drawGraphFragment(visibleXAxisStart - fragmentPadding, visibleXAxisEnd + fragmentPadding);
        rememberFragmentCenter();
    }

    private void clearScatterData() {
        chart.getScatterData().getDataSetByIndex(0).clear();
    }

    private void drawGraphFragment(float fragmentLeftBound, float fragmentRightBound) {
        IScatterDataSet dataSet = chart.getScatterData().getDataSetByIndex(0);

        alignFragmentIterator(fragmentLeftBound, fragmentRightBound);

        for (int i = this.fragmentIterator; i < points.size() - 1; i++) {
            Entry pt = points.get(i);
            if (pt.getX() < fragmentLeftBound)
                continue;

            if (pt.getX() > fragmentRightBound)
                break;

            dataSet.addEntry(pt);
        }

        dataSet.addEntry(points.get(points.size() - 1));

        chart.notifyDataSetChanged();
    }

    private void rememberFragmentCenter() {
        renderedFragmentCenter = visibleXAxisStart + (visibleXAxisEnd - visibleXAxisStart) / 2;
    }

    private void alignFragmentIterator(float xMin, float xMax) {
        while (fragmentIterator > 0) {
            Entry pt = points.get(fragmentIterator);

            if (pt.getX() == xMin)
                return;

            if (pt.getX() < xMin)
                break;

            fragmentIterator--;
        }

        while (fragmentIterator < points.size() - 2) {
            Entry pt = points.get(fragmentIterator);

            if (pt.getX() >= xMin && pt.getX() <= xMax)
                break;

            fragmentIterator++;
        }
    }

    private void computeVisibleXAxisRange(float visibleXAxisStart) {
        this.visibleXAxisStart = visibleXAxisStart;
        visibleXAxisEnd = visibleXAxisStart + visibleXAxisSize;
    }

    private void computeVisibleXAxisRange() {
        ViewPortHandler h = chart.getViewPortHandler();
        MPPointD topLeft = chart.getValuesByTouchPoint(h.contentLeft(), h.contentTop(), YAxis.AxisDependency.LEFT);
        MPPointD bottomRight = chart.getValuesByTouchPoint(h.contentRight(), h.contentBottom(), YAxis.AxisDependency.LEFT);
        visibleXAxisStart = (float) topLeft.x;
        visibleXAxisEnd = (float) bottomRight.x;
    }

    private boolean isFragmentAlreadyRendered() {
        return visibleXAxisStart < renderedFragmentCenter && renderedFragmentCenter < visibleXAxisEnd;
    }

    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartLongPressed(MotionEvent me) {

    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {

    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {

    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {

    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {
        computeVisibleXAxisRange();
        if (isFragmentAlreadyRendered()) {
            return;
        }

        chart.post(new Runnable() {
            @Override
            public void run() {
                updateChart();
            }
        });
    }

    private class MonthYearDateFormatter implements IAxisValueFormatter {

        private final SimpleDateFormat monthYearFormat = new SimpleDateFormat("MMM yy", Locale.getDefault());
        private final FloatDateConverter floatDateConverter = new FloatDateConverter();

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return monthYearFormat.format(floatDateConverter.floatMonthToDate(value));
        }
    }

    private class WeightFormatter implements IAxisValueFormatter {
        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return String.format("%3s", (int) value).replace(' ', '0');
        }
    }
}
