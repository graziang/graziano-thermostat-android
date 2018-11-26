package graziano.giuseppe.thermostat.adapter;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import graziano.giuseppe.thermostat.MainActivity;
import graziano.giuseppe.thermostat.R;
import graziano.giuseppe.thermostat.data.model.Program;
import graziano.giuseppe.thermostat.data.model.Thermostat;
import graziano.giuseppe.thermostat.fragment.ThermostatFragmentProgram;
import graziano.giuseppe.thermostat.network.HttpClient;

public class ProgramRecyclerViewAdapter extends RecyclerView.Adapter<ProgramRecyclerViewAdapter.ProgramViewHolder> {


    private ThermostatFragmentProgram.OnListFragmentInteractionListener mListener;

    private Context context = null;
    private List<Program> programs;
    public DayOfWeek dayOfWeek;

    public ProgramRecyclerViewAdapter(List<Program> programs, DayOfWeek day, Context context, ThermostatFragmentProgram.OnListFragmentInteractionListener listener) {

        this.programs = programs;
        this.context = context;
        this.mListener = listener;
        this.dayOfWeek = day;
    }


    @Override
    public ProgramRecyclerViewAdapter.ProgramViewHolder onCreateViewHolder(ViewGroup parent, int index) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_program_list_dialog_item, parent, false);


        return new ProgramRecyclerViewAdapter.ProgramViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ProgramRecyclerViewAdapter.ProgramViewHolder holder, final int position) {

        Program program = this.programs.get(position);

        final String days[] = context.getResources().getStringArray(R.array.week_days);


        String timeFrom = program.getStartTime().toString();
        String timeTo = program.getEndTime().toString();

        holder.timeFromTextView.setText(timeFrom);
        holder.timeToTextView.setText(timeTo);
        holder.activeSwitch.setChecked(program.isActive());
        holder.activeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(program.getWeekDay().equals(dayOfWeek)) {
                    program.setActive(isChecked);
                }
                //HttpClient.putProgram(MainActivity.user.getSelectedThermostatId(), program, null, null);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context, holder.programNameTextView.getText(), Toast.LENGTH_SHORT).show();
                mListener.OnListFragmentInteractionListener(program);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showConfirmDialog(program);
                return false;
            }
        });

    }

    @Override
    public int getItemCount() {
        return programs.size();
    }


    public class ProgramViewHolder extends RecyclerView.ViewHolder {

        public TextView timeFromTextView;
        public TextView timeToTextView;
        private Switch activeSwitch;


        public ProgramViewHolder(View view) {
            super(view);

            timeFromTextView = view.findViewById(R.id.timeFromTextView);
            timeToTextView = view.findViewById(R.id.timeToTextView);
            activeSwitch = view.findViewById(R.id.activeSwitch);
        }
    }


    private void showConfirmDialog(Program program){

        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setMessage("Eliminare programma?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Si",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        Thermostat thermostat = MainActivity.user.getSelectedThermostat();
                      //  HttpClient.deleteProgram(thermostat.getId(), program, null, null);
                        thermostat.getProgramMode().getPrograms().remove(program);
                        dialog.cancel();

                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

}
