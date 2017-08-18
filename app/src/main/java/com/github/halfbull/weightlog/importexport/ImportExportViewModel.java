package com.github.halfbull.weightlog.importexport;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.halfbull.weightlog.database.WeightDao;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ImportExportViewModel {

    @NonNull
    private final CsvImportModel csvImportModel;
    @NonNull
    private final CsvExportModel csvExportModel;

    public ImportExportViewModel(WeightDao weightDao) {
        this.csvImportModel = new CsvImportModel(weightDao, new StreamFactory(), new CsvConverterFactory());
        this.csvExportModel = new CsvExportModel(weightDao, new StreamFactory(), new CsvConverterFactory());
    }

    @NonNull
    LiveData<Result> importLog(@NonNull final File file) {
        final MutableLiveData<Result> liveData = new MutableLiveData<>();

        new AsyncTask<Void, Void, Void>() {
            @Nullable
            @Override
            protected Void doInBackground(Void... voids) {
                Result result;

                try {
                    int recordsProcessed = csvImportModel.importLog(file);
                    result = new Result(Result.SUCCESS, recordsProcessed);
                } catch (FileNotFoundException ex) {
                    result = new Result(Result.FILE_NOT_FOUND_EXCEPTION, 0);
                } catch (IOException ex) {
                    result = new Result(Result.IO_EXCEPTION, 0);
                }

                liveData.postValue(result);

                return null;
            }
        }.execute();

        return liveData;
    }

    @NonNull
    LiveData<Result> exportLog(@NonNull final File file) {
        final MutableLiveData<Result> liveData = new MutableLiveData<>();

        new AsyncTask<Void, Void, Void>() {
            @Nullable
            @Override
            protected Void doInBackground(Void... voids) {
                Result result;

                try {
                    int recordsProcessed = csvExportModel.exportLog(file);
                    result = new Result(Result.SUCCESS, recordsProcessed);
                } catch (IOException ex) {
                    result = new Result(Result.IO_EXCEPTION, 0);
                }

                liveData.postValue(result);

                return null;
            }
        }.execute();

        return liveData;
    }

    class Result {
        static final int SUCCESS = 0;
        static final int FILE_NOT_FOUND_EXCEPTION = 1;
        static final int IO_EXCEPTION = 2;

        private final int result;
        private final int recordsProcessed;

        Result(int result, int recordsProcessed) {
            this.result = result;
            this.recordsProcessed = recordsProcessed;
        }

        int getResult() {
            return result;
        }

        int getRecordsProcessed() {
            return recordsProcessed;
        }
    }
}
