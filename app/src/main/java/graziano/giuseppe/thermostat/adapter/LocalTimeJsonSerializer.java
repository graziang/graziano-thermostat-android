package graziano.giuseppe.thermostat.adapter;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Build;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.time.LocalTime;

public class LocalTimeJsonSerializer implements JsonSerializer<LocalTime> {


    @SuppressLint("NewApi")
    @Override
    public JsonElement serialize(LocalTime src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.toString());
    }
}