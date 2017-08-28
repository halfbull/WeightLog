package com.github.halfbull.weightlog.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

@Entity
public class Weight {

    @PrimaryKey
    private long id;

    @ColumnInfo
    private float value;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public Date getDate() {
        return new Date(id);
    }

    public void setDate(Date date) {
        this.id = date.getTime();
    }
}
