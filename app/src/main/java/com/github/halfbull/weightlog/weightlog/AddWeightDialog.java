package com.github.halfbull.weightlog.weightlog;

import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.halfbull.weightlog.AppViewModel;
import com.github.halfbull.weightlog.R;
import com.github.halfbull.weightlog.database.Weight;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Pattern;

public class AddWeightDialog extends DialogFragment implements MaterialDialog.SingleButtonCallback {

    interface AddWeightDialogListener {
        void onWeightAdded(float value);
    }

    private final String ARG_KEY_VALUE = "ADD_WEIGHT_DIALOG_VALUE";

    @NonNull
    private final InputFilter[] inputFilters = new InputFilter[]{new DecimalInputFilter()};

    public void setArguments(float defaultValue) {
        Bundle bundle = new Bundle();
        bundle.putFloat(ARG_KEY_VALUE, defaultValue);
        setArguments(bundle);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String defaultValue = getDefaultValue();
        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .title(R.string.add_weight_dialog_title)
                .positiveText(R.string.add_weight_dialog_ok_button)
                .negativeText(R.string.add_weight_dialog_cancel_button)
                .inputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL)
                .input(null, defaultValue, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {

                    }
                })
                .onPositive(this)
                .build();

        EditText input = dialog.getInputEditText();
        if (input != null) {
            input.setFilters(inputFilters);
            input.setImeOptions(EditorInfo.IME_FLAG_NO_FULLSCREEN);
        }

        return dialog;
    }

    @Override
    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
        EditText input = dialog.getInputEditText();
        if (input == null)
            return;

        final String valueRaw = input.getText().toString();
        if (valueRaw.equals(""))
            return;

        final float value = Float.valueOf(valueRaw);

        AddWeightDialogListener fragment = (AddWeightDialogListener) getTargetFragment();
        fragment.onWeightAdded(value);
    }

    private String getDefaultValue() {
        Bundle bundle = getArguments();
        float value = bundle.getFloat(ARG_KEY_VALUE);
        return Float.toString(value);
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
