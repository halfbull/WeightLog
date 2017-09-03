package com.github.halfbull.weightlog.weightlog;

import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import com.github.halfbull.weightlog.R;
import com.github.halfbull.weightlog.database.Weight;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DeleteWeightDialog extends DialogFragment {

    interface DeleteWeightDialogListener {
        void onWeightDeletionConfirmed();
    }

    private final String ARG_KEY_VALUE = "DELETE_WEIGHT_DIALOG_VALUE";
    private final String ARG_KEY_TIME = "DELETE_WEIGHT_DIALOG_TIME";

    @SuppressWarnings("SpellCheckingInspection")
    final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy, kk:mm", Locale.getDefault());

    public void setArguments(Weight weight) {
        Bundle bundle = new Bundle();
        bundle.putFloat(ARG_KEY_VALUE, weight.getValue());
        bundle.putLong(ARG_KEY_TIME, weight.getDate().getTime());
        setArguments(bundle);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final DeleteWeightDialogListener fragment = (DeleteWeightDialogListener) getTargetFragment();

        return new MaterialDialog.Builder(getActivity())
                .title(R.string.delete_weight_dialog_title)
                .content(getContent())
                .negativeText(R.string.delete_weight_dialog_cancel_button)
                .positiveText(R.string.delete_weight_dialog_ok_button)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        fragment.onWeightDeletionConfirmed();
                    }
                })
                .build();
    }

    private String getContent() {
        Bundle bundle = getArguments();
        float value = bundle.getFloat(ARG_KEY_VALUE);
        Date date = new Date(bundle.getLong(ARG_KEY_TIME));

        String valueStr = Float.toString(value);
        String dateStr = dateFormat.format(date);
        return valueStr + "\r\n" + dateStr;
    }
}
