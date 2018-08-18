package com.amazingteam.competenceproject.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.amazingteam.competenceproject.R;
import com.amazingteam.competenceproject.interfaces.WeatherRequestCallback;
import com.amazingteam.competenceproject.model.Cloth;
import com.amazingteam.competenceproject.model.Meeting;
import com.amazingteam.competenceproject.model.Place;
import com.amazingteam.competenceproject.model.Tag;
import com.amazingteam.competenceproject.ui.StylizationActivityClothesAdapter;
import com.amazingteam.competenceproject.util.DatabaseHelper;
import com.amazingteam.competenceproject.util.WeatherConverter;
import com.amazingteam.competenceproject.util.WeatherRequester;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

import static com.amazingteam.competenceproject.util.Algorithm.getClothesByType;
import static com.amazingteam.competenceproject.util.Algorithm.getWear;
import static com.amazingteam.competenceproject.util.Constants.DESTINATION;
import static com.amazingteam.competenceproject.util.Constants.LAST_KNOWN_LOCATION_LAT;
import static com.amazingteam.competenceproject.util.Constants.LAST_KNOWN_LOCATION_LON;

public class StylizationActivity extends AppCompatActivity implements WeatherRequestCallback, OnSuccessListener<Location> {

    private static final String TAG = StylizationActivity.class.getSimpleName();
    private List<Cloth> clothes;
    private List<Tag> selectedTags;
    private StylizationActivityClothesAdapter stylizationActivityClothesAdapter;
    private AdapterView.OnItemClickListener meetingListener;
    private AdapterView.OnItemLongClickListener meetingListenerLong;
    private ArrayAdapter<Meeting> meetingArrayAdapter;
    private ListView meetingListView;
    private TextView destinationTextView;
    private TextView placeTextView;
    private TextView temperatureTextView;
    private ImageView weatherIcon;
    private List<Meeting> meetingsList;
    private List<Cloth> pickedCloths;
    private List<Tag> weatherTags;
    private Boolean clothesPresent;
    private double currentLocationLat;
    private double currentLocationLon;
    private String destination;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private WeatherRequester weatherRequester;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stylization);
        initResources();

        FloatingActionButton addClothButton = (FloatingActionButton) findViewById(R.id.buttonAddNewMeetings);
        addClothButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StylizationActivity.this, MeetingActivity.class);
                startActivity(intent);
            }
        });

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        setLocationListener();
        weatherRequester = new WeatherRequester(this);

        meetingListView = (ListView) findViewById(R.id.meetingsList);
        meetingArrayAdapter = new ArrayAdapter<>(
                getApplicationContext(),
                R.layout.list_item_meeting,
                R.id.textView,
                meetingsList);
        meetingListView.setDivider(null);
        meetingListView.setAdapter(meetingArrayAdapter);
        meetingListView.setOnItemClickListener(meetingListener);
        meetingListView.setOnItemLongClickListener(meetingListenerLong);

        destinationTextView = (TextView) findViewById(R.id.destination);
        if (savedInstanceState != null) {
            destination = savedInstanceState.getString(DESTINATION);
        } else {
            destination = getString(R.string.choose_your_destination);
        }
        destinationTextView.setText(destination);

        placeTextView = (TextView) findViewById(R.id.place);
        temperatureTextView = (TextView) findViewById(R.id.temperature);
        weatherIcon = (ImageView) findViewById(R.id.weatherIcon);
    }

    @Override
    protected void onPause() {
        super.onPause();
        weatherRequester.stopRequest();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(DESTINATION, destination);
    }

    @Override
    public void onBackPressed() {
        if (meetingListView.getAdapter().equals(stylizationActivityClothesAdapter)) {
            destination = getString(R.string.choose_your_destination);
            destinationTextView.setText(destination);
            meetingArrayAdapter = new ArrayAdapter<>(
                    getApplicationContext(),
                    R.layout.list_item_meeting,
                    R.id.textView,
                    meetingsList);
            meetingListView.setAdapter(meetingArrayAdapter);
            pickedCloths.clear();
            meetingListView.setOnItemClickListener(meetingListener);
            meetingListView.setOnItemLongClickListener(meetingListenerLong);
        } else
            super.onBackPressed();
    }

    @Override
    public void onSuccess(Location location) {
        setCurrentLocation(location);
        weatherRequester.getWeatherAsync(String.valueOf(currentLocationLon), String.valueOf(currentLocationLat));
    }

    @Override
    public void onWeatherDownloadSuccess(Place place) {
        Log.d(TAG, "WEATHER CALLBACK: " + place.getName());
        Log.d(TAG, "WEATHER CALLBACK: " + place.getLat());
        Log.d(TAG, "WEATHER CALLBACK: " + place.getLon());
        Log.d(TAG, "WEATHER CALLBACK: " + place.getWeather().getDescription());
        Log.d(TAG, "WEATHER CALLBACK: " + place.getWeather().getTemperature() + "°C");

        placeTextView.setText(place.getName());
        String temperature = place.getWeather().getTemperature() + "°C";
        temperatureTextView.setText(temperature);
        weatherTags = WeatherConverter.generateTagsFromWeather(place.getWeather(), DatabaseHelper.getInstance(this));
        weatherIcon.setImageDrawable(getDrawable(getResources().getIdentifier(
                WeatherConverter.getDrawableNameFromWeatherIcon(place.getWeather().getIcon()),
                "drawable",
                getPackageName())));
        for (Tag tag : weatherTags) {
            Log.d(TAG, "TAG: " + tag.getName());
        }
    }

    @Override
    public void onWeatherDownloadError(String message) {
        Log.e(TAG, "ERROR: " + message);
        placeTextView.setText(R.string.no_information);
        temperatureTextView.setText(R.string.no_information);
    }

    private void setLocationListener() {
        try {
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(this, this);
        } catch (SecurityException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void setCurrentLocation(Location location) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (location != null) {
            Log.d(TAG, "Latitude: " + location.getLatitude() + "\n Longitude: " + location.getLongitude());
            editor.putFloat(LAST_KNOWN_LOCATION_LAT, (float) location.getLatitude());
            editor.putFloat(LAST_KNOWN_LOCATION_LON, (float) location.getLongitude());
            editor.apply();
        }
        currentLocationLat = sharedPreferences.getFloat(LAST_KNOWN_LOCATION_LAT, 0);
        currentLocationLon = sharedPreferences.getFloat(LAST_KNOWN_LOCATION_LON, 0);
        Log.d(TAG, "Latitude: " + currentLocationLat + "\n Longitude: " + currentLocationLon);
    }

    private void initResources() {
        meetingsList = new ArrayList<>();
        final DatabaseHelper databaseHelper = DatabaseHelper.getInstance(this);
        clothes = databaseHelper.getAllClothes();
        meetingsList = databaseHelper.getAllMeetings();
        selectedTags = new ArrayList<>();
        pickedCloths = new ArrayList<>();
        weatherTags = new ArrayList<>();
        databaseHelper.closeDataBase();
        clothesPresent = false;
        meetingListenerLong = new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int pos, long l) {
                DatabaseHelper databaseHelper1 = DatabaseHelper.getInstance(StylizationActivity.this);
                databaseHelper1.deleteMeeting(meetingsList.get(pos).getId());
                meetingArrayAdapter.remove(meetingsList.get(pos));
                meetingArrayAdapter.notifyDataSetChanged();
                databaseHelper1.closeDataBase();
                return true;
            }
        };
        meetingListener = new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
                selectedTags = meetingsList.get(pos).getTagList();
                if (meetingsList.get(pos).getWeatherDependant()) {
                    if (getClothesByType("winterhat", clothes).size() > 0) {
                        pickedCloths.add(getWear(clothes, "winterhat", weatherTags));
                        clothesPresent = true;
                    } else {
                        AlertDialog.Builder adBuilder = new AlertDialog.Builder(StylizationActivity.this);
                        adBuilder.setMessage("You have no winter hats");
                        adBuilder.create().show();
                    }
                    if (getClothesByType("tshirt", clothes).size() > 0) {
                        pickedCloths.add(getWear(clothes, "tshirt", selectedTags));
                        clothesPresent = true;
                    } else {
                        AlertDialog.Builder adBuilder = new AlertDialog.Builder(StylizationActivity.this);
                        adBuilder.setMessage("You have no T-shirts");
                        adBuilder.create().show();
                    }
                    if (getClothesByType("trousers", clothes).size() > 0) {
                        pickedCloths.add(getWear(clothes, "trousers", selectedTags));
                        clothesPresent = true;
                    } else {
                        AlertDialog.Builder adBuilder = new AlertDialog.Builder(StylizationActivity.this);
                        adBuilder.setMessage("You have no trousers");
                        adBuilder.create().show();
                    }
                    if (getClothesByType("winterboots", clothes).size() > 0) {
                        pickedCloths.add(getWear(clothes, "winterboots", weatherTags));
                        clothesPresent = true;
                    } else {
                        AlertDialog.Builder adBuilder = new AlertDialog.Builder(StylizationActivity.this);
                        adBuilder.setMessage("You have no winter boots");
                        adBuilder.create().show();
                    }
                } else {
                    Log.d("Cloth Set size :", Integer.toString(meetingsList.get(pos).getClothesSet().size()));
                    Log.d("Tag Set size :", Integer.toString(meetingsList.get(pos).getTagList().size()));
                    for (String s : meetingsList.get(pos).getClothesSet()) {
                        Log.d("Meeting cloth :", s);
                        if (getClothesByType(s, clothes).size() > 0) {
                            pickedCloths.add(getWear(clothes, s, selectedTags));
                            clothesPresent = true;
                        } else {
                            AlertDialog.Builder adBuilder = new AlertDialog.Builder(StylizationActivity.this);
                            adBuilder.setMessage("You have no " + s);
                            adBuilder.create().show();
                        }
                    }
                }

                if (clothesPresent) {
                    destination = getString(R.string.your_destination) + " " + meetingsList.get(pos).getName();
                    destinationTextView.setText(destination);
                    stylizationActivityClothesAdapter = new StylizationActivityClothesAdapter(getApplicationContext(), pickedCloths);
                    meetingListView.setAdapter(stylizationActivityClothesAdapter);
                    meetingListView.setOnItemClickListener(null);
                } else {
                    AlertDialog.Builder adBuilder = new AlertDialog.Builder(StylizationActivity.this);
                    adBuilder.setMessage("You aint got clothes");
                    adBuilder.create().show();
                }
            }
        };
    }
}

