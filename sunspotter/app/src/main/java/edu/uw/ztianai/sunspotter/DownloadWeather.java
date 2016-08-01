package edu.uw.ztianai.sunspotter;

import android.net.Uri;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Tianai Zhao on 16/4/10.
 * Handles downloading weather data from OPENWEATHERMAP API
 */
public class DownloadWeather {

    //Takes in the city input from user, and returns the json string format of the forecast data
    public static String downloadWeatherData(String input) {
        HttpURLConnection urlConnection = null; // Set up a URLConnection with supprot for HTTP-sepecific features
        BufferedReader reader = null; // Set up a Buffered Reader to read the data

        String weatherResult = null; // Final String format for all the data downloaded

        String urlString = ""; // The url link for fetching data

        try {
            // Constructs the URL for the Open Weather Map API to fetch data, including city name, API key, format, units information
            Uri buildString = Uri.parse("http://api.openweathermap.org/data/2.5/forecast")
                    .buildUpon()
                    .appendQueryParameter("q", input)
                    .appendQueryParameter("appid", BuildConfig.OPEN_WEATHER_MAP_API_KEY)
                    .appendQueryParameter("format", "json")
                    .appendQueryParameter("units", "imperial")
                    .build();
            urlString = buildString.toString();

            URL url = new URL(urlString);

            // Starts the connection and sends request to get data from Open Weather Map API
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Grabs the data and start processing the data
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer(); // Creates a StringBuffer to collect all data
            if(inputStream == null){
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line = reader.readLine(); // Read a line of text
            while(line != null) {
                buffer.append(line + "\n"); // Append new data to the buffer
                line = reader.readLine();
            }

            if(buffer.length() == 0){
                return null;
            }
            weatherResult = buffer.toString(); // Change the string buffer to a String format


        }catch(IOException e){
            return null;
        }
        finally { //executes when the try block exits
            if (urlConnection != null) { // Disconnect the url connection
                urlConnection.disconnect();
            }
            if (reader != null) { //Close the buffer reader
                try {
                    reader.close();
                }
                catch (IOException e) {
                }
            }
        }

        return weatherResult; // Returns the downloaded weather forecast data

    }
}
