package com.github.halfbull.weightlog.importexport;

import android.support.annotation.NonNull;

import java.io.Reader;
import java.io.Writer;

class CsvConverterFactory {

    @NonNull
    CsvDeserializer getDeserializer(Reader input) {
        return new CsvDeserializer(input);
    }

    @NonNull
    CsvSerializer getSerializer(Writer output) {
        return new CsvSerializer(output);
    }
}
