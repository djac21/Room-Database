package com.djac21.room.database.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.TypeConverters;

import com.djac21.room.database.models.Model;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
@TypeConverters(DateConverter.class)
public interface DaoInterface {

    @Query("select * from `table`")
    LiveData<List<Model>> getAllItems();

    @Query("select * from `table` where id = :id")
    Model getItembyId(String id);

    @Insert(onConflict = REPLACE)
    void addItem(Model model);

    @Delete
    void deleteItem(Model model);

    @Query("DELETE FROM `table`")
    void deleteAll();
}
