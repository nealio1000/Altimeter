package ventureindustries.altimeter;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;

public class SettingsActivity extends Activity {

    private boolean sensorUsed = true;
    private boolean unitsUsed = true;

//    View fragmentView;

    private RadioButton gpsCheckBox;
    private RadioButton barometerCheckBox;
    private RadioButton feetCheckBox;
    private RadioButton metersCheckBox;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);


        gpsCheckBox = (RadioButton) findViewById(R.id.gpsId);
        barometerCheckBox = (RadioButton) findViewById(R.id.baroId);
        feetCheckBox = (RadioButton) findViewById(R.id.feetId);
        metersCheckBox = (RadioButton) findViewById(R.id.metersId);

        gpsCheckBox.setChecked(false);
        barometerCheckBox.setChecked(true);
        feetCheckBox.setChecked(true);
        metersCheckBox.setChecked(false);



        
        
//        gpsCheckBox.setSelected(sensorUsed);
//        barometerCheckBox.setSelected(!sensorUsed);
//        feetCheckBox.setSelected(unitsUsed);
//        metersCheckBox.setSelected(!unitsUsed);




    }

    public void onRadioButtonClicked(View view) {
        // Is the view now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.gpsId:
                barometerCheckBox.setChecked(false);
                break;
            case R.id.baroId:
                gpsCheckBox.setChecked(false);
                break;
            case R.id.feetId:
                metersCheckBox.setChecked(false);
                break;
            case R.id.metersId:
                feetCheckBox.setChecked(false);
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();



        Log.d("DebugJose", "Units used: " + unitsUsed + ", Sensor Used: " + sensorUsed);

    }
}
