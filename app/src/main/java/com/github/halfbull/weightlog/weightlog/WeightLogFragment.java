package com.github.halfbull.weightlog.weightlog;

import android.arch.lifecycle.LifecycleFragment;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.halfbull.weightlog.ViewModelHost;
import com.github.halfbull.weightlog.R;
import com.github.halfbull.weightlog.database.Weight;

public class WeightLogFragment extends LifecycleFragment implements View.OnClickListener {

    private WeightLogViewModel model;
    private WeightLogAdapter weightLogAdapter;
    @Nullable
    private WeightDiffList weightDiffs;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        model = ViewModelProviders.of(getActivity()).get(ViewModelHost.class).getWeightLogModel();
        weightLogAdapter = new WeightLogAdapter();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_weight_log, container, false);

        RecyclerView weightLog = v.findViewById(R.id.weightLogRecyclerView);
        weightLog.setLayoutManager(new LinearLayoutManager(getContext()));
        weightLog.setAdapter(weightLogAdapter);

        ItemTouchHelper.SimpleCallback touch = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                WeightDiffList weightDiffs = WeightLogFragment.this.weightDiffs;
                if (weightDiffs != null) {
                    int position = viewHolder.getAdapterPosition();
                    final Weight weight = weightDiffs.getWeight(position);

                    new AsyncTask<Void, Void, Void>() {
                        @Nullable
                        @Override
                        protected Void doInBackground(Void... voids) {
                            model.delWeight(weight);
                            return null;
                        }
                    }.execute();
                }
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }
        };
        ItemTouchHelper touchHelper = new ItemTouchHelper(touch);
        touchHelper.attachToRecyclerView(weightLog);

        FloatingActionButton fab = v.findViewById(R.id.add_fab);
        fab.setOnClickListener(this);

        model.getWeightDiffs().observe(this, new Observer<WeightDiffList>() {
            @Override
            public void onChanged(@Nullable WeightDiffList weightDiffs) {
                WeightLogFragment.this.weightDiffs = weightDiffs;
                if (weightDiffs != null) {
                    weightLogAdapter.setModel(weightDiffs);
                }
            }
        });

        return v;
    }

    @Override
    public void onClick(View view) {
        AddWeightDialog d = new AddWeightDialog();
        d.show(getFragmentManager(), "ADD_WEIGHT_DIALOG");
    }
}
