package ventureindustries.altimeter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;

public class GraphActivty extends AppCompatActivity {

    public LineChart mLineChart;
    public ArrayList<Entry> entries;
    public ArrayList<ILineDataSet> iLineDataSet;
    public ArrayList<Float> mAltitudeData;
    public ArrayList<String> mLabels;
    public LineDataSet mLineDataSet;
    public LineData mLineData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_activty);

        mLineChart = (LineChart) findViewById(R.id.chart);
        entries = new ArrayList<>();
        mAltitudeData = new ArrayList<>();
        mLabels = new ArrayList<>();
        iLineDataSet = new ArrayList<>();
        mLineDataSet = new LineDataSet(entries, "ALTITUDE");
        mLineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
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
}
