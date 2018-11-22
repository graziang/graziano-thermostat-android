package graziano.giuseppe.thermostat.fragment;

import android.content.Context;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import graziano.giuseppe.thermostat.MainActivity;
import graziano.giuseppe.thermostat.R;
import graziano.giuseppe.thermostat.adapter.ProgramRecyclerViewAdapter;
import graziano.giuseppe.thermostat.data.model.Program;
import graziano.giuseppe.thermostat.data.model.Sensor;
import graziano.giuseppe.thermostat.data.model.Thermostat;


public class ThermostatFragmentProgram extends Fragment{
    private final int timerPeriod = 3000;

    private OnListFragmentInteractionListener mListener;
    private BottomSheetBehavior behavior;
    private BottomSheetDialogFragment programsBottomSheetDialog;
    private ProgramRecyclerViewAdapter programRecyclerViewAdapter;
    private Timer updateTimer;
    List<Program> programs;

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

        return  view;
    }


    public void updateThermostat() {
        Thermostat thermostat = MainActivity.user.getSelectedThermostat();

        if (thermostat != null) {

            if (thermostat.isActive()) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        programs.clear();
                        programs.addAll(MainActivity.user.getSelectedThermostat().getProgramMode().getPrograms());
                        programRecyclerViewAdapter.notifyDataSetChanged();
                    }
                });

            }
        }
    }


    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void OnListFragmentInteractionListener(Program program);
    }

}
