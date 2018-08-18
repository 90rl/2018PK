package com.amazingteam.competenceproject.util;

import android.os.AsyncTask;
import android.util.Log;

import com.amazingteam.competenceproject.interfaces.WeatherRequestCallback;
import com.amazingteam.competenceproject.model.Place;
import com.amazingteam.competenceproject.model.Weather;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherRequester {

    private static final String TAG = WeatherRequester.class.getSimpleName();
    private static final String OPEN_WEATHER_MAP_URL =
            "http://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&appid=%s&units=metric&lang=pl";
    private static final String OPEN_WEATHER_MAP_API_KEY =
            "dac058e9a2786b4d52e9418c778f1810";

    private WeatherRequestCallback weatherRequestCallback;
    private WeatherTask weatherTask;

    public WeatherRequester(WeatherRequestCallback weatherRequestCallback) {
        this.weatherRequestCallback = weatherRequestCallback;
        weatherTask = new WeatherTask();
    }

    public void getWeatherAsync(String lon, String lat) {
        weatherTask.execute(lon, lat);
    }

    public void stopRequest() {
        if (weatherTask != null) {
            weatherTask.cancel(true);
        }
    }

    private JSONObject getWeatherJSON(String lat, String lon) {
        try {
            URL url = new URL(String.format(
                    OPEN_WEATHER_MAP_URL, lon, lat, OPEN_WEATHER_MAP_API_KEY));
            Log.d(TAG, "URL: " + url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));

            StringBuilder json = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null)
                json.append(line).append("\n");
            reader.close();

            JSONObject data = new JSONObject(json.toString());

            if (data.getInt("cod") != 200) {
                return null;
            }

            return data;
        } catch (Exception e) {
            return null;
        }
    }

    private class WeatherTask extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... strings) {
            return getWeatherJSON(strings[0], strings[1]);
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            if (result == null) {
                weatherRequestCallback.onWeatherDownloadError("Results are null!");
                return;
            }
            try {
                Log.d("WEATHER_REQUESTER", String.valueOf(result));
                Place place = new Place();
                JSONObject coord = result.getJSONObject("coord");
                place.setLat(coord.getDouble("lat"));
                place.setLon(coord.getDouble("lon"));
                place.setName(result.getString("name"));

                Weather weather = new Weather();
                JSONObject weatherJson = result.getJSONArray("weather").getJSONObject(0);
                JSONObject temperature = result.getJSONObject("main");
                JSONObject wind = result.getJSONObject("wind");
                weather.setDescription(weatherJson.getString("description"));
                weather.setIcon(weatherJson.getString("icon"));
                weather.setId(weatherJson.getInt("id"));
                weather.setTemperature(temperature.getInt("temp"));
                weather.setWindSpeed(wind.getDouble("speed"));
                weather.setRainfall(WeatherConverter.getRainfallFromId(weather.getId()));

                place.setWeather(weather);

                weatherRequestCallback.onWeatherDownloadSuccess(place);
            } catch (JSONException e) {
                weatherRequestCallback.onWeatherDownloadError(e.getMessage());
            }
        }
    }
}
