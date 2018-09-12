package graziano.giuseppe.thermostat;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import graziano.giuseppe.thermostat.data.model.Sensor;
import graziano.giuseppe.thermostat.data.model.Thermostat;
import graziano.giuseppe.thermostat.data.model.User;
import graziano.giuseppe.thermostat.fragment.SensorGraphFragment;
import graziano.giuseppe.thermostat.fragment.SensorsFragment;
import graziano.giuseppe.thermostat.fragment.SettingsFragment;
import graziano.giuseppe.thermostat.fragment.ThermostatFragment;
import graziano.giuseppe.thermostat.network.HttpClient;
import graziano.giuseppe.thermostat.network.request.BasicAuthRequest;

public class
MainActivity extends AppCompatActivity implements SensorsFragment.OnListFragmentInteractionListener{

    public static User user = new User();

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    Fragment thermostatFragment = ThermostatFragment.newInstance();
                    openFragment(thermostatFragment);
                    return true;
                case R.id.navigation_sensors:
                    Fragment sensorFragment = SensorsFragment.newInstance();
                    openFragment(sensorFragment);
                    return true;
                case R.id.navigation_settings:
                    Fragment settingsFragment = SettingsFragment.newInstance();
                    openFragment(settingsFragment);
                    return true;

            }
            return false;
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Fragment thermostatFragment = ThermostatFragment.newInstance();
        openFragment(thermostatFragment);

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
}
