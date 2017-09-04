package com.github.halfbull.weightlog.statistics;

import android.arch.lifecycle.LifecycleFragment;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.halfbull.weightlog.R;
import com.github.halfbull.weightlog.AppViewModel;
import com.github.halfbull.weightlog.database.Weight;
import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.data.Entry;

import java.util.List;

public class StatisticsFragment  extends LifecycleFragment {

    private AppViewModel model;
    private ChartPresenter chartPresenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        model = ViewModelProviders.of(getActivity()).get(AppViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_statistics, container, false);

        //ToDo : chart localize (no chart data available)
        chartPresenter = new ChartPresenter(getActivity(), (ScatterChart) v.findViewById(R.id.chart));

        final ContentLoadingProgressBar progressBar = v.findViewById(R.id.statisticsProgressBar);
        progressBar.show();
        model.getGraphModel().getGraph().observe(this, new Observer<List<Entry>>() {
            @Override
            public void onChanged(@Nullable List<Entry> graph) {
                if (graph != null && graph.size() > 0) {
                    chartPresenter.initialize(graph);
                    chartPresenter.drawLastSegment();
                }
                progressBar.hide();
            }
        });

        return v;
    }
}
