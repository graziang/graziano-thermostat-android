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
import com.google.gson.reflect.TypeToken;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import graziano.giuseppe.thermostat.data.model.Measurement;
import graziano.giuseppe.thermostat.data.model.Thermostat;

public class MeasurementListRequest extends BasicAuthRequest<List<Measurement>> {

    private Gson gson = new Gson();

    public MeasurementListRequest(int method, String url, String requestBody, Listener listener, ErrorListener errorListener, String username, String password) {
        super(method, url, requestBody, listener, errorListener, username, password);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map headers = super.getHeaders();
        headers.put("className", Thermostat.class.getName());
        return headers;
    }

    @Override
    protected Response<List<Measurement>> parseNetworkResponse(NetworkResponse response) {

        try {
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));

            List<Measurement> measurements = gson.fromJson(jsonString, new TypeToken<List<Measurement>>(){}.getType());

            return Response.success(measurements,
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    public int compareTo(@NonNull Request<List<Measurement>>o) {
        return super.compareTo((Request) o);
    }
}