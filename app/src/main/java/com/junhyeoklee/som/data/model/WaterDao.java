package com.junhyeoklee.som.data.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface WaterDao {

    @Query("SELECT * FROM waters")
    LiveData<List<WaterEntry>> loadAllWaters();

    @Query("SELECT * FROM waters WHERE date = :date")
    LiveData<List<WaterEntry>> loadWaterBydate(String date);

    @Query("SELECT * FROM waters WHERE dateMonth = :dateMonth")
    LiveData<List<WaterEntry>> loadWaterBydateMonth(String dateMonth);

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
