package com.github.halfbull.weightlog.importexport;

import android.support.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Locale;

abstract class CsvConverter {

    @NonNull
    final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
}
