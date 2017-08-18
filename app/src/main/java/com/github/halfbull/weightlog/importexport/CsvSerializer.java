package com.github.halfbull.weightlog.importexport;

import android.support.annotation.NonNull;

import com.github.halfbull.weightlog.database.Weight;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

class CsvSerializer extends CsvConverter {

    private final Writer output;

    CsvSerializer(Writer output) {
        this.output = output;
    }

    void serialize(@NonNull List<Weight> weights) throws IOException {
        for (Weight weight : weights) {
            output.write(serialize(weight));
        }
    }

    @NonNull
    private String serialize(@NonNull Weight weight) {
        return DATE_FORMAT.format(weight.getDate()) + "\t" + weight.getValue() + "\r\n";
    }
}
