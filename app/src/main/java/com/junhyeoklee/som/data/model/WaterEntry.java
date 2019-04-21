package com.junhyeoklee.som.data.model;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/**
 * Created by junhyeok_lee on 2018. 2. 23..
 */
@Entity(tableName = "waters")
public class WaterEntry {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private int amount;
    @ColumnInfo(name = "date")
    private String date;
    //그래프 달마다 체크하기 위함
    private String dateMonth;

    @Ignore
    public WaterEntry(int amount,String date,String dateMonth){
        this.amount = amount;
        this.date = date;
        this.dateMonth = dateMonth;
    }

    public WaterEntry(int id,int amount,String date,String dateMonth){
        this.id = id;
        this.amount = amount;
        this.date = date;
        this.dateMonth = dateMonth;
    }


    public void setId(int id) {
        this.id = id;
    }
    public int getId() {return id;}

    public void setAmount(int amount) {this.amount = amount; }
    public int getAmount() {return amount;}



    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDateMonth() {
        return dateMonth;
    }

    public void setDateMonth(String dateMonth) {
        this.dateMonth = dateMonth;
    }
}
