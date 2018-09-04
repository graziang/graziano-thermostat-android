package graziano.giuseppe.thermostat.data.model;

public class ManualMode {

    public ManualMode(){
        this.sensorId = -1;
    }


    private long id;

    private boolean active;

    private Thermostat thermostat;

    private long sensorId;

    private boolean avg;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Thermostat getThermostat() {
        return thermostat;
    }

    public void setThermostat(Thermostat thermostat) {
        this.thermostat = thermostat;
    }

    public long getSensorId() {
        return sensorId;
    }

    public void setSensorId(long sensorId) {
        this.sensorId = sensorId;
    }

    public boolean isAvg() {
        return avg;
    }

    public void setAvg(boolean avg) {
        this.avg = avg;
    }
}
