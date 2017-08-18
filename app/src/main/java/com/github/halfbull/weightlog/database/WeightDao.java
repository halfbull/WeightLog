package com.github.halfbull.weightlog.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface WeightDao {

    @Query("SELECT * FROM weight ORDER BY date DESC")
    LiveData<List<Weight>> getLastReversed();

    @Query("SELECT * FROM weight ORDER BY date ASC")
    List<Weight> getAll();

    @Insert
    void insert(Weight weight);

    @Delete
    void delete(Weight weight);
}
