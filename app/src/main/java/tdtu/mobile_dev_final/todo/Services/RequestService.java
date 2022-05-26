package tdtu.mobile_dev_final.todo.Services;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
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

import cn.pedant.SweetAlert.SweetAlertDialog;
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
    private String type;

    public JSONObject getPaginatorInfo() {
        return paginatorInfo;
    }

    ToolsFunction toolsFunction = new ToolsFunction();
    Gson gson = new Gson();

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public RequestService(RequestQueue requestQueue, String requestUrl, String requestMethod, String requestAccessToken, String requestRefreshToken, String type) {
        this.queue = requestQueue;
        this.url = requestUrl;
        this.method = requestMethod;
        this.accessToken = requestAccessToken;
        this.refreshToken = requestRefreshToken;
        this.type = type;
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
        JSONObject jsonBody = new JSONObject();

        if (type == "EditTodo") {
            try {
                String name = sp.getString("updateTitle", "");
                String description = sp.getString("updateDescription", "");
                String startDate = sp.getString("updateStartDate", "");
                String endDate = sp.getString("updateEndDate", "");

                boolean status = sp.getBoolean("updateStatus", false);

                if (!TextUtils.isEmpty(name)) {
                    jsonBody.put("name", name);
                }

                if (!TextUtils.isEmpty(description)) {
                    jsonBody.put("description", description);
                }

                if (!TextUtils.isEmpty(startDate)) {
                    jsonBody.put("startDate", startDate);
                }

                if (!TextUtils.isEmpty(endDate)) {
                    jsonBody.put("endDate", endDate);
                }

                if (status) {
                    jsonBody.put("status", status);
                }

                Log.i("FinalJson", jsonBody.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        final String requestBody = jsonBody.toString();

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

                            if (type == "AllTodo") {
                                jsonArray =  response.getJSONArray("content");
                                paginatorInfo = (JSONObject) jsonArray.remove(jsonArray.length() -1);
                                listTodo = Todo.mapData(jsonArray);

                                editor.putString("todoListItem", gson.toJson(listTodo)) ;
                                editor.putString("paginator", gson.toJson(paginatorInfo));
                                editor.apply();
                            } else if (type == "EditTodo") {
                                String message =  response.getString("type");
                                Integer code = response.getInt("code");

                                if (code.equals(200)) {
                                    new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                                            .setTitleText("Successfully!")
                                            .setContentText("Update successfully!")
                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sDialog) {
                                                    sDialog.dismissWithAnimation();
                                                }
                                            })
                                            .show();
                                }
                            }

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
            public byte[] getBody() {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                    return null;
                }
            }

//            @Nullable
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> params = new HashMap<String, String>();
////                params.put("", "");
////                params.put("")
//            }

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

