package com.github.halfbull.weightlog.weightlog;

import android.arch.lifecycle.LifecycleFragment;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.github.halfbull.weightlog.AppViewModel;
import com.github.halfbull.weightlog.R;
import com.github.halfbull.weightlog.database.Weight;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class WeightLogFragment extends LifecycleFragment implements
        View.OnClickListener, DeleteWeightDialog.DeleteWeightDialogListener, AddWeightDialog.AddWeightDialogListener {

    private AppViewModel model;
    private WeightLogAdapter adapter;
    @Nullable
    private WeightDiffList weightDiffs;
    private RecyclerView weightLog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        model = ViewModelProviders.of(getActivity()).get(AppViewModel.class);
        adapter = new WeightLogAdapter(getActivity());
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        DeleteWeightDialog dialog = new DeleteWeightDialog();
        Weight weight = getSelectedWeight();
        dialog.setArguments(weight);
        dialog.setTargetFragment(this, 0);
        dialog.show(getFragmentManager(), "DELETE_WEIGHT_DIALOG");
        return super.onContextItemSelected(item);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_weight_log, container, false);

        weightLog = v.findViewById(R.id.weightLogRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        weightLog.setLayoutManager(layoutManager);
        weightLog.setAdapter(adapter);
        weightLog.addItemDecoration(new DividerItemDecoration(getContext(), layoutManager.getOrientation()));

        FloatingActionButton fab = v.findViewById(R.id.add_fab);
        fab.setOnClickListener(this);

        final ContentLoadingProgressBar progressBar = v.findViewById(R.id.weightLogProgressBar);
        progressBar.show();
        model.getWeightDao().getTail().observe(this, new Observer<List<Weight>>() {
            @Override
            public void onChanged(@Nullable List<Weight> weights) {
                weightDiffs = new WeightDiffList(weights);
                adapter.setModel(weightDiffs);
                progressBar.hide();
            }
        });

        return v;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_fab:
                AddWeightDialog dialog = new AddWeightDialog();
                float defaultValue = getNewDefaultValue();
                dialog.setArguments(defaultValue);
                dialog.setTargetFragment(this, 0);
                dialog.show(getFragmentManager(), "ADD_WEIGHT_DIALOG");
                break;
        }
    }

    @Override
    public void onWeightDeletionConfirmed() {
        if (weightDiffs == null) {
            return;
        }

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                Weight weight = getSelectedWeight();
                model.getWeightDao().delete(weight);
                return null;
            }
        }.execute();
    }

    @Override
    public void onWeightAdded(final float value) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                //ToDo this should be in ViewModel
                GregorianCalendar calendar = new GregorianCalendar();
                calendar.setTime(new Date());
                calendar.set(Calendar.MILLISECOND, 0);

                Weight w = new Weight();
                w.setDate(calendar.getTime());
                w.setValue(value);
                model.getWeightLogModel().addWeight(w);
                return null;
            }
        }.execute();
    }

    private float getNewDefaultValue() {
        float defaultValue = 75;
        if(weightDiffs != null && weightDiffs.size() > 0){
            Weight weight = weightDiffs.getWeight(0);
            if(weight != null) {
                defaultValue = weight.getValue();
            }
        }
        return defaultValue;
    }

    private Weight getSelectedWeight() {
        if (weightDiffs == null)
            return null;

        int position = adapter.getSelectedItemPosition();
        return weightDiffs.getWeight(position);
    }
}
