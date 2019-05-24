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
    //그래프 ~ 년도 1~12월까지 체크
    private String dateMonth;

    // 그래프 ~월 1~31일까지 체크
    private String dateWeek;

    // 마셨을때의 시간을 체크하기 위함
    private String dateTime;

    @Ignore
    public WaterEntry(int amount,String date,String dateMonth,String dateWeek,String dateTime){
        this.amount = amount;
        this.date = date;
        this.dateMonth = dateMonth;
        this.dateWeek = dateWeek;
        this.dateTime = dateTime;
    }

    public WaterEntry(int id,int amount,String date,String dateMonth,String dateWeek,String dateTime){
        this.id = id;
        this.amount = amount;
        this.date = date;
        this.dateMonth = dateMonth;
        this.dateWeek = dateWeek;
        this.dateTime = dateTime;
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

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getDateWeek() {
        return dateWeek;
    }

    public void setDateWeek(String dateWeek) {
        this.dateWeek = dateWeek;
    }
}
