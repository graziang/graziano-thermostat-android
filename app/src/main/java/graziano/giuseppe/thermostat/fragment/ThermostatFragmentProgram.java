package graziano.giuseppe.thermostat.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;

import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import graziano.giuseppe.thermostat.MainActivity;
import graziano.giuseppe.thermostat.R;
import graziano.giuseppe.thermostat.adapter.ProgramRecyclerViewAdapter;
import graziano.giuseppe.thermostat.data.model.Program;
import graziano.giuseppe.thermostat.data.model.Sensor;


public class ThermostatFragmentProgram extends Fragment{

    private OnListFragmentInteractionListener mListener;
    private BottomSheetBehavior behavior;
    private BottomSheetDialogFragment programsBottomSheetDialog;
    private ProgramRecyclerViewAdapter programRecyclerViewAdapter;


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


    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();

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

        List<Program> programs = new ArrayList<>(MainActivity.user.getSelectedThermostat().getProgramMode().getPrograms());

        programRecyclerViewAdapter = new ProgramRecyclerViewAdapter(programs, getContext(), mListener);
        recyclerView.setAdapter(programRecyclerViewAdapter);

        return  view;
    }

    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void OnListFragmentInteractionListener(Program program);
    }

}
