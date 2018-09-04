package graziano.giuseppe.thermostat.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import graziano.giuseppe.thermostat.MainActivity;
import graziano.giuseppe.thermostat.R;
import graziano.giuseppe.thermostat.adapter.SensorsRecyclerViewAdapter;
import graziano.giuseppe.thermostat.data.model.Sensor;
import graziano.giuseppe.thermostat.data.model.SensorStats;
import graziano.giuseppe.thermostat.data.model.Thermostat;
import graziano.giuseppe.thermostat.network.HttpClient;


public class SensorsFragment extends Fragment implements Response.Listener, Response.ErrorListener {

    private SensorsRecyclerViewAdapter sensorsRecyclerViewAdapter;
    private List<Sensor> sensors;

    public SensorsFragment() {
        // Required empty public constructor
    }

    public static SensorsFragment newInstance() {
        SensorsFragment fragment = new SensorsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thermostat thermostat = MainActivity.user.getSelectedThermostat();
        if(thermostat != null) {
           // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
             //   sensors = thermostat.getSensors().stream().sorted(Comparator.comparing((Sensor e) -> e.getName().toLowerCase())).collect(Collectors.toList());
           // }
            sensors = new ArrayList<>(thermostat.getSensors());
            Collections.sort(sensors, new Comparator<Sensor>() {
                @Override
                public int compare(Sensor o1, Sensor o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            });

            for (Sensor sensor: sensors){
                HttpClient.getSensorStats(thermostat.getId(), sensor.getId(), this, this);
               // HttpClient.getSensorStatsFromTo(thermostat.getId(), sensor.getId(), System.currentTimeMillis() - (1000 * 60 * 60 * 24), System.currentTimeMillis(), this, this);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sensors, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            this.sensorsRecyclerViewAdapter = new SensorsRecyclerViewAdapter(this.sensors, getContext());
            recyclerView.setAdapter(sensorsRecyclerViewAdapter);
        }
        return view;
    }


    @Override
    public void onResponse(Object response) {
        if(response instanceof SensorStats){
            SensorStats sensorStats = (SensorStats) response;
            for (Sensor sensor: this.sensors){
                if(sensorStats.getSensor().getId() == sensor.getId()) {
                    sensor.setSensorStats(sensorStats);
                }

            }
        }
        this.sensorsRecyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Log.e("", error.toString());
    }


}
