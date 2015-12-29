package com.example.ivanmatas.weahter4u.UI;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.ivanmatas.weahter4u.Adapters.DayAdapter;
import com.example.ivanmatas.weahter4u.Adapters.HourAdapter;
import com.example.ivanmatas.weahter4u.R;
import com.example.ivanmatas.weahter4u.weather.Day;
import com.example.ivanmatas.weahter4u.weather.Hour;

import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HourlyForecastActivity extends AppCompatActivity {
private Hour[] mHours;
    @Bind(R.id.recyclerView) RecyclerView mRecyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hourly_forecast);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        Parcelable[] parcelables= intent.getParcelableArrayExtra(MainActivity.HOURLY_FORECAST);
        mHours = Arrays.copyOf(parcelables, parcelables.length, Hour[].class); // kopiramo podatke iz parcelabela u mDays

        HourAdapter adapter= new HourAdapter(this,mHours);
        mRecyclerView.setAdapter(adapter);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setHasFixedSize(true);
    }
}
