package com.github.halfbull.weightlog.statistics;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.TypedValue;

import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PanZoom;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.StepMode;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.github.halfbull.weightlog.R;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

class GraphPointPresenter {

    private final Activity activity;
    private final XYPlot plot;
    private List<GraphPoint> graph;

    GraphPointPresenter(Activity activity, XYPlot plot) {
        this.activity = activity;
        this.plot = plot;
    }

    void Set(@NonNull List<GraphPoint> graph) {
        this.graph = graph;
        PanZoom.attach(plot);

        plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new MonthYearDateFormat());
        plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.LEFT).setFormat(new WeightFormat());

        drawGraph();
        setBoundaries();

        plot.redraw();
    }

    private void drawGraph() {
        String title = activity.getResources().getString(R.string.graph_weight_series_title);
        SimpleXYSeries series = new SimpleXYSeries(title);

        for (GraphPoint p : graph)
            series.addLast(p.getFloatMonth(), p.getValue());

        plot.addSeries(series, new LineAndPointFormatter(activity, R.xml.graph_point));
    }

    private void setBoundaries() {

        int rangeSize = activity.getResources().getInteger(R.integer.graph_range_size);
        float rangeStart = getRangeStart(rangeSize);
        plot.setRangeBoundaries(rangeStart, rangeStart + rangeSize, BoundaryMode.FIXED);
        plot.setRangeStep(StepMode.INCREMENT_BY_VAL, 5);
        plot.setUserRangeOrigin(0);

        int domainSize = activity.getResources().getInteger(R.integer.graph_domain_size);
        float domainStart = getDomainStart(domainSize);
        plot.setDomainBoundaries(domainStart, domainStart + domainSize, BoundaryMode.FIXED);
        plot.setDomainStep(StepMode.INCREMENT_BY_VAL, 1);
        plot.setUserDomainOrigin(0);

        plot.getOuterLimits().set(0, Integer.MAX_VALUE, 0, Integer.MAX_VALUE);
    }

    private float getRangeStart(int rangeSize) {
        if (graph.isEmpty())
            return 0;

        TypedValue tVal = new TypedValue();
        activity.getResources().getValue(R.integer.graph_last_point_range_position, tVal, false);
        float lastPointRangePosition = tVal.getFloat();

        float rangeStart = graph.get(graph.size() - 1).getValue();
        rangeStart -= rangeSize * lastPointRangePosition;
        return rangeStart;
    }

    private float getDomainStart(int domainSize) {
        if (graph.isEmpty())
            return 0;

        TypedValue tVal = new TypedValue();
        activity.getResources().getValue(R.integer.graph_last_point_domain_position, tVal, false);
        float lastPointDomainPosition = tVal.getFloat();

        float domainStart = graph.get(graph.size() - 1).getFloatMonth();
        domainStart -= domainSize * lastPointDomainPosition;
        return domainStart;
    }

    private class MonthYearDateFormat extends Format {

        private final SimpleDateFormat monthYearFormat = new SimpleDateFormat("MMM yy", Locale.getDefault());

        private final FloatDateConverter floatDateConverter = new FloatDateConverter();

        @Override
        public StringBuffer format(@NonNull Object o, @NonNull StringBuffer stringBuffer, @NonNull FieldPosition fieldPosition) {
            float floatMonth = ((Number) o).floatValue();
            String formattedDate = monthYearFormat.format(floatDateConverter.floatMonthToDate(floatMonth));
            return stringBuffer.append(formattedDate);
        }

        @Nullable
        @Override
        public Object parseObject(String s, @NonNull ParsePosition parsePosition) {
            return null;
        }
    }

    private class WeightFormat extends Format {
        @Override
        public StringBuffer format(@NonNull Object o, @NonNull StringBuffer stringBuffer, @NonNull FieldPosition fieldPosition) {
            float weight = ((Number) o).floatValue();
            return stringBuffer.append((int) weight);
        }

        @Nullable
        @Override
        public Object parseObject(String s, @NonNull ParsePosition parsePosition) {
            return null;
        }
    }
}
