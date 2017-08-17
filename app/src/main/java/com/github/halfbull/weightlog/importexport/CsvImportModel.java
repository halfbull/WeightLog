package com.github.halfbull.weightlog.importexport;

import android.support.annotation.NonNull;

import com.github.halfbull.weightlog.database.Weight;
import com.github.halfbull.weightlog.database.WeightDao;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class CsvImportModel {

    private final WeightDao weightDao;
    private final StreamFactory streamFactory;
    private final CsvConverterFactory csvConverterFactory;

    CsvImportModel(WeightDao weightDao, StreamFactory streamFactory, CsvConverterFactory csvConverterFactory) {
        this.weightDao = weightDao;
        this.streamFactory = streamFactory;
        this.csvConverterFactory = csvConverterFactory;
    }

    int importLog(@NonNull File file) throws IOException {
        try (Reader reader = streamFactory.getReader(file)) {
            CsvDeserializer csv = csvConverterFactory.getDeserializer(reader);
            List<Weight> importList = csv.deserialize();
            Set<Long> databaseTimestamps = getTimeStampsFromDatabase();
            return addUniqueEntries(databaseTimestamps, importList);
        }
    }

    @NonNull
    private Set<Long> getTimeStampsFromDatabase() {
        Set<Long> databaseSet = new HashSet<>();

        for (Weight w : weightDao.getAll()) {
            Long roundTimestamp = getTimestampRoundedBySeconds(w.getDate());
            if (!databaseSet.contains(roundTimestamp))
                databaseSet.add(roundTimestamp);
        }

        return databaseSet;
    }

    private int addUniqueEntries(@NonNull Set<Long> databaseTimestamps, @NonNull List<Weight> importList) {
        int added = 0;
        for (Weight w : importList) {

            Long roundTimestamp = getTimestampRoundedBySeconds(w.getDate());
            if (!databaseTimestamps.contains(roundTimestamp)) {
                weightDao.insert(w);
                added++;
                databaseTimestamps.add(roundTimestamp);
            }
        }
        return added;
    }

    private Long getTimestampRoundedBySeconds(Date date) {
        return date.getTime() / 1000 * 1000;
    }
}
