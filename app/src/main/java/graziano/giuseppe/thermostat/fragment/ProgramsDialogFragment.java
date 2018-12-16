package graziano.giuseppe.thermostat.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import graziano.giuseppe.thermostat.R;
import graziano.giuseppe.thermostat.adapter.ProgramRecyclerViewAdapter;
import graziano.giuseppe.thermostat.data.model.Program;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProgramsDialogFragment extends Fragment {


    public ProgramsDialogFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_programs_dialog, container, false);

/*
        programRecyclerViewAdapter = new ProgramRecyclerViewAdapter(programs, currentWeekDay, getContext(), mListener);
//        programRecyclerViewAdapter.notify();
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
        });*/
    }

}
