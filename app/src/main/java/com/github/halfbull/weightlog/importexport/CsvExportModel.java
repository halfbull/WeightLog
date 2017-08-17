package com.github.halfbull.weightlog.importexport;

import android.support.annotation.NonNull;

import com.github.halfbull.weightlog.database.Weight;
import com.github.halfbull.weightlog.database.WeightDao;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

class CsvExportModel {

    private final WeightDao weightDao;
    private final StreamFactory streamFactory;
    private final CsvConverterFactory csvConverterFactory;

    CsvExportModel(WeightDao weightDao, StreamFactory streamFactory, CsvConverterFactory csvConverterFactory) {
        this.weightDao = weightDao;
        this.streamFactory = streamFactory;
        this.csvConverterFactory = csvConverterFactory;
    }

    int exportLog(@NonNull File file) throws IOException {
        try (Writer writer = streamFactory.getWriter(file)) {
            CsvSerializer csvSerializer = csvConverterFactory.getSerializer(writer);
            List<Weight> weights = weightDao.getAll();
            csvSerializer.serialize(weights);
            return weights.size();
        }
    }
}
