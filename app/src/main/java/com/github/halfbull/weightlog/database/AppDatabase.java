package com.github.halfbull.weightlog.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.support.annotation.NonNull;

@Database(entities = Weight.class, version = 1, exportSchema = false)
@TypeConverters(RoomConvert.class)
public abstract class AppDatabase extends RoomDatabase {

    private static final String NAME = "weight-db";

    public static AppDatabase build(@NonNull Context context) {
        return Room.databaseBuilder(context, AppDatabase.class, NAME).build();
    }

    public abstract WeightDao weightDao();
}
