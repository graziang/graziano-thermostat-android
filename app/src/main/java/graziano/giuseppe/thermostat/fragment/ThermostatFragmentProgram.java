package graziano.giuseppe.thermostat.fragment;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;

import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Response;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.InfiniteScrollAdapter;
import com.yarolegovich.discretescrollview.transform.Pivot;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import graziano.giuseppe.thermostat.DataUtils;
import graziano.giuseppe.thermostat.MainActivity;
import graziano.giuseppe.thermostat.R;
import graziano.giuseppe.thermostat.adapter.ProgramRecyclerViewAdapter;
import graziano.giuseppe.thermostat.adapter.ThermostatSensorRecyclerViewAdapter;
import graziano.giuseppe.thermostat.data.model.Measurement;
import graziano.giuseppe.thermostat.data.model.Program;
import graziano.giuseppe.thermostat.data.model.Sensor;
import graziano.giuseppe.thermostat.data.model.Thermostat;
import graziano.giuseppe.thermostat.network.HttpClient;
import io.feeeei.circleseekbar.CircleSeekBar;
import pl.bclogic.pulsator4droid.library.PulsatorLayout;


public class ThermostatFragmentProgram extends Fragment implements Response.Listener{
    private final int timerPeriod = 6000;

    private OnListFragmentInteractionListener mListener;
    private BottomSheetBehavior behavior;
    private BottomSheetDialogFragment programsBottomSheetDialog;
    private ProgramRecyclerViewAdapter programRecyclerViewAdapter;
    private Timer updateTimer;
    List<Program> programs;
    private List<Measurement> measurementsLast;
    DiscreteScrollView measurementsScrollView;
    private ThermostatSensorRecyclerViewAdapter thermostatSensorRecyclerViewAdapter;

    public ThermostatFragmentProgram() {
        // Required empty public constructor
    }


    public static ThermostatFragmentProgram newInstance() {
        ThermostatFragmentProgram fragment = new ThermostatFragmentProgram();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        programs = new ArrayList<>();
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
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    private void showBottomSheetDialog() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_thermostat_program, container, false);


        View bottomSheet =  view.findViewById(R.id.bottom_sheet);
        behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                // React to state change
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // React to dragging events
            }
        });

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

//        programs = new ArrayList<>(MainActivity.user.getSelectedThermostat().getProgramMode().getPrograms());

        programRecyclerViewAdapter = new ProgramRecyclerViewAdapter(programs, getContext(), mListener);
//        programRecyclerViewAdapter.notify();
        recyclerView.setAdapter(programRecyclerViewAdapter);


        Button addPrigramButton = view.findViewById(R.id.addProgram);

        addPrigramButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                ProgramDialogFragment editNameDialogFragment = ProgramDialogFragment.newInstance(new Program());
                editNameDialogFragment.show(fm, "fragment_edit_name");
            }
        });

        TextView thermostatTemperatureTextView = view.findViewById(R.id.thermostatTemperatureTextView);
        TextView thermostatNameTextView = view.findViewById(R.id.thermostatNameTextView);
        //TextView thermostatLastMeasurementDateTextView = view.findViewById(R.id.thermostatLastMeasurementDateTextView);
        // thermostatLastMeasurementDateTextView.setText(R.string.measurement_miss);
        if(measurementsScrollView == null) {

            this.thermostatSensorRecyclerViewAdapter = new ThermostatSensorRecyclerViewAdapter(measurementsLast, getContext());
            measurementsScrollView = view.findViewById(R.id.sensors_measurements_scrollview);
            InfiniteScrollAdapter wrapper = InfiniteScrollAdapter.wrap(thermostatSensorRecyclerViewAdapter);
            measurementsScrollView.setAdapter(wrapper);
            measurementsScrollView.setOverScrollEnabled(true);

        }

        initializeView(view);

        return  view;
    }

    public void initializeView(View view) {



        PulsatorLayout pulsatorLayout = view.findViewById(R.id.pulsator);
        View button = pulsatorLayout.findViewById(R.id.status);
        Drawable background = button.getBackground();


        Thermostat thermostat = MainActivity.user.getSelectedThermostat();

        if(thermostat != null) {

          //  ((GradientDrawable) background).setColor(getContext().getResources().getColor(R.color.colorBackground));
            if (!thermostat.isStateOn()) {
                button.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                //  pulsatorLayout.setColor(getResources().getColor(R.color.colorNotActive));
                if (pulsatorLayout.isStarted()) {
                    pulsatorLayout.setAlpha(0);
                }
            } else {
                button.setBackgroundColor(getResources().getColor(R.color.colorActive));
                pulsatorLayout.setAlpha(1);
                if (!pulsatorLayout.isStarted()) {
                    pulsatorLayout.setColor(getResources().getColor(R.color.colorActive));
                    pulsatorLayout.start();
                }


            }
        }



    }



    public void updateThermostat() {
        Thermostat thermostat = MainActivity.user.getSelectedThermostat();

        if (thermostat != null) {

            if (thermostat.isActive()) {
                HttpClient.getMeasurementsLast(thermostat.getId(), this, null);
                HttpClient.getThermostat(thermostat.getId(), this, null);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        programs.clear();
                        programs.addAll(MainActivity.user.getSelectedThermostat().getProgramMode().getPrograms());
                        programs.sort(new Comparator<Program>() {
                            @TargetApi(Build.VERSION_CODES.O)
                            @Override
                            public int compare(Program o1, Program o2) {
                                int returnValue = 0;

                                final List<DayOfWeek> days = Arrays.asList(DayOfWeek.values());

                                if(days.indexOf(o1.getWeekDay()) < days.indexOf(o2.getWeekDay())){
                                   return -1;
                                }
                                else if(days.indexOf(o1.getWeekDay()) > days.indexOf(o2.getWeekDay())){
                                    return 1;
                                }

                                LocalTime startTime = LocalTime.of(o1.getStartTime().getHour(), o1.getStartTime().getMinute());

                                LocalTime endTime = LocalTime.of(o2.getStartTime().getHour(), o2.getStartTime().getMinute());

                                if(startTime.isBefore(endTime)){
                                    return -1;
                                }
                                else {
                                    return 1;
                                }

                            }
                        });
                        programRecyclerViewAdapter.notifyDataSetChanged();
                    }
                });

            }
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

                        initializeView(getView());

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


    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void OnListFragmentInteractionListener(Program program);
    }

}
