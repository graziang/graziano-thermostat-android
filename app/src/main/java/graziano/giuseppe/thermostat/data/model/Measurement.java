package graziano.giuseppe.thermostat.data.model;

import java.util.Date;

public class Measurement{

    private long id;

    private Date date;

    private float temperature;

    private Sensor sensor;

    private boolean avg = false;

    public Measurement() {
    }

    public Measurement(Sensor sensor, float temperature) {
        this.temperature = temperature;
        this.sensor = sensor;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public Sensor getSensor() {
        return sensor;
    }

    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
    }

    public boolean isAvg() {
        return avg;
    }

    public void setAvg(boolean avg) {
        this.avg = avg;
    }
}
