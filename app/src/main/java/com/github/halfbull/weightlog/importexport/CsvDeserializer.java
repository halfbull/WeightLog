package com.github.halfbull.weightlog.importexport;

import android.support.annotation.NonNull;

import com.github.halfbull.weightlog.database.Weight;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;

class CsvDeserializer extends CsvConverter {

    private final Reader input;

    CsvDeserializer(Reader input) {
        this.input = input;
    }

    @NonNull
    List<Weight> deserialize() throws IOException {
        List<Weight> list = new LinkedList<>();

        try (BufferedReader r = new BufferedReader(input)) {
            String line;
            while ((line = r.readLine()) != null) {
                try {
                    list.add(deserialize(line));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            return list;
        }
    }

    @NonNull
    private Weight deserialize(@NonNull String line) throws ParseException {
        String[] tokens = line.split("\t");

        Weight w = new Weight();

        w.setDate(DATE_FORMAT.parse(tokens[0]));
        w.setValue(Float.parseFloat(tokens[1]));

        return w;
    }
}
