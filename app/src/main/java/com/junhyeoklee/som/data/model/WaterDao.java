package com.junhyeoklee.som.data.model;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.junhyeoklee.som.data.model.WaterEntry;

import java.util.Date;
import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface WaterDao {

    @Query("SELECT * FROM waters")
    LiveData<List<WaterEntry>> loadAllWaters();

    @Query("SELECT * FROM waters WHERE date = :date")
    LiveData<List<WaterEntry>> loadWaterBydate(String date);

    @Query("SELECT * FROM waters WHERE date = :date")
    List<WaterEntry> loadWaterBydate2(String date);

    @Query("SELECT * FROM waters")
    LiveData<WaterEntry> loadWaters();

    @Query("SELECT * FROM waters where amount = :id")
    LiveData<WaterEntry> loadWaterById(int id);

    @Query("SELECT * FROM waters")
    List<WaterEntry> getStaticWaters();

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateWater(WaterEntry waterEntry);

   @Insert
   void insertWater(WaterEntry waterEntry);

   @Delete
   void deleteWater(WaterEntry waterEntry);

    @Delete
    void deleteAllWaters(List<WaterEntry> waterEntries);
}
