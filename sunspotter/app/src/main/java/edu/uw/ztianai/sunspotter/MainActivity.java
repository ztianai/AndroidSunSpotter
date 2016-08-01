package edu.uw.ztianai.sunspotter;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Tianai Zhao on 16/4/10.
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Creates the list view that will contain forecast data
        adapter = new ArrayAdapter<String>(this, R.layout.list_item, R.id.txtItem, new ArrayList<String>());
        ListView listView = (ListView)findViewById(R.id.listView);
        listView.setAdapter(adapter);
    }

    // Creates a background threading that downloads weather forecast
    public class WeatherDownloadTask extends AsyncTask<String, Void, ArrayList<WeatherData>> {

        @Override
        protected ArrayList<WeatherData> doInBackground(String... params) {
            String city = params[0];   // This is the city name that user types in
            String data = DownloadWeather.downloadWeatherData(city); //Fetch data from OpenWeatherMap API based user's search
            ArrayList<WeatherData> results = parseForecastJSONData(data); //Parse the data into ArrayList of WeatherData
            return results;

        }

        @Override
        protected void onPostExecute(ArrayList<WeatherData> weatherData) {
            super.onPostExecute(weatherData);

            adapter.clear(); // Clear the current list view and prepares for the new API results
            String firstSunnyDay = ""; // A string to keep track of the day and time of the first sunny day
            boolean isFirst = false; // First sunny day
            boolean isSunny = false; // Weather these is sunny day
            String weather = null; // The weather of the current forecast data
            for(WeatherData data: weatherData){ // Read through all the forecast data
                if(data.getSunnyDay() && !isFirst){ // If the current data has sun and is the first sunny day
                    firstSunnyDay = data.getDay() + " at " + data.getHour();
                    isFirst = true;
                    isSunny = true;
                    weather = data.getWeather();
                }
                adapter.add(data.toString()); //Populates the new information

            }

            // Updates the application with the most recent information
            ImageView imgView = (ImageView)findViewById(R.id.weatherPic); // Weather picture
            TextView sunnyResult = (TextView)findViewById(R.id.txtWeatherHeader); // Overall sun report
            TextView sunnyTime = (TextView)findViewById(R.id.txtWeatherDescr); // Time and day of the sun
            if(isSunny){
                sunnyTime.setText("It will be sunny on " + firstSunnyDay);
                sunnyResult.setText(R.string.sun);
                if(weather.equals("Clear")){
                    imgView.setImageResource(R.drawable.sunny);
                }else{

                    imgView.setImageResource(R.drawable.few_clouds);
                }

            }else{
                imgView.setImageResource(R.drawable.rainy);
                sunnyResult.setText(R.string.no_sun);
                sunnyTime.setText("There won't be any sun in the next 5 days.");
            }
        }
    }

    // Handle the search button, once clicked, it will start the fetching data process
    public void handleClick(View v) {
        EditText text = (EditText)findViewById(R.id.txtSearch);
        String searchTerm = text.getText().toString();
        WeatherDownloadTask task = new WeatherDownloadTask();
        task.execute(searchTerm);
        ViewStub stub = (ViewStub)findViewById(R.id.stub);
        if(stub != null) {
            stub.inflate();
        }
    }

    // Parse forecast json data into readable data and store data in an ArrayList
    public ArrayList<WeatherData> parseForecastJSONData(String json) {
        ArrayList<WeatherData> forecast = new ArrayList<WeatherData>(); //empty list to return

        try {
            // Starts parsing the JSON file
            JSONObject forecastObject = new JSONObject(json);

            // Pulls the forecast data
            JSONArray forecastArray = forecastObject.getJSONArray("list");

            // Loop through all the data
            for (int i = 0; i < forecastArray.length(); i++) {
                WeatherData forecastData = new WeatherData();
                JSONObject forecastArrayObj = forecastArray.getJSONObject(i);

                // Grabs the time of the forecast
                String dt = forecastArrayObj.getString("dt");
                Long time = Long.valueOf(dt) * 1000;
                Date date = new Date(time);
                SimpleDateFormat day = new SimpleDateFormat("EEE");
                SimpleDateFormat hour = new SimpleDateFormat("h:mm aa");
                forecastData.setDay(day.format(date));
                forecastData.setHour(hour.format(date));

                // Grabs the temperature from the forecast
                JSONObject mainTempObject = forecastArrayObj.getJSONObject("main");
                String temp = mainTempObject.getString("temp");
                forecastData.setTemperature(temp);

                // Grabs the current weather and weather description from the forecast
                JSONArray weatherArray = forecastArrayObj.getJSONArray("weather");
                JSONObject weatherObject = weatherArray.getJSONObject(0);
                String sunOrRain = weatherObject.getString("main");
                forecastData.setWeather(sunOrRain);
                String fewClouds = weatherObject.getString("description");

                // Checks to see if there will be sun
                // There will be sun if the weather is clear or the weather description is few clouds
                if (sunOrRain.equals("Clear")) {
                    forecastData.setSunnyDay(true);
                }else if(fewClouds.equals("few clouds")){
                    forecastData.setSunnyDay(true);
                    forecastData.setWeather("Few Clouds");
                }

                // Adds the current forecast data to the overall ArrayList of forecast data
                forecast.add(forecastData);
            }
        }catch (JSONException e){
            Log.v(TAG, "JSONException error");
            return null;
        }
        return forecast;
    }
}
