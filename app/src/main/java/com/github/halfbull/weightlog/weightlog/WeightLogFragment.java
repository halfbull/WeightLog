package com.github.halfbull.weightlog.weightlog;

import android.arch.lifecycle.LifecycleFragment;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.halfbull.weightlog.AppViewModel;
import com.github.halfbull.weightlog.R;
import com.github.halfbull.weightlog.database.Weight;

import java.util.List;

public class WeightLogFragment extends LifecycleFragment implements View.OnClickListener {

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_weight_log, container, false);

        weightLog = v.findViewById(R.id.weightLogRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        weightLog.setLayoutManager(layoutManager);
        weightLog.setAdapter(adapter);
        weightLog.addItemDecoration(new DividerItemDecoration(getContext(), layoutManager.getOrientation()));

        ItemTouchHelper.SimpleCallback touch = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();

                new AsyncTask<Void, Void, Void>() {
                    @Nullable
                    @Override
                    protected Void doInBackground(Void... voids) {
                        if (weightDiffs != null) {
                            Weight weight = weightDiffs.getWeight(position);
                            model.getWeightLogModel().delWeight(weight);
                        }
                        return null;
                    }
                }.execute();
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

        final ContentLoadingProgressBar progressBar = v.findViewById(R.id.weightLogProgressBar);
        progressBar.show();
        model.getWeightDao().getTail().observe(this, new Observer<List<Weight>>() {
            @Override
            public void onChanged(@Nullable List<Weight> weights) {
                weightDiffs =new WeightDiffList(weights);
                adapter.setModel(weightDiffs);
                progressBar.hide();
            }
        });

        model.getWeightLogModel().getRecycle().hasRecycledItems().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean hasRecycledItems) {
                if (hasRecycledItems != null && hasRecycledItems) {
                    showItemsDeletedSnackBar();
                }
            }
        });

        return v;
    }

    private void showItemsDeletedSnackBar() {
        int deletedItems = model.getWeightLogModel().getRecycle().size();
        String message = getResources().getQuantityString(R.plurals.weight_log_remove_snack_message, deletedItems, deletedItems);
        Snackbar.make(weightLog, message, Snackbar.LENGTH_LONG)
                .setAction(R.string.weight_log_remove_snack_undo, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        model.getWeightLogModel().getRecycle().restore();
                    }
                })
                .addCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        super.onDismissed(transientBottomBar, event);

                        if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT) {
                            model.getWeightLogModel().getRecycle().clear();
                        }
                    }
                }).show();
    }

    @Override
    public void onClick(View view) {
        AddWeightDialog d = new AddWeightDialog();
        d.show(getFragmentManager(), "ADD_WEIGHT_DIALOG");
    }
}
