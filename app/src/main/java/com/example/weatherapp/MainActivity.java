package com.example.weatherapp;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

public class MainActivity extends AppCompatActivity {
private RelativeLayout homeRL;
private ProgressBar loadingPB;
private TextView cityNameTV,temperatureTv,conditionTV;
private RecyclerView weatherRv;
private TextInputEditText cityEdit;
private ImageView backIV,iconIV,searchIV;
private ArrayList<WeatherModel> weatherModelArrayList;
private WeatherAdapter weatherAdapter;
private LocationManager locationManager;
private int PERMISSION_CODE=1;
private String cityName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        homeRL=findViewById(R.id.relativelayout);
        loadingPB=findViewById(R.id.progressbar);
        cityNameTV=findViewById(R.id.textview1);
        temperatureTv=findViewById(R.id.temperature);
        conditionTV=findViewById(R.id.condition);
        weatherRv=findViewById(R.id.recycler);
        cityEdit=findViewById(R.id.inputedittext);
        backIV=findViewById(R.id.imageview1);
        iconIV=findViewById(R.id.image2);
        searchIV=findViewById(R.id.search);
        weatherModelArrayList=new ArrayList<>();
        weatherAdapter=new WeatherAdapter(this,weatherModelArrayList);
        weatherRv.setAdapter(weatherAdapter);
//        locationManager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED
//                && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
//                ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},PERMISSION_CODE);
//        }
//        Location location=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//        if (location != null) { // Add null check here
//            cityName = getCityName(location.getLongitude(), location.getLatitude());
//            getWeatherInfo(cityName);
//        } else {
//            // Handle the case where location is null, maybe by showing a message to the user or using default coordinates
//            Toast.makeText(this, "Location is null", Toast.LENGTH_SHORT).show();
//        }
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_CODE);
        } else {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location != null) {
                cityName = getCityName(location.getLongitude(), location.getLatitude());
                getWeatherInfo(cityName);
            } else {
                Toast.makeText(this, "Location is null", Toast.LENGTH_SHORT).show();
            }
        }

        searchIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city=cityEdit.getText().toString();
                if (city.isEmpty()){
                    Toast.makeText(MainActivity.this, "Please Enter City Name", Toast.LENGTH_SHORT).show();
                }else {
                    cityNameTV.setText(cityName);
                    getWeatherInfo(city);
                }
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==PERMISSION_CODE){
            if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission Granted ", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, "Please provide the permission", Toast.LENGTH_SHORT).show();
            finish();
            }
        }
    }

    private String getCityName(double longitude, double latitude){
        String cityName="Not Found";
        Geocoder geocoder=new Geocoder(getBaseContext(), Locale.getDefault());
        try {
            List<Address> addresses=geocoder.getFromLocation(longitude,latitude,10);
            for (Address adr:addresses){
                if (adr!=null){
                    String city=adr.getLocality();
                    if (city!=null && !city.equals("")){
                        cityName=city;
                    }else {
                        Log.d("TAG","CITY NOT FOUND ");
                        Toast.makeText(this, "User City Not Found", Toast.LENGTH_SHORT).show();
                    }
                }
            }


        }catch (IOException e){
            e.printStackTrace();
        }
        return cityName;
    }
    private void getWeatherInfo(String cityName){
//        String url="http://api.weatherapi.com/v1/forecast.json?key=c66c35af28ab4e1f88a91141242603&q="+cityName+"&days=1&aqi=yes&alerts=yes";
        String url="http://api.weatherapi.com/v1/forecast.json?key=c66c35af28ab4e1f88a91141242603&q="+cityName+"&days=1&aqi=yes&alerts=yes";
        cityNameTV.setText(cityName);
        RequestQueue requestQueue=Volley.newRequestQueue(MainActivity.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        loadingPB.setVisibility(View.GONE);
                        homeRL.setVisibility(View.VISIBLE);
                        weatherModelArrayList.clear();
                        try {


                            String temperature = response.getJSONObject("current").getString("temp_c");
                            temperatureTv.setText(temperature+"°c");
                            int isDay=response.getJSONObject("current").getInt("is_day");
                            String condition=response.getJSONObject("current").getJSONObject("condition").getString("text");
                            String conditionIcon=response.getJSONObject("current").getJSONObject("condition").getString("icon");
                            Picasso.get().load("http".concat(conditionIcon)).into(iconIV);
                            conditionTV.setText(condition);
                            if (isDay==1){
                                //morning
                                Picasso.get().load("https://c.wallhere.com/photos/cd/1c/morning_light_sky_sun_sunlight_nature_colors_weather-536289.jpg!d").into(backIV);
                            }else {
                                //night
                                Picasso.get().load("https://th.bing.com/th/id/OIP.wQrp4QsbaETLM3qpPNvCSQHaEK?rs=1&pid=ImgDetMain").into(backIV);
                            }

                            JSONObject foreastObj=response.getJSONObject("forecast");
                            JSONObject forecastO=foreastObj.getJSONArray("forecastday").getJSONObject(0);
                            JSONArray hourArray=forecastO.getJSONArray("hour");
                            for (int i=0;i<hourArray.length();i++){
                                JSONObject hourObj=hourArray.getJSONObject(i);
                                String time=hourObj.getString("time");
                                String temper=hourObj.getString("temp_c");
                                String img=hourObj.getJSONObject("condition").getString("icon");
                                String wind=hourObj.getString("wind_kph");
                                weatherModelArrayList.add(new WeatherModel(time,temper,img,wind));
                            }
                            weatherAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                          e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Log the error for debugging
                        Log.e("API_ERROR", "Error in API request: " + error.getMessage());

                        // Check if the error is due to network issues
                        if (error.networkResponse == null) {
                            Toast.makeText(MainActivity.this, "Network error. Please check your internet connection.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Check if the error is due to an invalid city name (HTTP status code 404)
                        if (error.networkResponse.statusCode == 404) {
                            Toast.makeText(MainActivity.this, "City not found. Please enter a valid city name.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Handle other types of errors
                        Toast.makeText(MainActivity.this, "An error occurred. Please try again later.", Toast.LENGTH_SHORT).show();
                    }
                });
        requestQueue.add(jsonObjectRequest);
    }
}