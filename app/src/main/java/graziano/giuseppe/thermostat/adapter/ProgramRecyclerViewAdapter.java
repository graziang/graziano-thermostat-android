package graziano.giuseppe.thermostat.adapter;

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

public class ProgramRecyclerViewAdapter extends RecyclerView.Adapter<ProgramRecyclerViewAdapter.ProgramViewHolder> {


    private final Context context;
    private List<Program> programs;

    public ProgramRecyclerViewAdapter(List<Program> programs, Context context) {

        this.programs = programs;
        this.context = context;
    }


    @Override
    public ProgramRecyclerViewAdapter.ProgramViewHolder onCreateViewHolder(ViewGroup parent, int index) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_program_list_dialog_item, parent, false);


        return new ProgramRecyclerViewAdapter.ProgramViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ProgramRecyclerViewAdapter.ProgramViewHolder holder, final int position) {

        holder.programNameTextView.setText(programs.get(position).getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, holder.programNameTextView.getText(), Toast.LENGTH_SHORT).show();
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
