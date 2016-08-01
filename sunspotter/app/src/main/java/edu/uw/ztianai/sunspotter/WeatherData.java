package edu.uw.ztianai.sunspotter;

/**
 * Created by Tianai Zhao on 16/4/10.
 * Create a class calls WeatherData to keep track of each weather forecast information
 */
public class WeatherData {
    private int temperature;
    private String weather;
    private String day;
    private String hour;
    private boolean sunnyDay;

    public WeatherData() {
        temperature = 0;
        weather = "";
        day = "";
        hour ="";
        sunnyDay = false;
    }

    // Returns the weather of the current forecast
    public String getWeather(){
        return weather;
    }

    // Takes in a String format temperature, and rounds it into an integer temperature
    // Set this temperature to be the new temperature
    public void setTemperature(String temperature) {
        Double temp = Double.parseDouble(temperature);
        this.temperature = (int)(temp + 0.5);
    }

    // Takes in a String of weather, and set current weather to this new passed in weather
    public void setWeather(String weather) {
        this.weather = weather;
    }

    // Set the current day with the passed in day
    public void setDay(String day){
        this.day = day;
    }

    // Set the current time with the passed in data
    public void setHour(String hour){
        this.hour = hour;
    }

    // If passed in a true value, then set sunnyDay to true
    // Else set sunnyDay to false
    public void setSunnyDay(boolean sunnyDay){
        this.sunnyDay = sunnyDay;
    }

    // Returns whether there will be sun for a given forecast
    public boolean getSunnyDay (){
        return sunnyDay;
    }

    // Returns the day of the given forecast
    public String getDay(){
        return day;
    }

    // Returns the time of the given forecast
    public String getHour(){
        return hour;
    }

    // Return a String about the forecast data
    // If the weather is clear or few clouds, then there will be sun, otherwise no sun
    public String toString(){
        String sunResult = "";
        if(weather.equals("Clear") || weather.equals("Few Clouds")) {
            sunResult = "SUN!";
        }else{
            sunResult = "No Sun";
        }
        return sunResult + " (" + day + " " + hour + " ) - " + temperature + "\u2109";
    }
}
