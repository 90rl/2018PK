package com.amazingteam.competenceproject.util;

import com.amazingteam.competenceproject.model.Tag;
import com.amazingteam.competenceproject.model.Weather;

import java.util.ArrayList;
import java.util.List;

import static com.amazingteam.competenceproject.util.WeatherConstants.CLEAR;
import static com.amazingteam.competenceproject.util.WeatherConstants.DRIZZLE;
import static com.amazingteam.competenceproject.util.WeatherConstants.RAIN;
import static com.amazingteam.competenceproject.util.WeatherConstants.SNOW;
import static com.amazingteam.competenceproject.util.WeatherConstants.THUNDERSTORM;

public class WeatherConverter {

    public static int getRainfallFromId(int id) {
        int rainfall = id / 100;
        if (rainfall != THUNDERSTORM && rainfall != DRIZZLE
                && rainfall != SNOW && rainfall != RAIN) {
            return CLEAR;
        }
        return rainfall;
    }

    public static String getDrawableNameFromWeatherIcon(String icon) {
        String drawableName;
        switch (icon) {
            case "01d":
            case "01n":
                drawableName = "sun_24";
                break;
            case "02d":
            case "02n":
                drawableName = "partly_cloudy_day_24";
                break;
            case "03d":
            case "03n":
            case "04d":
            case "04n":
                drawableName = "clouds_24";
                break;
            case "09d":
            case "09n":
                drawableName = "rain_24";
                break;
            case "10d":
            case "10n":
                drawableName = "rain_cloud_24";
                break;
            case "11d":
            case "11n":
                drawableName = "storm_24";
                break;
            case "13d":
            case "13n":
                drawableName = "snow_24";
                break;
            default:
                drawableName = "clouds_24";
        }
        return drawableName;
    }

    public static List<Tag> generateTagsFromWeather(Weather weather, DatabaseHelper databaseHelper) {
        List<Tag> weatherTags = new ArrayList<>();
        weatherTags.add(databaseHelper.getTagByName(getTemperatureTagName(weather.getTemperature())));
        weatherTags.add(databaseHelper.getTagByName(getWindTagName(weather.getWindSpeed())));
        weatherTags.add(databaseHelper.getTagByName(getRainTagName(weather.getRainfall())));
        return weatherTags;
    }

    private static String getTemperatureTagName(int temperature) {
        if (temperature <= -5) {
            return "warm";
        } else if (temperature <= 10 && temperature > -5) {
            return "chilly";
        } else {
            return "brief";
        }
    }

    private static String getWindTagName(double windSpeed) {
        if (windSpeed > 22) {
            return "airProtective";
        } else {
            return "airy";
        }
    }

    private static String getRainTagName(double rainFall) {
        if (rainFall == SNOW) {
            return "snowProtective";
        } else if (rainFall == RAIN || rainFall == THUNDERSTORM) {
            return "rainproof";
        } else if (rainFall == DRIZZLE) {
            return "showerproof";
        } else {
            return "knitted";
        }
    }
}
