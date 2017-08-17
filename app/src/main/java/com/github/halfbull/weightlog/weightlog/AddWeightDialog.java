package com.github.halfbull.weightlog.weightlog;

import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.widget.EditText;

import com.github.halfbull.weightlog.ViewModelHost;
import com.github.halfbull.weightlog.R;
import com.github.halfbull.weightlog.database.Weight;

import java.util.Date;
import java.util.regex.Pattern;

public class AddWeightDialog extends DialogFragment {

    private WeightLogViewModel model;
    private EditText valueEditText;

    @NonNull
    private final InputFilter[] inputFilters = new InputFilter[]{new DecimalInputFilter()};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        model = ViewModelProviders.of(getActivity()).get(ViewModelHost.class).getWeightLogModel();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_add_weight, null);
        valueEditText = v.findViewById(R.id.value_edit_text);

        valueEditText.setFilters(inputFilters);

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.add_weight_dialog_title)
                .setView(v)
                .setPositiveButton(R.string.add_weight_dialog_ok_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String valueRaw = valueEditText.getText().toString();
                        if (valueRaw.equals("")) {
                            return;
                        }

                        final float value = Float.valueOf(valueRaw);
                        new AsyncTask<Void, Void, Void>() {
                            @Nullable
                            @Override
                            protected Void doInBackground(Void... voids) {
                                Weight w = new Weight();
                                w.setDate(new Date());
                                w.setValue(value);
                                model.addWeight(w);
                                return null;
                            }
                        }.execute();
                    }
                })
                .setNegativeButton(R.string.add_weight_dialog_cancel_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).create();
    }

    private class DecimalInputFilter implements InputFilter {

        private final InputFilterTool inputFilterTool = new InputFilterTool();
        private final Pattern pattern = Pattern.compile("[0-9]+(\\.[0-9]?)?");

        @Nullable
        @Override
        public CharSequence filter(@NonNull CharSequence source, int start, int end, @NonNull Spanned dest, int dStart, int dEnd) {
            CharSequence futureString = inputFilterTool.getFutureString(source, start, end, dest, dStart, dEnd);

            if (futureString.length() == 0)
                return null;

            if (pattern.matcher(futureString).matches())
                return null;

            if (inputFilterTool.isDeleteAction(dest, futureString))
                return inputFilterTool.getDeletedPart(dest, dStart, dEnd);

            return "";
        }
    }
}
