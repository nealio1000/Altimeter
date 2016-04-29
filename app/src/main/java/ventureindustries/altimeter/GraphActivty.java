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

public class GraphActivty extends Activity {

    public LineChart mLineChart;
    public ArrayList<Entry> entries;
    public ArrayList<ILineDataSet> iLineDataSet;
    public ArrayList<Float> mAltitudeData;
    public ArrayList<String> mLabels;
    public LineDataSet mLineDataSet;
    public LineData mLineData;
    private Button getColorButton;
    private int red;
    private int green;
    private int blue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_activty);
        Intent intent = getIntent();

        getColorButton = (Button) findViewById(R.id.get_color_button);

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
        mLineChart.notifyDataSetChanged();
        mLineChart.invalidate();

        getColorButton.setOnClickListener(new View.OnClickListener() {
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

            mLineDataSet.setColor(Color.rgb(data.getIntExtra("red", red),
                    data.getIntExtra("green", green), data.getIntExtra("blue", blue)));
            /*                          */


        }
    }
}
