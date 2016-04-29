package ventureindustries.altimeter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
    private Button saveButton;
    private boolean[] buttonSettings;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        buttonSettings = new boolean[2];
        setContentView(R.layout.activity_settings);

        saveButton = (Button) findViewById(R.id.save_button);
        gpsCheckBox = (RadioButton) findViewById(R.id.gpsId);
        barometerCheckBox = (RadioButton) findViewById(R.id.baroId);
        feetCheckBox = (RadioButton) findViewById(R.id.feetId);
        metersCheckBox = (RadioButton) findViewById(R.id.metersId);

        buttonSettings[0] = true;
        buttonSettings[1] = true;
        gpsCheckBox.setChecked(false);
        barometerCheckBox.setChecked(true);
        feetCheckBox.setChecked(true);
        metersCheckBox.setChecked(false);

        saveButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putBooleanArray("settings", buttonSettings);
                Intent returnIntent = new Intent();
                returnIntent.putExtras(bundle);
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        });

    }

    public void onRadioButtonClicked(View view) {
        // Is the view now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.gpsId:
                barometerCheckBox.setChecked(false);
                buttonSettings[0] = false;
                break;
            case R.id.baroId:
                gpsCheckBox.setChecked(false);
                buttonSettings[0] = true;
                break;
            case R.id.feetId:
                metersCheckBox.setChecked(false);
                buttonSettings[1] = true;
                break;
            case R.id.metersId:
                feetCheckBox.setChecked(false);
                buttonSettings[1] = false;
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();



        Log.d("DebugJose", "Units used: " + unitsUsed + ", Sensor Used: " + sensorUsed);

    }

    @Override
    public void onDestroy(){
        super.onDestroy();


    }
}
