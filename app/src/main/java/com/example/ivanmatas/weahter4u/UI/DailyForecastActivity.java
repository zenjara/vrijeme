package com.example.ivanmatas.weahter4u.UI;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ivanmatas.weahter4u.Adapters.DayAdapter;
import com.example.ivanmatas.weahter4u.R;
import com.example.ivanmatas.weahter4u.weather.Day;

import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DailyForecastActivity extends Activity {
    private Day[] mDays;
    @Bind(android.R.id.list) ListView mListView;
    @Bind(android.R.id.empty) TextView mEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_forecast);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        Parcelable[] parcelables= intent.getParcelableArrayExtra(MainActivity.DAILY_FORECAST);
        mDays = Arrays.copyOf(parcelables,parcelables.length,Day[].class); // kopiramo podatke iz parcelabela u mDays

        DayAdapter adapter= new DayAdapter(this,mDays);
        mListView.setAdapter(adapter);
        mListView.setEmptyView(mEmpty);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String dayOfTheWeek=mDays[position].getDayOfTheWeek();
                String conditions = mDays[position].getSummary();
                String highTemp = mDays[position].getTemperatureMax()+"";
                String message= String.format("On %s the high will be %S and it will be %s", dayOfTheWeek,highTemp,conditions);
                Toast.makeText(DailyForecastActivity.this,message,Toast.LENGTH_LONG).show();
            }
        });

        //setListAdapter(adapter);
       /* String[] daysOfTheWeek = { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };
        ArrayAdapter<String> adapter= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,daysOfTheWeek);
        setListAdapter(adapter);*/
          }

   /* @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        String dayOfTheWeek=mDays[position].getDayOfTheWeek();
        String conditions = mDays[position].getSummary();
        String highTemp = mDays[position].getTemperatureMax()+"";
        String message= String.format("On %s the high will be %S and it will be %s", dayOfTheWeek,highTemp,conditions);
        Toast.makeText(this,message,Toast.LENGTH_LONG).show();
    }*/
}
