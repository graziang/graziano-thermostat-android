package graziano.giuseppe.thermostat.data.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;


public class Sensor implements Serializable {


    private long id;

    private String name;

    private String description;

    private boolean active;


    private Thermostat thermostat;

    private Set<Measurement> measurements = new HashSet<>();

    private SensorStats sensorStats;

    public Sensor() {
    }

    public Sensor(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Thermostat getThermostat() {
        return thermostat;
    }

    public void setThermostat(Thermostat thermostat) {
        this.thermostat = thermostat;
    }

    public Set<Measurement> getMeasurements() {
        return measurements;
    }

    public void setMeasurements(Set<Measurement> measurements) {
        this.measurements = measurements;
    }

    public SensorStats getSensorStats() {
        return sensorStats;
    }

    public void setSensorStats(SensorStats sensorStats) {
        this.sensorStats = sensorStats;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sensor sensor = (Sensor) o;
        return id == sensor.id;
    }

}