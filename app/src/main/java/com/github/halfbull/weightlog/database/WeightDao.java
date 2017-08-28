package com.github.halfbull.weightlog.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface WeightDao {

    //@Query("SELECT * FROM weight")
    //LiveData<List<Weight>> getLastReversed();

    //@Query("SELECT * FROM weight ORDER BY id DESC LIMIT :offset, :count")
    //LiveData<List<Weight>> getPage(int offset, int count);

    @Query("SELECT * FROM weight LIMIT 300 OFFSET (SELECT COUNT(*) FROM weight) - 300")
    LiveData<List<Weight>> getTail();

    @Query("SELECT * FROM weight")
    List<Weight> getAll();

    @Query("SELECT * FROM weight")
    LiveData<List<Weight>> getAll2();

    @Insert
    void insert(Weight weight);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertList(List<Weight> weight);

    @Delete
    void delete(Weight weight);

    @Query("SELECT COUNT(*) FROM weight")
    int size();

    //@Query("SELECT COUNT(*) FROM weight")
    //LiveData<Integer> sizeLive();
}
