package graziano.giuseppe.thermostat.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import graziano.giuseppe.thermostat.MainActivity;
import graziano.giuseppe.thermostat.R;
import graziano.giuseppe.thermostat.data.model.Measurement;
import graziano.giuseppe.thermostat.data.model.Thermostat;


public class ThermostatSensorRecyclerViewAdapter extends RecyclerView.Adapter<ThermostatSensorRecyclerViewAdapter.SensorMeasurement> {

    private final Context context;
    private List<Measurement> measurements;


    public ThermostatSensorRecyclerViewAdapter(List<Measurement> list, Context context) {
        this.measurements = list;
        this.context = context;
    }

    @Override
    public SensorMeasurement onCreateViewHolder(ViewGroup parent, int index) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.thermostat_sensor_item, parent, false);


        return new SensorMeasurement(view);
    }


    @Override
    public void onBindViewHolder(final SensorMeasurement holder, final int position) {

        Measurement measurement = this.measurements.get(position);
        Drawable background = holder.itemView.getBackground();
        TextView sensorMeasurementLastTextView = holder.itemView.findViewById(R.id.sensor_measurement_last);
        TextView sensorNameTextView = holder.itemView.findViewById(R.id.sensor_name);

        int color = this.context.getResources().getColor(R.color.colorTextPrimary);

        if(measurement.isAvg()) {
            holder.sensorNameTextView.setText(this.context.getResources().getString(R.string.sensor_avg));

        }
        else {
            holder.sensorNameTextView.setText(measurement.getSensor().getName());
        }

        if( measurement.getTemperature() < 0){
            System.out.println(measurement.getTemperature());
        }

        BigDecimal bd = null;
        try {
            bd = new BigDecimal( measurement.getTemperature()).setScale(1, RoundingMode.HALF_EVEN);

        }
        catch (Exception e){
            System.out.println(measurement.getTemperature());
            return;
        }

        holder.lastTemperatureMeasurementTextView.setText(String.format("%s°C", bd.floatValue()));


        sensorMeasurementLastTextView.setTypeface(null, Typeface.NORMAL);
      //  sensorNameTextView.setTypeface(null, Typeface.NORMAL);


        Thermostat thermostat = MainActivity.user.getSelectedThermostat();

        if(thermostat != null) {
          //  if (thermostat.getMode().equals(Thermostat.MANUAL_MODE)) {
                Long sensorId = thermostat.getManualMode().getSensorId();
                if (thermostat.getManualMode().isAvg() && measurement.isAvg()) {
                    sensorMeasurementLastTextView.setTypeface(null, Typeface.BOLD);
                    //  sensorNameTextView.setTypeface(null, Typeface.BOLD);
                    color = this.context.getResources().getColor(R.color.colorPrimary);
                }
                if (!thermostat.getManualMode().isAvg() && measurement.getSensor() != null && measurement.getSensor().getId() == sensorId) {
                    sensorMeasurementLastTextView.setTypeface(null, Typeface.BOLD);
                    // sensorNameTextView.setTypeface(null, Typeface.BOLD);
                    color = this.context.getResources().getColor(R.color.colorPrimary);
                }
         //   }
        }



        ((GradientDrawable) background).setStroke(10, color);
        sensorMeasurementLastTextView.setTextColor(color);
        sensorNameTextView.setTextColor(color);
      //  ((GradientDrawable) background).setColor(this.context.getResources().getColor(R.color.colorAccent));


    }

    @Override
    public int getItemCount() {
        return measurements.size();
    }



    public class SensorMeasurement extends RecyclerView.ViewHolder {

        public TextView lastTemperatureMeasurementTextView;
        public TextView sensorNameTextView;

        public SensorMeasurement(View view) {
            super(view);
            lastTemperatureMeasurementTextView = view.findViewById(R.id.sensor_measurement_last);
            sensorNameTextView = view.findViewById(R.id.sensor_name);
        }
    }


}
