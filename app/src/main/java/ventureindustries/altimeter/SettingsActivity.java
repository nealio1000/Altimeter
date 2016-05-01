package ventureindustries.altimeter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

public class SettingsActivity extends Activity {

    private RadioButton gpsButton;
    private RadioButton barometerButton;
    private RadioButton feetButton;
    private RadioButton metersButton;
    
    private Button saveButton;
    private boolean[] buttonSettings;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        buttonSettings = new boolean[2];
        setContentView(R.layout.activity_settings);

        saveButton = (Button) findViewById(R.id.save_button);
        gpsButton = (RadioButton) findViewById(R.id.gpsId);
        barometerButton = (RadioButton) findViewById(R.id.baroId);
        feetButton = (RadioButton) findViewById(R.id.feetId);
        metersButton = (RadioButton) findViewById(R.id.metersId);

        buttonSettings[0] = true;
        buttonSettings[1] = true;
        gpsButton.setChecked(false);
        barometerButton.setChecked(true);
        feetButton.setChecked(true);
        metersButton.setChecked(false);

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

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.gpsId:
                barometerButton.setChecked(false);
                buttonSettings[0] = false;
                break;
            case R.id.baroId:
                gpsButton.setChecked(false);
                buttonSettings[0] = true;
                break;
            case R.id.feetId:
                metersButton.setChecked(false);
                buttonSettings[1] = true;
                break;
            case R.id.metersId:
                feetButton.setChecked(false);
                buttonSettings[1] = false;
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }
}
