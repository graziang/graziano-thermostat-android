package graziano.giuseppe.thermostat.network.request;

import android.support.annotation.NonNull;
import android.util.Base64;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonRequest;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class BasicAuthRequest<T> extends JsonRequest<T> {

    private String username;
    private String password;


    public BasicAuthRequest(int method, String url, String requestBody, Response.Listener listener, Response.ErrorListener errorListener, String username, String password) {
        super(method, url, requestBody, listener, errorListener);
        this.username = username;
        this.password = password;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {

        Map<String, String> headers = new HashMap<>();
        String key = "Authorization";
        String encodedString = Base64.encodeToString(String.format("%s:%s", username, password).getBytes(), Base64.NO_WRAP);
        String value = String.format("Basic %s", encodedString);
        headers.put(key, value);
        return headers;
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
       return (Response<T>) Response.success("",null);
    }

    /*


       @Override
    protected Response<Object> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
            Object jsonObject = gson.fromJson(jsonString, Object.class);

            return Response.success(jsonObject,
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }
     */
    @Override
    public int compareTo(@NonNull Request<T> o) {
        return super.compareTo((Request) o);
    }
}