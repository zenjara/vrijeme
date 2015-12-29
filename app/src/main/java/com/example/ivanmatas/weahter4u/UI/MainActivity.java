    package com.example.ivanmatas.weahter4u.UI;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ivanmatas.weahter4u.R;
import com.example.ivanmatas.weahter4u.weather.Current;
import com.example.ivanmatas.weahter4u.weather.Day;
import com.example.ivanmatas.weahter4u.weather.Forecast;
import com.example.ivanmatas.weahter4u.weather.Hour;
import com.google.android.gms.common.api.GoogleApiClient;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

    public class MainActivity extends AppCompatActivity  {
        public static final String TAG = MainActivity.class.getSimpleName();
        public static final String DAILY_FORECAST="DAILY_FORECAST";
        public static final String HOURLY_FORECAST ="HOURLY_FORECAST" ;
        private GoogleApiClient mGoogleApiClient;
      private Location mLastLocation;
      private double mLatitude;
      private double mLongitude;


      private Forecast mForecast;


        @Bind(R.id.timeLabel) TextView mTimeLabel;
      @Bind (R.id.locationLabel) TextView mLocationLabel;
        @Bind(R.id.temperatureLabel) TextView mTemperatureLabel;
        @Bind(R.id.humidityValue) TextView mHumidityValue;
        @Bind(R.id.precipValue) TextView mPrecipValue;
        @Bind(R.id.summaryLabel) TextView mSummaryLabel;
        @Bind(R.id.iconImageView) ImageView mIconImageView;
        @Bind(R.id.refreshImageView) ImageView mRefreshImageView;
        @Bind(R.id.progressBar) ProgressBar mProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        mProgressBar.setVisibility(View.INVISIBLE);

        final double latitude=43.5155;
       final double longitude=16.4778;

        mRefreshImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getForecast(latitude, longitude);
            }
        });
        getForecast(latitude,longitude);
        //getForecast(latitude, longitude);
   }



        private void getForecast(double latitude,double longitude) {


            String apiKey="aa475f1a3afca5c077bedf631cc6dd92";
            mLocationLabel.setText("Split,Croatia");

            String forecastUrl = "https://api.forecast.io/forecast/"+apiKey+"/"+latitude+","+longitude + "?units=si";

            if (isNetworkAvailable()) { // Requestanje podataka s forecast api-em , preko biblioteke okttp

                toggleRefresh();
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(forecastUrl).build();
                Call call = client.newCall(request);
                call.enqueue(new Callback() { //obrađivanje podataka se vrsi u pozadinskom threadu
                    @Override
                    public void onFailure(Request request, IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                toggleRefresh();
                            }
                        });
                        alertUserAboutError();
                    }

                    @Override
                    public void onResponse(Response response) throws IOException {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                toggleRefresh();
                            }
                        });

                        try {
                            String jsonData = response.body().string();
                            Log.v(TAG, jsonData);
                            if (response.isSuccessful()) {
                                mForecast=parseForecastDetails(jsonData); //koristimo sad Forecast jer je to logicki skup dana,sati i trenutnog vremena
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        updateDisplay();
                                    }
                                });

                            } else {
                                alertUserAboutError();
                            }
                        } catch (IOException e) {
                            Log.e(TAG, "Exepction caught: ", e);
                        } catch (JSONException e) {
                            Log.e(TAG, "Exepction caught: ", e);
                        }
                    }
                });
            }
        }

        private void toggleRefresh() {
            if (mProgressBar.getVisibility()==View.INVISIBLE){
                mProgressBar.setVisibility(View.VISIBLE);
                mRefreshImageView.setVisibility(View.INVISIBLE);
            }else{
                mProgressBar.setVisibility(View.INVISIBLE);
                mRefreshImageView.setVisibility(View.VISIBLE);
            }

        }

        private void updateDisplay() {
            Current current = mForecast.getCurrent(); // koritimo current al preko forecast-a, jer je current sad dio vece cjeline, nije sam

            mTemperatureLabel.setText(current.getTemperature()+"");
            mTimeLabel.setText("At "+ current.getFormattedTime()+ " it will be");
            mHumidityValue.setText(current.getHumidity()+"");
            mPrecipValue.setText(current.getPrecipChance() + "%");
            mSummaryLabel.setText(current.getSummary());
            Drawable drawable = ContextCompat.getDrawable(this, current.getIconId());
            mIconImageView.setImageDrawable(drawable);
        }


      private Forecast parseForecastDetails(String jsonData) throws JSONException {
          Forecast forecast = new Forecast(); // instaciramo novi forecast objekt

          forecast.setCurrent(getCurrentDetails(jsonData)); // postavljamo trenutno vrijeme pozivajuci pocetnu metodu s jsonData kao parametrom
          forecast.setHourlyForecast(getHourlyForecast(jsonData));
          forecast.setDailyForecast(getDailyForecast(jsonData));

          return forecast;
      }

      private Day[] getDailyForecast(String jsonData) throws JSONException {
          JSONObject forecast = new JSONObject(jsonData); // instanciranje json objekta na temelju primljenih json podataka
          String timezone = forecast.getString("timezone");
          JSONObject daily = forecast.getJSONObject("daily");
          JSONArray data = daily.getJSONArray("data");

          Day[] days = new Day[data.length()];
          for(int i=0;i<data.length();i++){
              JSONObject jsonDay = data.getJSONObject(i);
              Day day = new Day();
              day.setSummary(jsonDay.getString("summary"));
              day.setTemperatureMax(jsonDay.getDouble("temperatureMax"));
              day.setTime(jsonDay.getLong("time"));
              day.setIcon(jsonDay.getString("icon"));
              day.setTimeZone(timezone);

              days[i]= day;
          }
          return days;
      }

      private Hour[] getHourlyForecast(String jsonData) throws JSONException {
          JSONObject forecast = new JSONObject(jsonData); // instanciranje json objekta na temelju primljenih json podataka
          String timezone = forecast.getString("timezone");
          JSONObject hourly = forecast.getJSONObject("hourly");
          JSONArray data = hourly.getJSONArray("data"); // dohvacamo array unutar objekta hourly

          Hour[] hours = new Hour[data.length()]; // otvaramo novi array za spremanje sati
          for(int i=0;i<data.length();i++){
              JSONObject jsonHour = data.getJSONObject(i); // dohvacamo jsonobjekt na prvom mjestu i pridruzujemo ga jsonHour-u
              Hour hour = new Hour(); // instanciramo novi hour objekt
              hour.setSummary(jsonHour.getString("summary")); // postavljamo vrijednosti objektu preko jsonHour objekta za danu poziciju i
              hour.setTemperature(jsonHour.getDouble("temperature"));
              hour.setTime(jsonHour.getLong("time"));
              hour.setIcon(jsonHour.getString("icon"));
              hour.setTimeZone(timezone);

              hours[i]= hour;
          }
          return hours;
      }


      private Current getCurrentDetails(String jsonData) throws JSONException{
            JSONObject forecast = new JSONObject(jsonData); // instanciranje json objekta na temelju primljenih json podataka
            String timezone = forecast.getString("timezone");
            Log.i(TAG,"From JSON :"+ timezone);
            JSONObject currently = forecast.getJSONObject("currently");// dohvacanje objekta unutar objekta
            Current current = new Current(); // instanciranje cweather objekta

            current.setIcon(currently.getString("icon"));
            current.setSummary(currently.getString("summary"));
            current.setTemperature(currently.getDouble("temperature"));
            current.setTime(currently.getLong("time"));
            current.setHumidity(currently.getDouble("humidity"));
            current.setPrecipChance(currently.getDouble("precipProbability"));
            current.setTimeZone(timezone);

            Log.d(TAG, current.getFormattedTime());

            return current;
        }

        //funkcija koja provjerava da li postoji mreza i jel povezano na mrezu
        private boolean isNetworkAvailable() {
            ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();
            boolean isAvailable=false;
            if (networkInfo!=null && networkInfo.isConnected()){
                isAvailable=true;
            }else{
                Toast.makeText(this,"network is not available",Toast.LENGTH_LONG).show();
            }
            return  isAvailable;
        }

        private void alertUserAboutError() { // instanciranje alertdialogfragmenta i prikaz
            AlertDialogFragment dialog = new AlertDialogFragment();
            dialog.show(getFragmentManager(), "Error_dialog");
        }

        @OnClick (R.id.dailyButton)
        public void startDailyActivity(View view){
            Intent intent = new Intent(this,DailyForecastActivity.class);
            intent.putExtra(DAILY_FORECAST,mForecast.getDailyForecast());
            startActivity(intent);

        }
        @OnClick (R.id.hourlyButton)
        public void startHourlyActivity(View view){
            Intent intent = new Intent(this,HourlyForecastActivity.class);
            intent.putExtra(HOURLY_FORECAST,mForecast.getHourlyForecast());

            startActivity(intent);
        }
      }
