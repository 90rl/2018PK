package com.amazingteam.competenceproject.interfaces;

import com.amazingteam.competenceproject.model.Place;

public interface WeatherRequestCallback {

    void onWeatherDownloadSuccess(Place place);

    void onWeatherDownloadError(String message);
}
