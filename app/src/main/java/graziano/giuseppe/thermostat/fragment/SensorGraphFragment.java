package graziano.giuseppe.thermostat.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Response;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.DefaultAxisValueFormatter;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import graziano.giuseppe.thermostat.MainActivity;
import graziano.giuseppe.thermostat.R;
import graziano.giuseppe.thermostat.data.model.Measurement;
import graziano.giuseppe.thermostat.data.model.Sensor;
import graziano.giuseppe.thermostat.data.model.SensorStats;
import graziano.giuseppe.thermostat.network.HttpClient;

public class SensorGraphFragment extends Fragment implements Response.Listener{


    private static final String SENSOR_PARAM = "sensor_param";
    private Sensor sensor;
    private SensorStats sensorStats;
    private List<Measurement> measurementList = new ArrayList<>();

    private LineChart graph;
    private TextView temperatureMinTextView;
    private TextView temperatureMaxTextView;
    private TextView temperatureAvgTextView;

    public SensorGraphFragment() {
        // Required empty public constructor
    }

    public static SensorGraphFragment newInstance(Sensor sensor) {
        SensorGraphFragment fragment = new SensorGraphFragment();
        Bundle args = new Bundle();
        args.putSerializable(SENSOR_PARAM, sensor);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            sensor = (Sensor) getArguments().getSerializable(SENSOR_PARAM);
            HttpClient.getSensorStats(MainActivity.user.getSelectedThermostat().getId(), sensor.getId(), this, null);
            HttpClient.getMeasurements(MainActivity.user.getSelectedThermostat().getId(), sensor.getId(), this, null);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_sensor_graph, container, false);

        TextView sensorNameTextView = view.findViewById(R.id.sensorNameTextView);
        sensorNameTextView.setText(sensor.getName());

        graph = (LineChart) view.findViewById(R.id.graph);

        Description description = new Description();
        description.setText("");
        graph.setDescription(description);
        graph.setTouchEnabled(true);
        graph.setScaleEnabled(true);
        graph.setPinchZoom(true);
        graph.setDragEnabled(true);
        graph.getDescription().setEnabled(false);


        temperatureMinTextView = view.findViewById(R.id.temperature_min);
        temperatureMaxTextView = view.findViewById(R.id.temperature_max);
        temperatureAvgTextView = view.findViewById(R.id.temperature_avg);

        return view;
    }

    @Override
    public void onResponse(Object response) {
        if(response instanceof List){
            measurementList = (List<Measurement>) response;

            List<Entry> entries = new ArrayList<>();


            XAxis xAxis = graph.getXAxis();
            xAxis.setTextColor(getResources().getColor(R.color.colorTextPrimary));
            xAxis.setValueFormatter(new IAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(new Date((long)value));
                    int hours = cal.get(Calendar.HOUR_OF_DAY);
                    int minutes = cal.get(Calendar.MINUTE);
                    return hours + ":" + minutes;
                }
            });

            YAxis leftAxis = graph.getAxisLeft();
            YAxis rightAxis = graph.getAxisRight();

            leftAxis.setTextColor(getResources().getColor(R.color.colorTextPrimary));
            rightAxis.setTextColor(getResources().getColor(R.color.colorTextPrimary));

            for (Measurement measurement: measurementList){

                Entry entry = new Entry(measurement.getDate().getTime(), measurement.getTemperature());
                entries.add(entry);
            }

            LineDataSet dataSet = new LineDataSet(entries, "Temperatura nelle ultime 24 ore"); // add entries to dataset
            dataSet.setColor(getResources().getColor(R.color.colorAccent));
            dataSet.setCircleColor(getResources().getColor(R.color.colorAccent));
            dataSet.setValueTextColor(getResources().getColor(R.color.colorTextPrimary));
            dataSet.setCircleColorHole(getResources().getColor(R.color.colorAccent));
            dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            dataSet.setCubicIntensity(0.2f);
            dataSet.setLineWidth(1.8f);
            //set1.setDrawFilled(true);
            dataSet.setDrawCircles(false);

            LineData lineData = new LineData(dataSet);
            graph.setData(lineData);
            graph.invalidate();

        }
        if(response instanceof SensorStats){
            sensorStats = (SensorStats) response;

            BigDecimal bd = new BigDecimal(sensorStats.getMinTemperature()).setScale(1, RoundingMode.HALF_EVEN);
            String stringMin =  getResources().getString(R.string.sensor_min) + ": "  + bd.floatValue() + "°C";


            bd = new BigDecimal(sensorStats.getMaxTemperature()).setScale(1, RoundingMode.HALF_EVEN);
            String stringMax = getString(R.string.sensor_max) + ": "  + bd.floatValue() + "°C";

            bd = new BigDecimal(sensorStats.getAvgTemperature()).setScale(1, RoundingMode.HALF_EVEN);
            String stringAvg = getString(R.string.sensor_avg) + ": " + bd.floatValue() + "°C";

            temperatureMinTextView.setText(stringMin);
            temperatureMaxTextView.setText(stringMax);
            temperatureAvgTextView.setText(stringAvg);
        }
    }
}
