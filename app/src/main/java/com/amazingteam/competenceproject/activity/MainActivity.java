package com.amazingteam.competenceproject.activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.amazingteam.competenceproject.R;
import com.amazingteam.competenceproject.model.Meeting;
import com.amazingteam.competenceproject.model.Tag;
import com.amazingteam.competenceproject.model.Template;
import com.amazingteam.competenceproject.util.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

import static com.amazingteam.competenceproject.util.Constants.FIRST_START;
import static com.amazingteam.competenceproject.util.Constants.LAST_KNOWN_LOCATION_LAT;
import static com.amazingteam.competenceproject.util.Constants.LAST_KNOWN_LOCATION_LON;
import static com.amazingteam.competenceproject.util.Constants.PERMISSION_RESULT;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkCameraPermission() && checkWritePermission()
                    && checkLocationPermission()) {
                Log.d(TAG, "Permissions granted");
            } else {
                if (shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA)) {
                    Toast.makeText(this,
                            "Application required access to camera", Toast.LENGTH_SHORT).show();
                }
                requestPermissions(new String[]{
                        android.Manifest.permission.CAMERA,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_RESULT);
            }
        }

        if (isItFirstRun()) {
            Log.d(TAG, "First run.");

            DatabaseHelper databaseHelper = DatabaseHelper.getInstance(this);
            databaseHelper.dropAndCreateDatabase();
            initializeTags(databaseHelper);
            initializeTemplates(databaseHelper);
            initializeMeetings(databaseHelper);
            addDefaultLocation();
            databaseHelper.closeDataBase();
        } else {
            Log.d(TAG, "No first run");
        }
    }

    private void addDefaultLocation() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(LAST_KNOWN_LOCATION_LAT, 51.86563f);
        editor.putFloat(LAST_KNOWN_LOCATION_LON, 19.46667f);
        editor.apply();
    }


    public void click(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.buttonDressMeUp:
                intent = new Intent(MainActivity.this, StylizationActivity.class);
                startActivity(intent);
                break;
            case R.id.buttonWardrobe:
                intent = new Intent(MainActivity.this, WardrobeActivity.class);
                startActivity(intent);
                break;
        }
    }

    private boolean isItFirstRun() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (!prefs.getBoolean(FIRST_START, false)) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(FIRST_START, true);
            editor.apply();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_RESULT:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Application will not run without camera services", Toast.LENGTH_SHORT).show();
                } else if (grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Application will not run without access to external storage", Toast.LENGTH_SHORT).show();
                } else if (grantResults[2] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Application will not run without location services", Toast.LENGTH_SHORT).show();
                } else {
                    break;
                }
                finish();
                break;
        }
    }

    private boolean checkCameraPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean checkWritePermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean checkLocationPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void initializeTags(DatabaseHelper databaseHelper) {
        databaseHelper.createTag(new Tag("black", 1, "colour"));
        databaseHelper.createTag(new Tag("gray", 1, "colour"));
        databaseHelper.createTag(new Tag("white", 1, "colour"));
        databaseHelper.createTag(new Tag("pink", 1, "colour"));
        databaseHelper.createTag(new Tag("red", 1, "colour"));
        databaseHelper.createTag(new Tag("orange", 1, "colour"));
        databaseHelper.createTag(new Tag("lightBrown", 1, "colour"));
        databaseHelper.createTag(new Tag("brown", 1, "colour"));
        databaseHelper.createTag(new Tag("yellow", 1, "colour"));
        databaseHelper.createTag(new Tag("cyan", 1, "colour"));
        databaseHelper.createTag(new Tag("lightGreen", 1, "colour"));
        databaseHelper.createTag(new Tag("green", 1, "colour"));
        databaseHelper.createTag(new Tag("lightBlue", 1, "colour"));
        databaseHelper.createTag(new Tag("navyBlue", 1, "colour"));
        databaseHelper.createTag(new Tag("blue", 1, "colour"));
        databaseHelper.createTag(new Tag("violet", 1, "colour"));

        databaseHelper.createTag(new Tag("moro", 1, "pattern"));
        databaseHelper.createTag(new Tag("abstract", 1, "pattern"));
        databaseHelper.createTag(new Tag("striped", 1, "pattern"));
        databaseHelper.createTag(new Tag("checkered", 1, "pattern"));
        databaseHelper.createTag(new Tag("dotted", 1, "pattern"));

        databaseHelper.createTag(new Tag("formal", 1, "destination"));
        databaseHelper.createTag(new Tag("informal", 1, "destination"));
        databaseHelper.createTag(new Tag("sports", 1, "destination"));
        databaseHelper.createTag(new Tag("casual", 1, "destination"));
        databaseHelper.createTag(new Tag("slim", 1, "destination"));
        databaseHelper.createTag(new Tag("straight", 1, "destination"));
        databaseHelper.createTag(new Tag("practical", 1, "destination"));
        databaseHelper.createTag(new Tag("capacious", 1, "destination"));
        databaseHelper.createTag(new Tag("skinny", 1, "destination"));

        databaseHelper.createTag(new Tag("warm", 1, "weather"));
        databaseHelper.createTag(new Tag("chilly", 1, "weather"));
        databaseHelper.createTag(new Tag("brief", 1, "weather"));
        databaseHelper.createTag(new Tag("airProtective", 1, "weather"));
        databaseHelper.createTag(new Tag("airy", 1, "weather"));
        databaseHelper.createTag(new Tag("snowProtective", 1, "weather"));
        databaseHelper.createTag(new Tag("rainproof", 1, "weather"));
        databaseHelper.createTag(new Tag("showerproof", 1, "weather"));
        databaseHelper.createTag(new Tag("knitted", 1, "weather"));
    }

    private void initializeTemplates(DatabaseHelper databaseHelper) {
        databaseHelper.createTemplate(new Template("tshirt", R.drawable.clothtemp_tshirt_1, R.drawable.tshirt_1_icon));
        databaseHelper.createTemplate(new Template("trousers", R.drawable.clothtemp_trousers_1, R.drawable.trousers_1_icon));
        databaseHelper.createTemplate(new Template("winterboots", R.drawable.clothtemp_winterboots_1, R.drawable.winterboots_1_icon));
        databaseHelper.createTemplate(new Template("winterhat", R.drawable.clothtemp_winterhat_1, R.drawable.winterhat_1_icon));
        databaseHelper.createTemplate(new Template("shirt", R.drawable.clothtemp_shirt_1, R.drawable.shirt_1_icon));
        databaseHelper.createTemplate(new Template("shorttrousers", R.drawable.clothtemp_shorttrousers_1, R.drawable.shorttrousers_1_icon));
    }

    private void initializeMeetings(DatabaseHelper databaseHelper) {
        List<String> set1 = new ArrayList<>();
        set1.add("tshirt"); set1.add("trousers"); set1.add("winterboots");
        List<Tag> set1Tags = new ArrayList<>();
        set1Tags.add(new Tag("informal", 1, "destination"));
        set1Tags.add(new Tag("straight", 1, "destination"));
        set1Tags.add(new Tag("abstract", 1, "pattern"));
        set1Tags.add(new Tag("white", 1, "colour"));
        databaseHelper.createMeeting(new Meeting("cinema", set1Tags, false, set1));

        List<String> set2 = new ArrayList<>();
        set2.add("shirt"); set2.add("trousers"); set2.add("winterboots");
        List<Tag> set2Tags = new ArrayList<>();
        set2Tags.add(new Tag("formal", 1, "destination"));
        set2Tags.add(new Tag("casual", 1, "destination"));
        set2Tags.add(new Tag("capacious", 1, "destination"));
        set2Tags.add(new Tag("blue", 1, "colour"));

        databaseHelper.createMeeting(new Meeting("theathe", set2Tags, false, set2 ));

        List<String> set3 = new ArrayList<>();
        set3.add("tshirt"); set3.add("winterboots");set3.add("shorttrousers");
        List<Tag> set3Tags = new ArrayList<>();
        set3Tags.add(new Tag("informal", 1, "destination"));
        set3Tags.add(new Tag("casual", 1, "destination"));
        set3Tags.add(new Tag("sports", 1, "destination"));
        set3Tags.add(new Tag("abstract", 1, "pattern"));
        set3Tags.add(new Tag("moro", 1, "pattern"));

        databaseHelper.createMeeting(new Meeting("gym", set3Tags, false, set3 ));

        List<String> set4 = new ArrayList<>();
        set4.add("shirt"); set4.add("trousers"); set4.add("winterboots");
        List<Tag> set4Tags = new ArrayList<>();
        set4Tags.add(new Tag("formal", 1, "destination"));
        set4Tags.add(new Tag("straight", 1, "destination"));
        set4Tags.add(new Tag("slim", 1, "destination"));
        set4Tags.add(new Tag("black", 1, "colour"));
        set4Tags.add(new Tag("gray", 1, "colour"));
        set4Tags.add(new Tag("white", 1, "colour"));

        databaseHelper.createMeeting(new Meeting("church", set4Tags, false, set4 ));

        List<String> set5 = new ArrayList<>();
        set5.add("shirt"); set5.add("trousers"); set5.add("winterboots");
        List<Tag> set5Tags = new ArrayList<>();
        set5Tags.add(new Tag("informal", 1, "destination"));
        set5Tags.add(new Tag("slim", 1, "destination"));
        set5Tags.add(new Tag("practical", 1, "destination"));
        set5Tags.add(new Tag("checkered", 1, "pattern"));
        set5Tags.add(new Tag("blue", 1, "colour"));

        databaseHelper.createMeeting(new Meeting("work", set5Tags, false, set5 ));

        List<String> set6 = new ArrayList<>();
        set6.add("tshirt"); set6.add("trousers"); set6.add("winterboots");set6.add("winterhat");
        List<Tag> set6Tags = new ArrayList<>();
        set6Tags.add(new Tag("informal", 1, "destination"));
        set6Tags.add(new Tag("casual", 1, "destination"));
        set6Tags.add(new Tag("skinny", 1, "destination"));
        set5Tags.add(new Tag("abstract", 1, "pattern"));

        databaseHelper.createMeeting(new Meeting("school", set6Tags, false, set6 ));

        List<String> set7 = new ArrayList<>();
        set7.add("tshirt"); set7.add("trousers"); set7.add("winterboots");set7.add("winterhat");
        List<Tag> set7Tags = new ArrayList<>();
        set7Tags.add(new Tag("informal", 1, "destination"));
        set7Tags.add(new Tag("casual", 1, "destination"));
        set7Tags.add(new Tag("capacious", 1, "destination"));
        set3Tags.add(new Tag("abstract", 1, "pattern"));
        set3Tags.add(new Tag("moro", 1, "pattern"));

        databaseHelper.createMeeting(new Meeting("camping", set7Tags, false, set7 ));
    }

}

