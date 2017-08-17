package com.github.halfbull.weightlog.database;

import android.arch.persistence.room.TypeConverter;
import android.support.annotation.NonNull;

import java.util.Date;

class RoomConvert {

    @TypeConverter
    @SuppressWarnings("WeakerAccess") // required by Room
    public static Long dateToTimestamp(@NonNull Date date) {
        return date.getTime();
    }


    @NonNull
    @TypeConverter
    @SuppressWarnings("WeakerAccess") // required by Room
    public Date timestampToDate(Long timestamp) {
        return new Date(timestamp);
    }
}
