package ventureindustries.altimeter;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.io.IOException;
import java.util.ArrayList;


public class MyAltimeter extends AppCompatActivity {
    private Button startButton;
    private Button stopButton;
    private Button getDataButton;
    private Button heartRateButton;
    private TextView mAltimeter;
    private TextView readyText;
    private TextView pressureText;
    private static final String DEBUG_TAG = "DEBUGGER MESSAGE:   ";
    private SensorEventListener mSensorEventListener;
    private SensorManager mSensorManager;
    private Sensor mBarometerSensor;
    public float currentSeaLevelPressure = 0;
    public ArrayList<Float> elevations;
    private boolean recordElevationFlag = false;
    private boolean pressureIsCurrent = false;
    private Intent graphIntent;
    private Intent heartRateIntent;
//    private android.support.v4.app.FragmentManager manager;
//    private android.support.v4.app.FragmentTransaction transaction;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_altimeter);

        graphIntent = new Intent(this, GraphActivty.class);
        heartRateIntent = new Intent(this, HeartRateActivity.class);
//        manager = getSupportFragmentManager();

        startButton = (Button) findViewById(R.id.start_button);
        stopButton = (Button) findViewById(R.id.stop_button);
        getDataButton = (Button)findViewById(R.id.get_data_button);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAltimeter = (TextView) findViewById(R.id.altitudeView);
        heartRateButton = (Button) findViewById(R.id.heart_rate_button);
        readyText = (TextView) findViewById(R.id.ready_text);
        pressureText = (TextView) findViewById(R.id.pressure_text);
        startButton.setEnabled(false);
        stopButton.setEnabled(false);
        elevations = new ArrayList<>();

//        StartFragment start = new StartFragment();
//
//        transaction = manager.beginTransaction();
//        transaction.add(R.id.fragmentContainer, start);
//        transaction.commit();

        if (mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE) != null){
            mBarometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
            Log.d(DEBUG_TAG, "Barometer found");
            mSensorEventListener = new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    if(pressureIsCurrent){
                        float altitude = mSensorManager.getAltitude(currentSeaLevelPressure, event.values[0]) * 3.28084f;
                        if(recordElevationFlag){
                            elevations.add(altitude);
                        }
                        mAltimeter.setText(String.valueOf(altitude));
                    }
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {

                }
            };
        }
        else {
            Log.d(DEBUG_TAG, "No barometer found");
        }

        getDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPressureData();
            }
        });

        startButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
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
                for(Float f : elevations){
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
        mSensorManager.registerListener(mSensorEventListener, mBarometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        // Be sure to unregister the sensor when the activity pauses.
        super.onPause();
        mSensorManager.unregisterListener(mSensorEventListener);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//
//        getMenuInflater().inflate(R.menu.my_altimeter_actions, menu);
//
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.settings:
//
//                Settings settings = new Settings();
//
//                transaction = manager.beginTransaction();
//                transaction.replace(R.id.fragmentContainer, settings);
//                transaction.addToBackStack("settings");
//                transaction.commit();
//
//
//                return true;
//
//            default:
//                // If we got here, the user's action was not recognized.
//                // Invoke the superclass to handle it.
//                return super.onOptionsItemSelected(item);
//
//        }
//
//
//    }
}
