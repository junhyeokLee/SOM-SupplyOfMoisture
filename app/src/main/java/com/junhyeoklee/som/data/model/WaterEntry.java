package com.junhyeoklee.som.data.model;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.Nullable;

import java.util.Date;

/**
 * Created by junhyeok_lee on 2018. 2. 23..
 */
@Entity(tableName = "waters")
public class WaterEntry {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private int amount;
    @ColumnInfo(name = "updated_at")
    private String updateAt;

    @Ignore
    public WaterEntry(int amount,String updateAt){
        this.amount = amount;
        this.updateAt = updateAt;
    }

    public WaterEntry(int id,int amount,String updateAt){
        this.id = id;
        this.amount = amount;
        this.updateAt = updateAt;
    }


    public void setId(int id) {
        this.id = id;
    }
    public int getId() {return id;}

    public void setAmount(int amount) {this.amount = amount; }
    public int getAmount() {return amount;}



    public String getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(String updateAt) {
        this.updateAt = updateAt;
    }

}
