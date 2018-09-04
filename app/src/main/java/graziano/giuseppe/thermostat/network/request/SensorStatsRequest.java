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

import java.io.UnsupportedEncodingException;
import java.util.Map;

import graziano.giuseppe.thermostat.data.model.SensorStats;
import graziano.giuseppe.thermostat.data.model.Thermostat;

public class SensorStatsRequest extends BasicAuthRequest<SensorStats> {

    private Gson gson = new Gson();

    public SensorStatsRequest(int method, String url, String requestBody, Listener listener, ErrorListener errorListener, String username, String password) {
        super(method, url, requestBody, listener, errorListener, username, password);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map headers = super.getHeaders();
        headers.put("className", Thermostat.class.getName());
        return headers;
    }

    @Override
    protected Response<SensorStats> parseNetworkResponse(NetworkResponse response) {

        try {
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));

            SensorStats fam = gson.fromJson(jsonString, SensorStats.class);

            return Response.success(fam,
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    public int compareTo(@NonNull Request<SensorStats> o) {
        return super.compareTo((Request) o);
    }
}