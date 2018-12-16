package graziano.giuseppe.thermostat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import graziano.giuseppe.thermostat.data.model.Program;
import graziano.giuseppe.thermostat.data.model.Sensor;
import graziano.giuseppe.thermostat.data.model.Thermostat;
import graziano.giuseppe.thermostat.data.model.User;
import graziano.giuseppe.thermostat.fragment.ProgramDialogFragment;
import graziano.giuseppe.thermostat.fragment.SensorGraphFragment;
import graziano.giuseppe.thermostat.fragment.SensorsFragment;
import graziano.giuseppe.thermostat.fragment.SettingsFragment;
import graziano.giuseppe.thermostat.fragment.ThermostatFragmentManual;
import graziano.giuseppe.thermostat.fragment.ThermostatFragmentProgram;
import graziano.giuseppe.thermostat.network.HttpClient;

public class
MainActivity extends AppCompatActivity implements SensorsFragment.OnListFragmentInteractionListener, ThermostatFragmentProgram.OnListFragmentInteractionListener{

    public static User user = new User();
    public long selectedItem = 0;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:

                    Thermostat thermostat = MainActivity.user.getSelectedThermostat();
                    if(thermostat != null && selectedItem == item.getItemId()){
                        for (Thermostat t: MainActivity.user.getThermostats()){
                            if(t.getId() != thermostat.getId()) {
                                MainActivity.user.setSelectedThermostatId(t.getId());
                                thermostat = user.getSelectedThermostat();
                                HttpClient.putUserThermostat(MainActivity.user.getSelectedThermostatId(), null, null);
                                break;
                            }
                        }

                       /* if(thermostat.getMode().equals(Thermostat.PROGRAM_MODE)){
                            Fragment thermostatFragment = ThermostatFragmentProgram.newInstance();
                            openFragment(thermostatFragment);
                            return true;
                        }
                        if(thermostat.getMode().equals(Thermostat.MANUAL_MODE)){
                            Fragment thermostatFragment = ThermostatFragmentManual.newInstance();
                            openFragment(thermostatFragment);
                            return true;
                        }*/
                    }

                        Fragment thermostatFragment = ThermostatFragmentManual.newInstance();
                        openFragment(thermostatFragment);

                    selectedItem = item.getItemId();
                    return true;
                case R.id.navigation_sensors:
                    Fragment sensorFragment = SensorsFragment.newInstance();
                    openFragment(sensorFragment);
                    selectedItem = item.getItemId();
                    return true;
                case R.id.navigation_settings:
                    Fragment settingsFragment = SettingsFragment.newInstance();
                    openFragment(settingsFragment);
                    selectedItem = item.getItemId();
                    return true;

            }

            return false;
        }
    };


    @Override
    public void onResume(){
        super.onResume();



        Thermostat thermostat = MainActivity.user.getSelectedThermostat();
        if(thermostat != null){

            Fragment thermostatFragment = ThermostatFragmentManual.newInstance();
            openFragment(thermostatFragment);
           /* if(thermostat.getMode().equals(Thermostat.PROGRAM_MODE)){
                Fragment thermostatFragment = ThermostatFragmentProgram.newInstance();
                openFragment(thermostatFragment);
            }
            if(thermostat.getMode().equals(Thermostat.MANUAL_MODE)){
                Fragment thermostatFragment = ThermostatFragmentManual.newInstance();
                openFragment(thermostatFragment);
            }*/
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        HttpClient.initialize(this);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        user.setUsername(sharedPreferences.getString(User.PREFERENCE_USERNAME, ""));
        user.setPassword(sharedPreferences.getString(User.PREFERENCE_PASSWORD, ""));

        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivity(loginIntent);



    }

    private void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
      //  transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void OnListFragmentInteractionListener(Sensor sensor) {
        if(sensor!= null) {

            FragmentManager fragmentManager = getSupportFragmentManager();
            if(fragmentManager.getFragments().size() == 1) {
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment sensorGraphFragment = SensorGraphFragment.newInstance(sensor);
                fragmentTransaction.add(R.id.container, sensorGraphFragment);
                fragmentTransaction.addToBackStack(sensorGraphFragment.toString());
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                fragmentTransaction.commit();
            }
        }

    }

    @Override
    public void OnListFragmentInteractionListener(Program program) {
        FragmentManager fm = getSupportFragmentManager();
        ProgramDialogFragment editNameDialogFragment = ProgramDialogFragment.newInstance(program);
        editNameDialogFragment.show(fm, "fragment_edit_name");
    }
}
