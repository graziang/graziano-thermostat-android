package graziano.giuseppe.thermostat.data.model;


import java.util.Set;


public class User {

    public static String PREFERENCE_USERNAME = "PREFERENCE_USERNAME";
    public static String PREFERENCE_PASSWORD = "PREFERENCE_PASSWORD";

    private Long id;

    private String username;

    private String password;

    private boolean isAdmin;

    private Long selectedThermostatId;

    private Set<Thermostat> thermostats;

    public User() {
        super();
    }

    //
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public Long getSelectedThermostatId() {
        return selectedThermostatId;
    }

    public void setSelectedThermostatId(Long selectedThermostatId) {
        this.selectedThermostatId = selectedThermostatId;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Thermostat> getThermostats() {
        return thermostats;
    }

    public void setThermostats(Set<Thermostat> thermostats) {
        this.thermostats = thermostats;
    }

    public Thermostat getSelectedThermostat(){
        if(this.selectedThermostatId == null) {
            return null;
        }
        if(this.thermostats != null){
            for (Thermostat t: this.thermostats){
                if(t.getId() == this.selectedThermostatId){
                    return t;
                }
            }
        }

        return null;

    }
}