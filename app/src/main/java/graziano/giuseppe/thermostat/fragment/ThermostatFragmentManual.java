package graziano.giuseppe.thermostat.fragment;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.transform.Pivot;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import graziano.giuseppe.thermostat.DataUtils;
import graziano.giuseppe.thermostat.MainActivity;
import graziano.giuseppe.thermostat.R;
import graziano.giuseppe.thermostat.adapter.ThermostatSensorRecyclerViewAdapter;
import graziano.giuseppe.thermostat.data.model.Measurement;
import graziano.giuseppe.thermostat.data.model.Thermostat;
import graziano.giuseppe.thermostat.network.HttpClient;
import io.feeeei.circleseekbar.CircleSeekBar;
import pl.bclogic.pulsator4droid.library.PulsatorLayout;


public class ThermostatFragmentManual extends Fragment  implements Response.Listener, Response.ErrorListener{


    private final int timerPeriod = 3000;
    private float MIN_THEMPERATURE = 10;
    private float MAX_THEMPERATURE = 31;
    private float thermostatTemperature = 0;
    private String lastMeasurementdate = "";

    private ThermostatSensorRecyclerViewAdapter thermostatSensorRecyclerViewAdapter;
    DiscreteScrollView measurementsScrollView;
    private List<Measurement> measurementsLast;
    private Timer updateTimer;
    private Snackbar thermostatActiveSnackbar;
    public ThermostatFragmentManual() {
        // Required empty public constructor
    }


    public static ThermostatFragmentManual newInstance() {
        ThermostatFragmentManual fragment = new ThermostatFragmentManual();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.measurementsLast = new ArrayList<>();

    }

    @Override
    public void onResume() {
        super.onResume();
        this.updateTimer = new Timer();
        updateThermostat();
        this.updateTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateThermostat();
            }
        }, 0, timerPeriod);

    }

    @Override
    public void onPause() {
        super.onPause();
        if(this.updateTimer != null) {
            this.updateTimer.cancel();
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_thermostat_manual, container, false);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                initializeView(view);
            }
        });


        return  view;
    }

    public void initializeView(View view) {
        CircleSeekBar thermostatSeakBar = view.findViewById(R.id.thermostatSeekBar);
        TextView thermostatTemperatureTextView = view.findViewById(R.id.thermostatTemperatureTextView);
        TextView thermostatNameTextView = view.findViewById(R.id.thermostatNameTextView);
        TextView thermostatLastMeasurementDateTextView = view.findViewById(R.id.thermostatLastMeasurementDateTextView);
        thermostatLastMeasurementDateTextView.setText(R.string.measurement_miss);
        if(measurementsScrollView == null) {

            this.thermostatSensorRecyclerViewAdapter = new ThermostatSensorRecyclerViewAdapter(measurementsLast, getContext());
            measurementsScrollView = view.findViewById(R.id.sensors_measurements_scrollview);
            measurementsScrollView.setAdapter(thermostatSensorRecyclerViewAdapter);

            measurementsScrollView.setItemTransformer(new ScaleTransformer.Builder()
                    .setMaxScale(1.05f)
                    .setMinScale(0.8f)
                    .setPivotX(Pivot.X.CENTER) // CENTER is a default one
                    .setPivotY(Pivot.Y.BOTTOM) // CENTER is a default one
                    .build());
            measurementsScrollView.setAlpha(0);
        }


      /*  measurementsScrollView = InfiniteScrollAdapter.wrap(thermostatSensorRecyclerViewAdapter);
        scrollView.setAdapter(measurementsScrollView);
        scrollView.setItemTransformer(new ScaleTransformer.Builder()
        .setMaxScale(1.05f)
        .setMinScale(0.8f)
        .setPivotX(Pivot.X.CENTER) // CENTER is a default one
        .setPivotY(Pivot.Y.BOTTOM) // CENTER is a default one
        .build());*/
        Thermostat thermostat = MainActivity.user.getSelectedThermostat();

        if(thermostat != null) {



            thermostatNameTextView.setText(thermostat.getName());


            int value = (int) (((thermostat.getTemperature() - MIN_THEMPERATURE) * thermostatSeakBar.getMaxProcess()) / (MAX_THEMPERATURE - MIN_THEMPERATURE));
            thermostatSeakBar.setCurProcess(value);
            thermostatTemperatureTextView.setText(String.format("%s°C", thermostat.getTemperature()));
            this.thermostatTemperature = thermostat.getTemperature();


            PulsatorLayout pulsatorLayout = view.findViewById(R.id.pulsator);
            View button = pulsatorLayout.findViewById(R.id.status);
            Drawable background = button.getBackground();

            if(measurementsLast != null && measurementsLast.size() > 0) {


                String lastUpdate = String.format("%s", (DataUtils.printDifference(measurementsLast.get(1).getDate(), new Date())));
                thermostatLastMeasurementDateTextView.setText(lastUpdate);

                if (measurementsScrollView.getScrollState() == RecyclerView.SCROLL_STATE_IDLE){
                    if (thermostat.getManualMode().isAvg()) {

                        measurementsScrollView.scrollToPosition(0);
                    } else {
                        int position = 0;
                        for (Measurement measurement : measurementsLast) {


                            Long sensorId = thermostat.getManualMode().getSensorId();
                            if (measurement.getSensor() != null && measurement.getSensor().getId() == sensorId) {
                                measurementsScrollView.scrollToPosition(position);
                                break;
                            }
                            position++;
                        }
                    }
                }
                measurementsScrollView.setAlpha(1);
            }

            if(!thermostat.isStateOn()) {
                thermostatSeakBar.setPointerColor(getResources().getColor(R.color.colorNotActive));
            }
            else {
                thermostatSeakBar.setPointerColor(getResources().getColor(R.color.colorActive));

            }



            ((GradientDrawable) background).setColor(getContext().getResources().getColor(R.color.colorBackground));
            if(!thermostat.isStateOn()) {
                //  pulsatorLayout.setColor(getResources().getColor(R.color.colorNotActive));
                if(pulsatorLayout.isStarted()) {
                    pulsatorLayout.setAlpha(0);
                }
            }
            else {
                pulsatorLayout.setAlpha(1);
                if(!pulsatorLayout.isStarted()) {

                    pulsatorLayout.setColor(getResources().getColor(R.color.colorActive));
                    pulsatorLayout.start();
                }


            }

        }
      //  PulsatorLayout pulsatorLayout = view.findViewById(R.id.pulsator);
       // pulsatorLayout.setAlpha(1);
       // pulsatorLayout.start();
        thermostatSeakBar.setOnSeekBarChangeListener(new CircleSeekBar.OnSeekBarChangeListener() {
            @Override
            public void onChanged(CircleSeekBar circleSeekBar, int i) {

                thermostatTemperature = (i * (MAX_THEMPERATURE - MIN_THEMPERATURE)/ circleSeekBar.getMaxProcess()) + MIN_THEMPERATURE;

                BigDecimal bd = new BigDecimal(thermostatTemperature).setScale(1, RoundingMode.HALF_EVEN);
                thermostatTemperature = bd.floatValue();
                thermostatTemperatureTextView.setText(String.format("%s°C", thermostatTemperature));
            }
        });
    }

    public void updateThermostat(){
        Thermostat thermostat = MainActivity.user.getSelectedThermostat();

        if(thermostat != null) {

            if(!thermostat.isActive()) {


                if(thermostatActiveSnackbar == null){
                    thermostatActiveSnackbar = Snackbar.make(getView(), R.string.thermostat_not_active, 5000);
                    if(!thermostatActiveSnackbar.isShown()) {
                        thermostatActiveSnackbar.show();
                    }
                }


            }

            if(this.thermostatTemperature == 0) {
                this.thermostatTemperature = thermostat.getTemperature();
            }

            if(measurementsLast != null && measurementsLast.size() > 0 && measurementsScrollView != null && measurementsScrollView.getAlpha() == 1) {
                Measurement measurementSelected = this.measurementsLast.get(measurementsScrollView.getCurrentItem());

                if (measurementSelected.isAvg()) {
                    thermostat.getManualMode().setAvg(true);
                } else {
                    thermostat.getManualMode().setSensorId(measurementSelected.getSensor().getId());
                    thermostat.getManualMode().setAvg(false);

                }
            }
            thermostat.setTemperature(this.thermostatTemperature);

            if (measurementsScrollView.getScrollState() == RecyclerView.SCROLL_STATE_IDLE){
                HttpClient.putThermostat(thermostat, this, this);
            }
            HttpClient.getMeasurementsLast(thermostat.getId(), this, this);
        }



    }

    @Override
    public void onResponse(Object response) {
        if(response instanceof Thermostat){
            Thermostat thermostat = (Thermostat) response;
            MainActivity.user.getSelectedThermostat().setStateOn(thermostat.isStateOn());
            // getFragmentManager().beginTransaction().detach(this).attach(this).commit();

            if(getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (thermostatTemperature == thermostat.getTemperature()) {
                            initializeView(getView());
                        }
                    }
                });
            }

        }
        if(response instanceof List){
            List<Measurement> measurements = (List<Measurement>) response;

            measurements = new ArrayList<>(measurements);
            Collections.sort(measurements, new Comparator<Measurement>() {
                @Override
                public int compare(Measurement o1, Measurement o2) {
                    return o1.getSensor().getName().compareTo(o2.getSensor().getName());
                }
            });
            float avg = 0;
            for (Measurement measurement: measurements){
                avg += measurement.getTemperature();
            }
            avg = avg / measurements.size();
            Measurement measurementAgv = new Measurement();
            measurementAgv.setTemperature(avg);
            measurementAgv.setAvg(true);
            measurements.add(0, measurementAgv);

            this.measurementsLast.clear();
            this.measurementsLast.addAll(measurements);
            thermostatSensorRecyclerViewAdapter.notifyDataSetChanged();



        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        error.toString();
    }
}
