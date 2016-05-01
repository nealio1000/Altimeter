package ventureindustries.altimeter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;

public class GraphActivity extends Activity {

    public LineChart mLineChart;
    public ArrayList<Entry> entries;
    public ArrayList<ILineDataSet> iLineDataSet;
    public ArrayList<Float> mAltitudeData;
    public ArrayList<String> mLabels;
    public LineDataSet mLineDataSet;
    public LineData mLineData;
    private Button getLineColorButton;
    private Button getDotColorButton;
    private int red = 0;
    private int green = 0;
    private int blue = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_activty);
        Intent intent = getIntent();

        getLineColorButton = (Button) findViewById(R.id.get_line_color_button);
        getDotColorButton = (Button) findViewById(R.id.get_dot_color_button);

        mLineChart = (LineChart) findViewById(R.id.chart);
        entries = new ArrayList<>();
        mAltitudeData = new ArrayList<>();
        mLabels = new ArrayList<>();
        iLineDataSet = new ArrayList<>();
        mLineDataSet = new LineDataSet(entries, "ALTITUDE");
        mLineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        float[] data = intent.getFloatArrayExtra("altitudes");

        for(int i = 0; i < data.length; i++) {
            entries.add(new Entry(data[i], i));
        }

        iLineDataSet.add(0,mLineDataSet);
        for(int i = 0; i < data.length; i++)
            mLabels.add(String.valueOf(i));

        mLineData = new LineData(mLabels, iLineDataSet);
        mLineChart.setData(mLineData);
        mLineChart.setBackgroundColor(Color.rgb(244,244,244));
        mLineDataSet.setCircleColor(Color.rgb(255, 0, 0));
        mLineChart.notifyDataSetChanged();
        mLineChart.invalidate();

        mLineDataSet.setColor(Color.rgb(0, 0, 0));

        getLineColorButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent("ACTION_COLOR");
                startActivityForResult(intent, 0);
            }
        });

        getDotColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("ACTION_COLOR");
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode == RESULT_OK && requestCode == 0){
            mLineDataSet.setColor(Color.rgb(data.getIntExtra("red", red),
                    data.getIntExtra("green", green), data.getIntExtra("blue", blue)));
        }

        if(resultCode == RESULT_OK && requestCode == 1){
            mLineDataSet.setCircleColor(Color.rgb(data.getIntExtra("red", red),
                    data.getIntExtra("green", green), data.getIntExtra("blue", blue)));
        }
    }
}
