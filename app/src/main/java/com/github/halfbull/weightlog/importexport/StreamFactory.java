package com.github.halfbull.weightlog.importexport;

import android.support.annotation.NonNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

class StreamFactory {

    @NonNull
    FileReader getReader(@NonNull File file) throws FileNotFoundException {
        return new FileReader(file);
    }

    @NonNull
    FileWriter getWriter(@NonNull File file) throws IOException {
        return new FileWriter(file);
    }
}
