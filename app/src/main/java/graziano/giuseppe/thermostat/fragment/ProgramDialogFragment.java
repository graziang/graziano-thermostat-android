package graziano.giuseppe.thermostat.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import graziano.giuseppe.thermostat.R;
import graziano.giuseppe.thermostat.data.model.Program;

public class ProgramDialogFragment extends DialogFragment {

    private static final String ARG_PROGRAM = "program";

    private Program program;

    private TextView textViewWeekDay;


   // private OnFragmentInteractionListener mListener;

    public ProgramDialogFragment() {
        // Required empty public constructor
    }

    public static ProgramDialogFragment newInstance(Program program) {
        ProgramDialogFragment fragment = new ProgramDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PROGRAM, program);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            program = (Program) getArguments().getSerializable(ARG_PROGRAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View view = inflater.inflate(R.layout.fragment_program_dialog, container, false);

        textViewWeekDay = (TextView) view.findViewById(R.id.weekDay);
        // Fetch arguments from bundle and set title
        String title = program.getName();
        getDialog().setTitle(title);
        textViewWeekDay.setText(program.getName());
        return view;
    }


}
