package graziano.giuseppe.thermostat.adapter;

import android.content.Context;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import graziano.giuseppe.thermostat.R;

import graziano.giuseppe.thermostat.data.model.Sensor;
import graziano.giuseppe.thermostat.data.model.SensorStats;
import graziano.giuseppe.thermostat.fragment.SensorsFragment;
import pl.bclogic.pulsator4droid.library.PulsatorLayout;


public class SensorsRecyclerViewAdapter extends RecyclerView.Adapter<SensorsRecyclerViewAdapter.SensorViewHolder> {


    private final Context context;
    private List<Sensor> sensors;
    private final SensorsFragment.OnListFragmentInteractionListener mListener;

    public SensorsRecyclerViewAdapter(List<Sensor> sensors, Context context, SensorsFragment.OnListFragmentInteractionListener mListener) {

        this.sensors = sensors;
        this.context = context;
        this.mListener = mListener;
    }

    @Override
    public SensorsRecyclerViewAdapter.SensorViewHolder onCreateViewHolder(ViewGroup parent, int index) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sensor_item, parent, false);


        return new SensorsRecyclerViewAdapter.SensorViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final SensorsRecyclerViewAdapter.SensorViewHolder holder, final int position) {

        Sensor sensor = this.sensors.get(position);
        holder.sensorNameTextView.setText(sensor.getName());
        SensorStats sensorStats= sensor.getSensorStats();
        if(sensorStats != null) {
            BigDecimal bd = new BigDecimal(sensorStats.getMinTemperature()).setScale(1, RoundingMode.HALF_EVEN);
            String stringMin =  context.getResources().getString(R.string.sensor_min) + ": "  + bd.floatValue() + "°C";


            bd = new BigDecimal(sensorStats.getMaxTemperature()).setScale(1, RoundingMode.HALF_EVEN);
            String stringMax =  context.getResources().getString(R.string.sensor_max) + ": "  + bd.floatValue() + "°C";

            bd = new BigDecimal(sensorStats.getAvgTemperature()).setScale(1, RoundingMode.HALF_EVEN);
            String stringAvg = context.getResources().getString(R.string.sensor_avg) + ": " + bd.floatValue() + "°C";

            String statsString = String.format("%s\n%s\n%s",stringMin, stringMax, stringAvg);
            holder.sensorStatsTextView.setText(statsString);
        }
        else {
           holder.sensorStatsTextView.setText("");
        }

        if(sensor.isActive()) {
            holder.sensorPulsatorLayout.start();
        }
        else {
            holder.sensorPulsatorLayout.stop();
        }

      //  View button = holder.sensorPulsatorLayout.findViewById(R.id.status);
//        Drawable background = button.getBackground();
        if(!sensor.isActive()) {
            holder.sensorPulsatorLayout.setColor(context.getResources().getColor(R.color.colorNotActive));
         //   ((GradientDrawable) background).setColor(context.getResources().getColor(R.color.colorNotActive));


        }
        else {
            holder.sensorPulsatorLayout.setColor(context.getResources().getColor(R.color.colorActive));
         //  ((GradientDrawable) background).setColor(context.getResources().getColor(R.color.colorActive));

        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.OnListFragmentInteractionListener(sensor );
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return sensors.size();
    }


    public class SensorViewHolder extends RecyclerView.ViewHolder {

        public TextView sensorStatsTextView;
        public TextView sensorNameTextView;

        public PulsatorLayout sensorPulsatorLayout;

        public SensorViewHolder(View view) {
            super(view);

            sensorStatsTextView = view.findViewById(R.id.sensor_stats);
            sensorNameTextView = view.findViewById(R.id.sensor_name);
            sensorPulsatorLayout = view.findViewById(R.id.sensor_active_pulsator);
        }
    }

}
