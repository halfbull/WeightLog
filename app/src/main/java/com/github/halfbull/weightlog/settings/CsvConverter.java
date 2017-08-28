package com.github.halfbull.weightlog.settings;

import com.github.halfbull.weightlog.database.Weight;

import java.io.IOException;
import java.io.Writer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class CsvConverter {

    private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);

    Weight deserialize(String line) throws ParseException {
        String[] tokens = line.split("\t");

        Weight w = new Weight();
        w.setDate(DATE_FORMAT.parse(tokens[0]));
        w.setValue(Float.parseFloat(tokens[1]));
        return w;
    }

    void serialize(Writer writer, Weight weight) throws IOException {
        writer.write(DATE_FORMAT.format(weight.getDate()));
        writer.write("\t");
        writer.write(Float.toString(weight.getValue()));
        writer.write("\r\n");
    }
}
