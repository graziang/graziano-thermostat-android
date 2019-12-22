package graziano.giuseppe.thermostat.data.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalTime;


public class Program implements Serializable{


    private long id = 0L;

    private String name;
    private String description;
    private boolean active;
    private boolean repete;
    private DayOfWeek weekDay;

    private LocalTime startTime;

    private LocalTime endTime;
    private long sourceId;
    private boolean sourceOn;
    @JsonIgnore
    private ProgramMode programMode;

    private float temperature;


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

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isRepete() {
        return repete;
    }

    public void setRepete(boolean repete) {
        this.repete = repete;
    }

    public DayOfWeek getWeekDay() {
        return weekDay;
    }

    public void setWeekDay(DayOfWeek weekDay) {
        this.weekDay = weekDay;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public long getSourceId() {
        return sourceId;
    }

    public void setSourceId(long sourceId) {
        this.sourceId = sourceId;
    }

    public boolean isSourceOn() {
        return sourceOn;
    }

    public void setSourceOn(boolean sourceOn) {
        this.sourceOn = sourceOn;
    }

    public ProgramMode getProgramMode() {
        return programMode;
    }

    public void setProgramMode(ProgramMode programMode) {
        this.programMode = programMode;
    }


    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Program program = (Program) o;
        return id == program.id;
    }

}
