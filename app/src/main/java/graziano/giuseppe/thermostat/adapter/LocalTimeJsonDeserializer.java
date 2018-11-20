package graziano.giuseppe.thermostat.adapter;

import android.annotation.TargetApi;
import android.os.Build;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.time.LocalTime;

public class LocalTimeJsonDeserializer implements JsonDeserializer<LocalTime> {



    @TargetApi(Build.VERSION_CODES.O)
    @Override
    public LocalTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
       String date = json.getAsString();
       String hourString = date.substring(0, date.indexOf(":"));
       int hour = Integer.valueOf(hourString);
       String minuteString = date.substring(date.indexOf(":") + 1, date.lastIndexOf(":"));
       int minute = Integer.valueOf(minuteString);
       return LocalTime.of(hour, minute);
    }
}