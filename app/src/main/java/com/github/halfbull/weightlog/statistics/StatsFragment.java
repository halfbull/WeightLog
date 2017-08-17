package com.github.halfbull.weightlog.statistics;

import android.arch.lifecycle.LifecycleFragment;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidplot.xy.XYPlot;
import com.github.halfbull.weightlog.ViewModelHost;
import com.github.halfbull.weightlog.R;

import java.util.List;

public class StatsFragment extends LifecycleFragment {

    private ViewModelHost model;
    private GraphPointPresenter graphPointPresenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        model = ViewModelProviders.of(getActivity()).get(ViewModelHost.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_stats, container, false);

        graphPointPresenter = new GraphPointPresenter(getActivity(), (XYPlot) v.findViewById(R.id.plot));

        model.getGraphModel().getGraph().observe(this, new Observer<List<GraphPoint>>() {
            @Override
            public void onChanged(@Nullable List<GraphPoint> graph) {
                if (graph != null) {
                    graphPointPresenter.Set(graph);
                }
            }
        });

        return v;
    }
}
