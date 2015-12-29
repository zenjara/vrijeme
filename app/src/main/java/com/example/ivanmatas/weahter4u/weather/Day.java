package com.example.ivanmatas.weahter4u.weather;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by IvanMatas on 12/7/2015.
 */

//////
    // radimo klasu dan s varijablama koje cemo koristit za prikaz korisniku na ekranu, informacije smo vec dobili u json obliku
public class Day implements Parcelable { // implementa ovo da bi se mogli kompleksniji podaci prenit izmedju aktivnosti
    private long mTime;
    private String mSummary;
    private double mTemperatureMax;
    private String mIcon;
    private String mTimeZone;


    public long getTime() {
        return mTime;
    }

    public void setTime(long time) {
        mTime = time;
    }

    public String getSummary() {
        return mSummary;
    }

    public void setSummary(String summary) {
        mSummary = summary;
    }

    public int getTemperatureMax() {
        return (int) Math.round(mTemperatureMax);
    }

    public void setTemperatureMax(double temperatureMax) {
        mTemperatureMax = temperatureMax;
    }

    public String getIcon() {
        return mIcon;
    }

    public void setIcon(String icon) {
        mIcon = icon;
    }

    public String getTimeZone() {
        return mTimeZone;
    }

    public void setTimeZone(String timeZone) {
        mTimeZone = timeZone;
    }
    public int getIconId(){
        return  Forecast.getIconId(mIcon);
    }
    public String getDayOfTheWeek(){
        SimpleDateFormat formatter = new SimpleDateFormat("EEEE");
        formatter.setTimeZone(TimeZone.getTimeZone(mTimeZone));
        Date dateTime = new Date(mTime*1000);
        return formatter.format(dateTime);
    }

    @Override
    public int describeContents() { // ode ne diramo nista
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) { // wrap up-amo koristene podatke iz klase
        dest.writeLong(mTime);
        dest.writeString(mSummary);
        dest.writeDouble(mTemperatureMax);
        dest.writeString(mIcon);
        dest.writeString(mTimeZone);

    }
    private Day(Parcel in){ // odmotavamo ih istim redoslijedom!!
        mTime=in.readLong();
        mSummary=in.readString();
        mTemperatureMax=in.readDouble();
        mIcon=in.readString();
        mTimeZone=in.readString();
    }

    public  Day(){} // javlja gresku bez ovog
    public static final Creator<Day> CREATOR = new Creator<Day>() { // kreatora je nuzno napisat
        @Override
        public Day createFromParcel(Parcel source) {
            return new Day(source);
        }

        @Override
        public Day[] newArray(int size) {
            return new Day[size];
        }
    };
}
