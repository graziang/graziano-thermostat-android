package graziano.giuseppe.thermostat.fragment;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;


import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.transform.Pivot;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;
import graziano.giuseppe.thermostat.DataUtils;
import graziano.giuseppe.thermostat.MainActivity;
import graziano.giuseppe.thermostat.R;
import graziano.giuseppe.thermostat.adapter.ProgramRecyclerViewAdapter;
import graziano.giuseppe.thermostat.adapter.ThermostatSensorRecyclerViewAdapter;
import graziano.giuseppe.thermostat.data.model.Measurement;
import graziano.giuseppe.thermostat.data.model.Program;
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
    private ThermostatFragmentProgram.OnListFragmentInteractionListener mListener;
    private ProgramRecyclerViewAdapter programRecyclerViewAdapter;
    private BottomSheetBehavior behavior;
    private    HorizontalCalendar horizontalCalendar;
    List<Program> programs = new ArrayList<>();
    private DayOfWeek currentWeekDay = DayOfWeek.MONDAY;
    private CircleSeekBar thermostatSeakBar;

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
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ThermostatFragmentProgram.OnListFragmentInteractionListener) {
            mListener = (ThermostatFragmentProgram.OnListFragmentInteractionListener) context;
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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_thermostat_manual, container, false);

        thermostatSeakBar = view.findViewById(R.id.thermostatSeekBar);


        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                initializeView(view);
            }
        });

        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.DAY_OF_YEAR, -7);

        /* ends after 1 month from now */
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.DAY_OF_YEAR, 7);

        HorizontalCalendar horizontalCalendar = new HorizontalCalendar.Builder(view, R.id.calendarView)
                .range(startDate, endDate)
                .configure().showTopText(false).end()
                .datesNumberOnScreen(5)
                .build();
        currentWeekDay = LocalDate.now().getDayOfWeek();

        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {

                LocalDate calendarAsLocalDate = date.toInstant()
                        .atZone(date.getTimeZone().toZoneId())
                        .toLocalDate();

                currentWeekDay = calendarAsLocalDate.getDayOfWeek();

                updatePrograms();

            }
        });

        View bottomSheet =  view.findViewById(R.id.bottom_sheet);
        behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                // React to state change
                if(newState == 3){
                  //  thermostatSeakBar.setClickable(false);
                }
                else if (newState == 2){
                  //  thermostatSeakBar.setClickable(true);
                }
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

        programRecyclerViewAdapter = new ProgramRecyclerViewAdapter(programs, currentWeekDay, getContext(), mListener);
//        programRecyclerViewAdapter.notify();
        recyclerView.setAdapter(programRecyclerViewAdapter);


        Button addPrigramButton = view.findViewById(R.id.addProgram);

        addPrigramButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                Program program = new Program();
                program.setWeekDay(currentWeekDay);
                ProgramDialogFragment programDialogFragment = ProgramDialogFragment.newInstance(program);
                programDialogFragment.show(fm, "fragment_edit_name");
            }
        });

        Thermostat thermostat = MainActivity.user.getSelectedThermostat();

        if(thermostat != null) {

            ToggleButton toggleButton = view.findViewById(R.id.toggleButton);
            toggleButton.setChecked(thermostat.getMode().equals(Thermostat.MANUAL_MODE));


            toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        thermostat.setMode(Thermostat.MANUAL_MODE);
                    }
                    else {
                        thermostat.setMode(Thermostat.PROGRAM_MODE);
                    }
                }
            });
        }

        updatePrograms();

        return  view;
    }
    public void updatePrograms(){


        programs.clear();
        programs.addAll(MainActivity.user.getSelectedThermostat().getProgramMode().getPrograms());
        //filtra per data
        programs.removeIf(p-> !p.getWeekDay().equals(currentWeekDay));

        programs.sort(new Comparator<Program>() {
            @TargetApi(Build.VERSION_CODES.O)
            @Override
            public int compare(Program o1, Program o2) {
                int day1index = Arrays.asList(DayOfWeek.values()).indexOf(o1.getWeekDay());
                int day2Index = Arrays.asList(DayOfWeek.values()).indexOf(o2.getWeekDay());
                if(day1index < day2Index){
                    return -1;
                }
                if(day1index > day2Index){
                    return 1;
                }

                if (o1.getStartTime().isBefore(o2.getStartTime())){
                    return -1;
                }
                else{
                    return 1;
                }

            }
        });

        programRecyclerViewAdapter.dayOfWeek = currentWeekDay;
        programRecyclerViewAdapter.notifyDataSetChanged();

    }


    public void initializeView(View view) {

        TextView thermostatTemperatureTextView = view.findViewById(R.id.thermostatTemperatureTextView);
      //  TextView thermostatNameTextView = view.findViewById(R.id.thermostatNameTextView);
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


            getActivity().setTitle(thermostat.getName());

         //   thermostatNameTextView.setText(thermostat.getName());

            int value = (int) (((thermostat.getTemperature() - MIN_THEMPERATURE) * thermostatSeakBar.getMaxProcess()) / (MAX_THEMPERATURE - MIN_THEMPERATURE));
            thermostatSeakBar.setCurProcess(value);
            thermostatTemperatureTextView.setText(String.format("%s°C", thermostat.getTemperature()));
            this.thermostatTemperature = thermostat.getTemperature();


            PulsatorLayout pulsatorLayout = view.findViewById(R.id.pulsator);
            View button = pulsatorLayout.findViewById(R.id.status);
            Drawable background = button.getBackground();

            if(measurementsLast != null && measurementsLast.size() > 0) {


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

        TextView textViewStato = (TextView)  view.findViewById(R.id.textViewStato);

        String stato = "";

        if(measurementsLast.size() == 0){
                textViewStato.setTextColor(getResources().getColor(R.color.colorAccent));
                stato = "TERMOSTATO OFFLINE";
        }
        else if(System.currentTimeMillis() - measurementsLast.get(1).getDate().getTime() > 60 * 1000 ){
            textViewStato.setTextColor(getResources().getColor(R.color.colorAccent));
            stato = "TERMOSTATO OFFLINE";
        }
        else if(thermostat.isStateOn()){
            textViewStato.setTextColor(getResources().getColor(R.color.colorActive));
            stato = "RISCALDAMENTO ACCESO";
        }
        else {
            textViewStato.setTextColor(getResources().getColor(R.color.colorNotActive));
            LocalTime start = null;
            LocalTime now = LocalTime.now();
            LocalDate date = LocalDate.now();
            for (Program program: thermostat.getProgramMode().getPrograms()){
                if(program.isActive() && date.getDayOfWeek().equals(program.getWeekDay())){
                    if(program.getStartTime().isAfter(now)){
                        if(start == null){
                            start = program.getStartTime();
                        }
                        else {
                            if(program.getStartTime().isBefore(start)){
                                start = program.getStartTime();
                            }
                        }
                    }
                }
            }

            if(start != null) {
                stato = "RISCALDAMENTO SPENTO.\n Sì accenderà alle " + start.toString();
            }
            else {
                stato = "RISCALDAMENTO SPENTO";
            }

        }

        textViewStato.setText(stato);



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


           // MainActivity.user.getSelectedThermostat().setProgramMode(thermostat.getProgramMode());
          //  MainActivity.user.getSelectedThermostat().setProgramMode(thermostat.getProgramMode());
            //MainActivity.user.getSelectedThermostat().setProgramMode(thermostat.getProgramMode());
            // getFragmentManager().beginTransaction().detach(this).attach(this).commit();


            if(getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (thermostatTemperature == thermostat.getTemperature()) {
                            initializeView(getView());
                            updatePrograms();
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
