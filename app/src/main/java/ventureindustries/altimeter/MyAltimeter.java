package ventureindustries.altimeter;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import java.util.ArrayList;

public class MyAltimeter extends AppCompatActivity implements SensorEventListener{
    private Button get_color_button;
    private Button startButton;
    private Button stopButton;
    private Button getDataButton;
    private TextView mAltimeter;
    private static final String DEBUG_TAG = "DEBUGGER MESSAGE:   ";
    private int red = 0;
    private int green = 0;
    private int blue = 0;
    private SensorManager mSensorManager;
    private Sensor mBarometerSensor;
    private int indexCounter = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_altimeter);

        get_color_button = (Button) findViewById(R.id.getcolor);
        startButton = (Button) findViewById(R.id.start_button);
        stopButton = (Button) findViewById(R.id.stop_button);
        getDataButton = (Button)findViewById(R.id.get_data_button);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAltimeter = (TextView) findViewById(R.id.altitudeView);


        if (mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE) != null){
            mBarometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
            Log.d(DEBUG_TAG, "Barometer found");
        }
        else {
            Log.d(DEBUG_TAG, "No barometer found");
        }

        startButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
                startButton.setEnabled(false);
                stopButton.setEnabled(true);
            }
        });

        stopButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                startButton.setEnabled(true);
                stopButton.setEnabled(false);
            }
        });


        get_color_button.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent("ACTION_COLOR");
                startActivityForResult(intent, 0);
            }
        });
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
    public void onSensorChanged(SensorEvent event) {
        float currentPressure = event.values[0];
        float altitude = mSensorManager.getAltitude(1001.7f, currentPressure) * 3.28084f;
        mAltimeter.setText(String.valueOf(altitude));


//        entries.add(new Entry(altitude, indexCounter));
//        iLineDataSet.add(0, mLineDataSet);
//        mLabels.add(String.valueOf((System.currentTimeMillis() - startTimeMs) / 1000.0f));
////        mLabels.add(String.valueOf(indexCounter));
//        mLineData = new LineData(mLabels, iLineDataSet);
//        mLineChart.setData(mLineData);
//        mLineChart.notifyDataSetChanged();
//        mLineChart.invalidate();
        indexCounter++;

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        // Register a listener for the sensor.
        super.onResume();
        mSensorManager.registerListener(this, mBarometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        // Be sure to unregister the sensor when the activity pauses.
        super.onPause();
        mSensorManager.unregisterListener(this);
    }
}
