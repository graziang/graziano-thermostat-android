package graziano.giuseppe.thermostat.network.request;

import android.support.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.UnsupportedEncodingException;
import java.time.LocalTime;
import java.util.Map;

import graziano.giuseppe.thermostat.adapter.LocalTimeJsonDeserializer;
import graziano.giuseppe.thermostat.data.model.Thermostat;

public class ThermostatRequest extends BasicAuthRequest<Thermostat> {

    private Gson gson = new GsonBuilder().registerTypeAdapter(LocalTime.class, new LocalTimeJsonDeserializer()).create();


    public ThermostatRequest(int method, String url, String requestBody, Listener listener, ErrorListener errorListener, String username, String password) {
        super(method, url, requestBody, listener, errorListener, username, password);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map headers = super.getHeaders();
        headers.put("className", Thermostat.class.getName());
        return headers;
    }

    @Override
    protected Response<Thermostat> parseNetworkResponse(NetworkResponse response) {

        try {
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));

            Thermostat fam = gson.fromJson(jsonString, Thermostat.class);

            return Response.success(fam,
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    public int compareTo(@NonNull Request<Thermostat> o) {
        return super.compareTo((Request) o);
    }
}