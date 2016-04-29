package ventureindustries.altimeter;

import android.app.Activity;
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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.io.IOException;
import java.util.ArrayList;


public class MyAltimeter extends Activity {
    private Button startButton;
    private Button stopButton;
    private Button getDataButton;
    private TextView mAltimeter;
    private TextView readyText;
    private static final String DEBUG_TAG = "DEBUGGER MESSAGE:   ";
    private int red = 0;
    private int green = 0;
    private int blue = 0;
    private SensorEventListener mSensorEventListener;
    private SensorManager mSensorManager;
    private Sensor mBarometerSensor;
    private int indexCounter = 1;
    public float currentSeaLevelPressure = 0;
    public ArrayList<Float> elevations;
    private boolean recordElevationFlag = false;
    private boolean pressureIsCurrent = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_altimeter);

        startButton = (Button) findViewById(R.id.start_button);
        stopButton = (Button) findViewById(R.id.stop_button);
        getDataButton = (Button)findViewById(R.id.get_data_button);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAltimeter = (TextView) findViewById(R.id.altitudeView);
        readyText = (TextView) findViewById(R.id.ready_text);
        startButton.setEnabled(false);
        stopButton.setEnabled(false);
        elevations = new ArrayList<>();

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
                            indexCounter++;
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
                recordElevationFlag = true;
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startButton.setEnabled(true);
                stopButton.setEnabled(false);
                recordElevationFlag = false;
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
//            mSensorManager.registerListener(mSensorEventListener, mBarometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }



    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode == RESULT_OK && requestCode == 0){
//            ColorWindow.setBackgroundColor(Color.rgb(data.getIntExtra("red", red),
//                    data.getIntExtra("green", green), data.getIntExtra("blue", blue)));

            /* Set the line color here */

//            mLineDataSet.setColor(Color.rgb(data.getIntExtra("red", red),
//                    data.getIntExtra("green", green), data.getIntExtra("blue", blue)));
            /*                          */


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
}
