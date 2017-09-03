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
        //Log.i("!!!", "DELETE_ITEM " + adapter.getSelectedItemPosition());

        //new MaterialDialog.Builder(getActivity()).title("Sure?").negativeText("Cancel").positiveText("YA").show();

        /*DialogFragment df = new DialogFragment(){
            @NonNull
            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                return new MaterialDialog.Builder(getActivity()).title("Sure?").negativeText("Cancel").positiveText("YA").build();
            }
        };
        df.show(getFragmentManager(), "DELETE_WEIGHT_DIALOG");*/

        DeleteWeightDialog dialog = new DeleteWeightDialog();

        /*Bundle bundle = new Bundle();
        bundle.putString("DELETE_WEIGHT_DIALOG_CONTENT", "!!!!!!!!!!!!");
        dialog.setArguments(bundle);*/
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

        /*weightLog.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                //Object o = this;
                //Log.i("!!!!","onCreateContextMenu() => "+view.getId());

            }
        });*/




        /*weightLog.setLongClickable(true);
        weightLog.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Log.i("!!!", "OnLongClick");
                return true;
            }
        });*/

        //weightLog.regi


        /*ItemTouchHelper.SimpleCallback touch = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
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
        touchHelper.attachToRecyclerView(weightLog);*/

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

        /*model.getWeightLogModel().getRecycle().hasRecycledItems().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean hasRecycledItems) {
                if (hasRecycledItems != null && hasRecycledItems) {
                    showItemsDeletedSnackBar();
                }
            }
        });*/

        return v;
    }

    /*private void showItemsDeletedSnackBar() {
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
    }*/

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_fab :
                AddWeightDialog d = new AddWeightDialog();
                d.setTargetFragment(this, 0);
                d.show(getFragmentManager(), "ADD_WEIGHT_DIALOG");
                break;
        }
    }

    @Override
    public void onWeightDeletionConfirmed() {
        if (weightDiffs == null) {
            return;
        }

        new AsyncTask<Void,Void,Void>() {
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
        new AsyncTask<Void,Void,Void>(){
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

    private Weight getSelectedWeight() {
        if(weightDiffs == null)
            return null;

        int position = adapter.getSelectedItemPosition();
        return weightDiffs.getWeight(position);
    }
}
