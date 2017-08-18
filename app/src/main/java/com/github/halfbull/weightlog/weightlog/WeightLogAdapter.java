package com.github.halfbull.weightlog.weightlog;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.halfbull.weightlog.R;
import com.github.halfbull.weightlog.database.Weight;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;

class WeightLogAdapter extends RecyclerView.Adapter<WeightLogAdapter.WeightViewHolder> {

    private WeightDiffList weightDiffs;

    WeightLogAdapter() {
        weightDiffs = new WeightDiffList(new LinkedList<Weight>());
    }

    @NonNull
    @Override
    public WeightViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.weight_log_row, parent, false);
        return new WeightViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeightViewHolder holder, int position) {
        WeightDiff weightDiff = weightDiffs.getDiff(position);
        holder.bind(weightDiff);
    }

    @Override
    public int getItemCount() {
        return weightDiffs.size();
    }

    void setModel(WeightDiffList weightDiffs) {
        this.weightDiffs = weightDiffs;
        notifyDataSetChanged();
    }

    class WeightViewHolder extends RecyclerView.ViewHolder {

        @SuppressWarnings("SpellCheckingInspection")
        final SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy, kk:mm", Locale.getDefault());
        private final TextView diff;
        private final TextView value;
        private final TextView date;

        WeightViewHolder(@NonNull View itemView) {
            super(itemView);
            diff = itemView.findViewById(R.id.diff_tv);
            value = itemView.findViewById(R.id.value_tv);
            date = itemView.findViewById(R.id.date_tv);
        }

        void bind(@NonNull WeightDiff weightDiff) {
            diff.setText(formatDiff(weightDiff.getDiff()));
            value.setText(formatFloat(weightDiff.getValue()));
            date.setText(formatDate(weightDiff.getDate()));

            if (weightDiff.getDiff() < 0) {
                diff.setBackgroundResource(R.drawable.circle_shape_minus);
            } else if (weightDiff.getDiff() > 0) {
                diff.setBackgroundResource(R.drawable.circle_shape_plus);
            }
        }

        private String formatFloat(float v) {
            return String.format(Locale.US, "%.1f", v);
        }

        private String formatDiff(float v) {
            String str = formatFloat(v);
            if (v == 0)
                return "";
            if (v > 0)
                return "+" + str;
            return str;
        }

        private String formatDate(Date date) {
            return dateFormat.format(date);
        }
    }
}
