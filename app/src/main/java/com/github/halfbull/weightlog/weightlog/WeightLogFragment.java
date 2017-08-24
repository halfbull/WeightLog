package com.github.halfbull.weightlog.weightlog;

import android.arch.lifecycle.LifecycleFragment;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.ContentLoadingProgressBar;
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
    private WeightLogAdapter adapter;
    @Nullable
    private WeightDiffList weightDiffs;
    private RecyclerView weightLog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        model = ViewModelProviders.of(getActivity()).get(ViewModelHost.class).getWeightLogModel();
        adapter = new WeightLogAdapter();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_weight_log, container, false);

        weightLog = v.findViewById(R.id.weightLogRecyclerView);
        weightLog.setLayoutManager(new LinearLayoutManager(getContext()));
        weightLog.setAdapter(adapter);

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
                            model.delWeight(weight);
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
        model.getWeightDiffs().observe(this, new Observer<WeightDiffList>() {
            @Override
            public void onChanged(@Nullable WeightDiffList weightDiffs) {
                WeightLogFragment.this.weightDiffs = weightDiffs;
                if (weightDiffs != null) {
                    adapter.setModel(weightDiffs);
                    progressBar.hide();
                }
            }
        });

        model.getRecycle().hasRecycledItems().observe(this, new Observer<Boolean>() {
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
        int deletedItems = model.getRecycle().size();
        String message = getResources().getQuantityString(R.plurals.weight_log_remove_snack_message, deletedItems, deletedItems);
        Snackbar.make(weightLog, message, Snackbar.LENGTH_LONG)
                .setAction(R.string.weight_log_remove_snack_undo, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        model.getRecycle().restore();
                    }
                })
                .addCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        super.onDismissed(transientBottomBar, event);

                        if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT) {
                            model.getRecycle().clear();
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
