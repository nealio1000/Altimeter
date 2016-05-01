package ventureindustries.altimeter;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Set;

import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.LatLon;


public class MyAltimeter extends AppCompatActivity {
    private Button startButton;
    private Button stopButton;
    private Button getDataButton;
    private Button heartRateButton;
    private TextView mAltimeter;
    private TextView readyText;
    private TextView pressureText;
    private static final String DEBUG_TAG = "DEBUGGER MESSAGE:   ";
    private SensorEventListener mBarometerEventListener;
    private SensorManager mSensorManager;
    private Sensor mBarometerSensor;
    public float currentSeaLevelPressure = 0;
    public ArrayList<Float> elevations;
    private boolean recordElevationFlag = false;
    private boolean pressureIsCurrent = false;
    private Intent graphIntent;
    private Intent heartRateIntent;
    private Intent settingsIntent;
    private float unit = 3.28084f;
//    private EGM96 egm96;
    private boolean isAltitudeModeGps = false;
    private static final String dataFilePath = "WW15MGH.DAC";


    private LocationManager locationManager;
    private Location startLocation = null;
    private Location lastKnown = null;
    private LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_altimeter);
        appInit();
        startButtonListeners();
        initializeBarometer();
        initializeGps();

    }

    public void appInit() {

//        try {
//            egm96 = new EGM96("/storage/emulated/0/Download/WW15MGH.DAC");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        graphIntent = new Intent(this, GraphActivity.class);
        heartRateIntent = new Intent(this, HeartRateActivity.class);
        settingsIntent = new Intent(this, SettingsActivity.class);

        startButton = (Button) findViewById(R.id.start_button);
        stopButton = (Button) findViewById(R.id.stop_button);
        getDataButton = (Button) findViewById(R.id.get_data_button);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAltimeter = (TextView) findViewById(R.id.altitudeView);
        heartRateButton = (Button) findViewById(R.id.heart_rate_button);
        readyText = (TextView) findViewById(R.id.ready_text);
        pressureText = (TextView) findViewById(R.id.pressure_text);
        startButton.setEnabled(false);
        stopButton.setEnabled(false);
        elevations = new ArrayList<>();
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
    }

    public void startButtonListeners() {
        getDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPressureData();
            }
        });

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                elevations.clear();
                startButton.setEnabled(false);
                stopButton.setEnabled(true);
                heartRateButton.setEnabled(false);
                recordElevationFlag = true;
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startButton.setEnabled(true);
                stopButton.setEnabled(false);
                recordElevationFlag = false;
                heartRateButton.setEnabled(true);

                float[] data = new float[elevations.size()];
                int i = 0;
                for (Float f : elevations) {
                    data[i++] = (f != null ? f : Float.NaN);
                }

                graphIntent.putExtra("altitudes", data);
                startActivity(graphIntent);
            }
        });

        heartRateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(heartRateIntent);
            }
        });
    }

    private void initializeBarometer() {
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE) != null) {
            mBarometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
            Toast yesBarom = Toast.makeText(getApplicationContext(), "Barometer Sensor Found", Toast.LENGTH_LONG);
            yesBarom.show();
            mBarometerEventListener = new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {

                    if(!isAltitudeModeGps) {

                        if (pressureIsCurrent) {
                            float altitude = mSensorManager.getAltitude(currentSeaLevelPressure, event.values[0]) * unit;
                            if (recordElevationFlag) {
                                elevations.add(altitude);
                            }
                            mAltimeter.setText(String.valueOf(altitude));
                        }
                    }
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {
                }
            };
        } else {
            Toast noBarom = Toast.makeText(getApplicationContext(), "Sorry, this device does not have a barometer. Please select GPS from settings", Toast.LENGTH_LONG);
            noBarom.show();
        }
    }

    public void initializeGps() {

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                if(isAltitudeModeGps) {
                    if (isBetterLocation(location, lastKnown)) {
//                        LatLon latLon = LatLon.fromDegrees(location.getLatitude(), location.getLongitude());
//                        double offset = egm96.getOffset(latLon.getLatitude(), latLon.getLongitude());


                        if (recordElevationFlag) {
                            float altitude = (float)location.getAltitude() * unit;
                            elevations.add(altitude);
                        }
                        mAltimeter.setText(String.valueOf(location.getAltitude() * unit));
                    }
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };

        if (ActivityCompat.checkSelfPermission(
                getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, locationListener);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK && requestCode == 0) {
            Bundle bundle = data.getExtras();

            boolean isBarometerSelected = bundle.getBooleanArray("settings")[0];
            boolean isFeetSelected = bundle.getBooleanArray("settings")[1];

            if (isBarometerSelected) {
                SettingsActivity.startSensor = true;
                isAltitudeModeGps = false;
                if(!pressureIsCurrent) {
                    readyText.setText("NOT READY");
                    readyText.setTextColor(Color.rgb(255, 0, 0));
                    startButton.setEnabled(false);
                }
            }else{
                SettingsActivity.startSensor = false;
                isAltitudeModeGps = true;
                readyText.setText("READY");
                readyText.setTextColor(Color.rgb(0, 255, 0));
                startButton.setEnabled(true);
            }

            if(isFeetSelected)
                unit = 3.28084f;
            else
                unit = 1;

        }
    }

    public void getPressureData(){
        // Start thread to get data
        String url = "http://w1.weather.gov/xml/current_obs/KDEN.xml";
        connect(url);
    }

    /**
     * Checks if phone has good connection then calls the PullSnowHistoryTask
     * Async Task to connect to the website
     *
     * @param stringUrl The URL of the location
     */
    public void connect(String stringUrl) {
        ConnectivityManager connMgr = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            Log.d("CONNECTION INFO: ", "good connection");
            new PullPressureDataTask().execute(stringUrl);
        } else {
            Log.d("CONNECTION INFO: ", "bad connection");
        }
    }

    private class PullPressureDataTask extends AsyncTask<String, Void, Float> {
        @Override
        protected Float doInBackground(String... urls) {
            // params comes from the execute() call: params[0] is the url.
            try {
                Document doc = Jsoup.connect(urls[0]).get();
                Element pressure_mb = doc.select("pressure_mb").first();
                float pressure = Float.parseFloat(pressure_mb.text());

                return pressure;

            } catch (IOException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Float result) {
            currentSeaLevelPressure = result;
            startButton.setEnabled(true);
            readyText.setText("READY");
            readyText.setTextColor(Color.rgb(0, 255, 0));
            pressureIsCurrent = true;
            pressureText.setText(String.valueOf(currentSeaLevelPressure));
        }
    }

    @Override
    protected void onResume() {
        // Register a listener for the sensor.
        super.onResume();
        mSensorManager.registerListener(mBarometerEventListener, mBarometerSensor, SensorManager.SENSOR_DELAY_NORMAL);

        if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, locationListener);
    }

    @Override
    protected void onPause() {
        // Be sure to unregister the sensor when the activity pauses.
        super.onPause();
//        mSensorManager.unregisterListener(mBarometerEventListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_altimeter_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                startActivityForResult(settingsIntent, 0);
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    private static final int TWO_MINUTES = 1000 * 60 * 2;

    /** Determines whether one Location reading is better than the current Location fix
     * @param location  The new Location that you want to evaluate
     * @param currentBestLocation  The current Location fix, to which you want to compare the new one
     */
    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate) {
            return true;
        }
        return false;
    }
}
