package graziano.giuseppe.thermostat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;

import java.util.Date;

public class DataUtils {

    //Get string date miss
    public static String printDifference(Date startDate, Date endDate) {
        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        //different = different % minutesInMilli;

        //for seconds
        // long elapsedSeconds = different / secondsInMilli;

        String dateDifferenceString = "";

        if(elapsedDays > 0){
            dateDifferenceString += elapsedDays + "giorni, ";
        }
        if(elapsedHours > 0){
            dateDifferenceString += elapsedHours + "ore, ";
        }
        if(elapsedMinutes >= 0){
            dateDifferenceString += elapsedMinutes + " minuti fa";
        }
        if(elapsedMinutes == 0){
            return "ora";
        }
        return dateDifferenceString;
    }

}
