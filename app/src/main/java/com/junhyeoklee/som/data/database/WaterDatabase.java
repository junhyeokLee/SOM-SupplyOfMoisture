package com.junhyeoklee.som.data.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.util.Log;

import com.junhyeoklee.som.data.DateConverter;
import com.junhyeoklee.som.data.model.WaterDao;
import com.junhyeoklee.som.data.model.WaterEntry;
@Database(entities = {WaterEntry.class},version = 11, exportSchema = false)
@TypeConverters(DateConverter.class)
public abstract class WaterDatabase extends RoomDatabase {
    private static final String LOG_TAG = WaterDatabase.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "waterDB";
    private static WaterDatabase sInstance;

    public static WaterDatabase getInstance(Context context){

        if(sInstance == null){
            synchronized (LOCK){
                Log.d(LOG_TAG, "Creating new database instance");
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        WaterDatabase.class,WaterDatabase.DATABASE_NAME)
                        .fallbackToDestructiveMigration()
                        .build();
            }
        }
        Log.d(LOG_TAG, "Getting the database instance");
        return sInstance;
    }

    public abstract WaterDao waterDao();
}
