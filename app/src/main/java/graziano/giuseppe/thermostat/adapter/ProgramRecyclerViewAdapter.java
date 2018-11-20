package graziano.giuseppe.thermostat.adapter;

import android.app.FragmentManager;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import graziano.giuseppe.thermostat.R;
import graziano.giuseppe.thermostat.data.model.Program;
import graziano.giuseppe.thermostat.fragment.ThermostatFragmentProgram;

public class ProgramRecyclerViewAdapter extends RecyclerView.Adapter<ProgramRecyclerViewAdapter.ProgramViewHolder> {


    private ThermostatFragmentProgram.OnListFragmentInteractionListener mListener;

    private final Context context;
    private List<Program> programs;

    public ProgramRecyclerViewAdapter(List<Program> programs, Context context, ThermostatFragmentProgram.OnListFragmentInteractionListener listener) {

        this.programs = programs;
        this.context = context;
        this.mListener = listener;
    }


    @Override
    public ProgramRecyclerViewAdapter.ProgramViewHolder onCreateViewHolder(ViewGroup parent, int index) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_program_list_dialog_item, parent, false);


        return new ProgramRecyclerViewAdapter.ProgramViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ProgramRecyclerViewAdapter.ProgramViewHolder holder, final int position) {

        Program program = this.programs.get(position);

        String text = programs.get(position).getWeekDay().toString() + " - " + program.getStartTime().toString() + " a " + program.getEndTime().toString();

        holder.programNameTextView.setText(text);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context, holder.programNameTextView.getText(), Toast.LENGTH_SHORT).show();
                mListener.OnListFragmentInteractionListener(program);
            }
        });

    }

    @Override
    public int getItemCount() {
        return programs.size();
    }


    public class ProgramViewHolder extends RecyclerView.ViewHolder {

        public TextView programNameTextView;


        public ProgramViewHolder(View view) {
            super(view);

            programNameTextView = view.findViewById(R.id.program_name);
        }
    }

}
