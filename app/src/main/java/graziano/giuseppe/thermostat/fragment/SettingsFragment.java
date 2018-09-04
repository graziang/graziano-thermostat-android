package graziano.giuseppe.thermostat.fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v14.preference.MultiSelectListPreference;
import android.support.v14.preference.SwitchPreference;
import android.support.v4.app.Fragment;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;


import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import graziano.giuseppe.thermostat.LoginActivity;
import graziano.giuseppe.thermostat.MainActivity;
import graziano.giuseppe.thermostat.R;
import graziano.giuseppe.thermostat.data.model.Thermostat;
import graziano.giuseppe.thermostat.data.model.User;
import graziano.giuseppe.thermostat.network.HttpClient;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener, Response.Listener, Response.ErrorListener {
    private static final String TAG = SettingsFragment.class.getSimpleName();

    public final static String PREFERENCE_KEY_LOGIN = "preference_login";
    public final static String PREFERENCE_KEY_THERMOSTAT_SELECTED = "preference_thermostat_selected";
    public final static String PREFERENCE_KEY_THERMOSTAT_ACTIVE = "preference_thermostat_active";
    public final static String PREFERENCE_KEY_THERMOSTAT_MODE_SELECTED = "preference_thermostat_mode_selected";

    SharedPreferences sharedPreferences;

    public static Fragment newInstance(){
        return new SettingsFragment();
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        //add xml
        addPreferencesFromResource(R.xml.preferences);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        onSharedPreferenceChanged(sharedPreferences, getString(R.string.title_settings));

        Preference loginPreference = (Preference) findPreference(PREFERENCE_KEY_LOGIN);
        loginPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                displayConfirmAlertDialog();
                return true;
            }
        });
    }

    public void displayConfirmAlertDialog()
    {

        final AlertDialog.Builder builder =  new AlertDialog.Builder(getActivity());
        final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
        final String message = getString(R.string.login_request);

        builder.setMessage(message)
                .setPositiveButton(getString(R.string.button_ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                logoutAndNewLogin();
                                d.dismiss();
                            }
                        })
                .setNegativeButton(getString(R.string.button_cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                d.cancel();
                            }
                        });
        builder.create().show();
    }


    private void logoutAndNewLogin(){
        MainActivity.user.setUsername("");
        MainActivity.user.setPassword("");

        SharedPreferences.Editor editor = android.preference.PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();

        editor.putString(User.PREFERENCE_USERNAME, MainActivity.user.getUsername());
        editor.putString(User.PREFERENCE_PASSWORD, MainActivity.user.getPassword());
        editor.commit();
        Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
        startActivity(loginIntent);
    }

    @Override
    public void onResume() {
        super.onResume();
        //unregister the preferenceChange listener
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);

        initializePreferences();
    }

    @TargetApi(Build.VERSION_CODES.N)
    private void initializePreferences(){
        Preference loginPreference = findPreference(PREFERENCE_KEY_LOGIN);
        loginPreference.setSummary(MainActivity.user.getUsername());

        if(MainActivity.user.getThermostats() == null){
            return;
        }

        ListPreference thermostatSelectPreference = (ListPreference) findPreference(PREFERENCE_KEY_THERMOSTAT_SELECTED);
        List<String> thermostatNames = new ArrayList();
        List<String> thermostatIds = new ArrayList();
        List<Thermostat> thermostats = new ArrayList<>(MainActivity.user.getThermostats());
        Collections.sort(thermostats, new Comparator<Thermostat>() {
            @Override
            public int compare(Thermostat t1, Thermostat t2) {
                return t1.getName().compareTo(t2.getName());
            }
        });
        MainActivity.user.setThermostats(new HashSet<>(thermostats));
        if(thermostats != null){
            for (Thermostat thermostat: thermostats){
                thermostatNames.add(thermostat.getName());
                thermostatIds.add(String.valueOf(thermostat.getId()));
            }
        }
        final CharSequence[] entriesNames = thermostatNames.toArray(new CharSequence[thermostatNames.size()]);
        final CharSequence[] entriesIds = thermostatIds.toArray(new CharSequence[thermostatIds.size()]);
        thermostatSelectPreference.setEntries(entriesNames);
        thermostatSelectPreference.setEntryValues(entriesIds);


        SwitchPreference switchThermostatActivePreference = (SwitchPreference) findPreference(PREFERENCE_KEY_THERMOSTAT_ACTIVE);
        ListPreference thermostatModeSelectPreference = (ListPreference) findPreference(PREFERENCE_KEY_THERMOSTAT_MODE_SELECTED);
        thermostatSelectPreference.setSummary(R.string.thermostat_selection_empty);

        Thermostat selectedThermostat = new Thermostat();
        if(MainActivity.user.getSelectedThermostatId() != null){
            selectedThermostat = MainActivity.user.getSelectedThermostat();
            thermostatSelectPreference.setSummary(selectedThermostat.getName());
            thermostatSelectPreference.setValueIndex(thermostatIds.indexOf(String.valueOf(selectedThermostat.getId())));
            List entriesValues = Arrays.asList(thermostatModeSelectPreference.getEntryValues());
            thermostatModeSelectPreference.setSummary(thermostatModeSelectPreference.getEntries()[entriesValues.indexOf(selectedThermostat.getMode())]);
            thermostatModeSelectPreference.setValueIndex(entriesValues.indexOf(selectedThermostat.getMode()));
        }
        switchThermostatActivePreference.setChecked(selectedThermostat.isActive());




    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        Thermostat selectedThermostat = MainActivity.user.getSelectedThermostat();

        if(key.equals(PREFERENCE_KEY_LOGIN)){

        }
        else if (key.equals(PREFERENCE_KEY_THERMOSTAT_ACTIVE)){
            if(selectedThermostat != null) {
                SwitchPreference switchThermostatActivePreference = (SwitchPreference) findPreference(PREFERENCE_KEY_THERMOSTAT_ACTIVE);
                selectedThermostat.setActive(switchThermostatActivePreference.isChecked());
                HttpClient.putThermostat(selectedThermostat, this, this);
            }
        }
        else if (key.equals(PREFERENCE_KEY_THERMOSTAT_SELECTED)){
            final ListPreference thermostatSelectPreference = (ListPreference) findPreference(PREFERENCE_KEY_THERMOSTAT_SELECTED);
            MainActivity.user.setSelectedThermostatId(Long.valueOf(thermostatSelectPreference.getValue()));
            thermostatSelectPreference.setSummary(thermostatSelectPreference.getEntry());
            initializePreferences();
            HttpClient.putUserThermostat( MainActivity.user.getSelectedThermostatId(), this, this);
        }
        else if (key.equals(PREFERENCE_KEY_THERMOSTAT_MODE_SELECTED)){
            ListPreference thermostatModeSelectPreference = (ListPreference) findPreference(PREFERENCE_KEY_THERMOSTAT_MODE_SELECTED);
            selectedThermostat.setMode(thermostatModeSelectPreference.getValue());
            thermostatModeSelectPreference.setSummary(thermostatModeSelectPreference.getEntry());
            HttpClient.putThermostat(selectedThermostat, this, this);

        }


    }

    @Override
    public void onPause() {
        super.onPause();
        //unregister the preference change listener
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onErrorResponse(VolleyError error) {

    }

    @Override
    public void onResponse(Object response) {

    }
}
