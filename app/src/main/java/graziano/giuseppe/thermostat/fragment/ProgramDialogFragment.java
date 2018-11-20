package graziano.giuseppe.thermostat.fragment;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.time.DayOfWeek;
import java.time.LocalDate;

import graziano.giuseppe.thermostat.R;
import graziano.giuseppe.thermostat.data.model.Program;

public class ProgramDialogFragment extends DialogFragment {

    private static final String ARG_PROGRAM = "program";

    private Program program;

    private NumberPicker numberPikerWeekDay;


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

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();


        View view = inflater.inflate(R.layout.fragment_program_dialog, null);

        numberPikerWeekDay = (NumberPicker) view.findViewById(R.id.weekDay);
        final String dayss[] = {DayOfWeek.MONDAY.toString(),DayOfWeek.THURSDAY.toString(),DayOfWeek.WEDNESDAY.toString(),DayOfWeek.THURSDAY.toString(),DayOfWeek.FRIDAY.toString(),DayOfWeek.SATURDAY.toString(),DayOfWeek.SUNDAY.toString()};

        final String days[] =  getResources().getStringArray(R.array.week_days);


        numberPikerWeekDay.setMinValue(0);
        numberPikerWeekDay.setMaxValue(DayOfWeek.values().length - 1);
        numberPikerWeekDay.setDisplayedValues(days);
        numberPikerWeekDay.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        NumberPicker.OnValueChangeListener myValChangedListener = new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                //  Toast..setText("Value: " + days[newVal]);
                Toast.makeText(getContext(), DayOfWeek.values()[newVal].toString(), Toast.LENGTH_SHORT).show();

            }
        };

        numberPikerWeekDay.setOnValueChangedListener(myValChangedListener);

        builder.setView(view)
                .setTitle("ECCOLOOOOOO")
                .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // call the method on the parent activity when user click the positive button
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // call the method on the parent activity when user click the negative button
                    }
                });
        return builder.create();
    }



}
