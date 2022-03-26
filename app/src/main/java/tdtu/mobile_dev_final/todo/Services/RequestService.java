package tdtu.mobile_dev_final.todo.Services;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tdtu.mobile_dev_final.todo.RequestSingleton;
import tdtu.mobile_dev_final.todo.Todo;
import tdtu.mobile_dev_final.todo.Utils.ToolsFunction;

public class RequestService {

    private static List<Todo> listTodo;
    private RequestQueue queue;
    private String url;
    private String method;
    private JSONArray jsonArray;
    private String accessToken, refreshToken;
    private JSONObject paginatorInfo;

    public JSONObject getPaginatorInfo() {
        return paginatorInfo;
    }

    ToolsFunction toolsFunction = new ToolsFunction();
    Gson gson = new Gson();

    public RequestService(RequestQueue requestQueue, String requestUrl, String requestMethod, String requestAccessToken, String requestRefreshToken) {
        this.queue = requestQueue;
        this.url = requestUrl;
        this.method = requestMethod;
        this.accessToken = requestAccessToken;
        this.refreshToken = requestRefreshToken;
    }

    public RequestQueue getQueue() {
        return queue;
    }

    public String getUrl() {
        return url;
    }

    public String getMethod() {
        return method;
    }

    public static List<Todo> getListTodo() {
        return listTodo;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setQueue(RequestQueue queue) {
        this.queue = queue;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setListTodo(List<Todo> listTodo) {
        this.listTodo = listTodo;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void setPaginatorInfo(JSONObject paginatorInfo) {
        this.paginatorInfo = paginatorInfo;
    }

    public final void makeRequest(Context context) {
        SharedPreferences sp = context.getSharedPreferences("TodoPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        JsonObjectRequest objectRequest = new JsonObjectRequest(toolsFunction.getRequestMethod(method), url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            JSONObject headers = response.getJSONObject("headers");

                            if (headers.has("x-access-token")) {
                                String x_access_token = headers.getString("x-access-token");
                                toolsFunction.setToken(context, x_access_token);
                            }

                            jsonArray =  response.getJSONArray("content");
                            paginatorInfo = (JSONObject) jsonArray.remove(8);
                            listTodo = Todo.mapData(jsonArray);

                            Log.i("PaginatorInfoIn", String.valueOf(paginatorInfo));

                            editor.putString("todoListItem", gson.toJson(listTodo)) ;
                            editor.putString("paginator", gson.toJson(paginatorInfo));
                            editor.apply();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Error Detected", Toast.LENGTH_SHORT).show();
            }
        }
        ){

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/json; charset=utf-8");
                params.put("x-refresh", refreshToken);
                params.put("Authorization", "Bearer " + accessToken);
                return params;
            }

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                try {
                    String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));

                    JSONObject jsonResponse = new JSONObject(jsonString);
                    assert response.headers != null;
                    jsonResponse.put("headers", new JSONObject(response.headers));

                    return Response.success(jsonResponse, HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    return Response.error(new ParseError(e));
                } catch (JSONException je) {
                    je.printStackTrace();
                    return Response.error(new ParseError(je));
                }
            }
        };

        RequestSingleton.getInstance(context).addToRequestQueue(objectRequest);
    }
}
