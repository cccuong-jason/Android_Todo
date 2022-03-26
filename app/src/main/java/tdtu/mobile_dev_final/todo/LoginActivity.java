package tdtu.mobile_dev_final.todo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.apachat.loadingbutton.core.customViews.CircularProgressButton;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    TextInputLayout textEmailLayout, textPasswordLayout;
    TextInputEditText textInputEmail, textInputPassword;
    CircularProgressButton btnLogin;
    RequestQueue requestQueue;
    SharedPreferences sp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        setContentView(R.layout.login_activity);

        // Init Login View Components
        init();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (textInputEmail.getText().toString().isEmpty()  || textInputPassword.getText().toString().isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please fill both email & password", Toast.LENGTH_SHORT).show();
                } else {
                    btnLogin.startAnimation();
                    callLoginApi();
                }
            }
        });
    }

    public void init() {
       textEmailLayout = findViewById(R.id.tlEmail);
       textPasswordLayout = findViewById(R.id.tlPassword);
       textInputEmail = findViewById(R.id.tiEmail);
       textInputPassword = findViewById(R.id.tiPassword);
       btnLogin = (CircularProgressButton) findViewById(R.id.btnLogin);
       sp = getSharedPreferences("TodoPrefs", Context.MODE_PRIVATE);
    }

    private class startAnimation extends AsyncTask<String, String, String> {
        protected String doInBackground(String... params) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
            return "done";
        }

        protected void onPostExecute(String s) {
            if (s.equals("done")) {

                btnLogin.doneLoadingAnimation(ContextCompat.getColor(LoginActivity.this, R.color.title), bitmapConvert(getResources().getDrawable(R.drawable.ic_check)));
                new android.os.Handler(Looper.getMainLooper()).postDelayed(
                        new Runnable() {
                            public void run() {
                                btnLogin.revertAnimation();
                                Intent splash = new Intent(LoginActivity.this, DashboardActivity.class);
                                startActivity(splash);
                                Animatoo.animateFade(LoginActivity.this);
                            }
                        },
                        1000);
            }
        }
    }

    private void callLoginApi() {

        RequestQueue queue = RequestSingleton.getInstance(this.getApplicationContext()).getRequestQueue();
        String url = "http://192.168.1.212:8000/api/sessions";
        JSONObject jsonBody = new JSONObject();

        try {
            jsonBody.put("email", textInputEmail.getText().toString());
            jsonBody.put("password", textInputPassword.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String requestBody = jsonBody.toString();

        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            String accessToken = response.getString("accessToken");
                            String refreshToken = response.getString("refreshToken");
                            SharedPreferences.Editor editor = sp.edit();

                            editor.putString("accessToken", accessToken);
                            editor.putString("refreshToken", refreshToken);
                            editor.commit();

                            new startAnimation().execute();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse localNetworkResponse = error.networkResponse;
                new Handler(Looper.getMainLooper()).postDelayed(
                        new Runnable() {
                            public void run() {
                                parseVolleyError(error);
                                btnLogin.doneLoadingAnimation(ContextCompat.getColor(LoginActivity.this, R.color.title), bitmapConvert(getResources().getDrawable(R.drawable.ic_cancel)));
                            }
                        },
                        1500);
                new Handler(Looper.getMainLooper()).postDelayed(
                        new Runnable() {
                            public void run() {
                                btnLogin.revertAnimation();
                            }
                        },
                        2000);
            }
        }
        ){
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/json; charset=utf-8");
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


            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                try {
                    String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                    return Response.success(new JSONObject(jsonString), HttpHeaderParser.parseCacheHeaders(response));

                } catch (UnsupportedEncodingException e) {

                    Log.i("UEE", String.valueOf(e));
                    return Response.error(new ParseError(e));

                } catch (JSONException je) {

                    Log.i("JE", String.valueOf(je));
                    return Response.error(new ParseError(je));
                }
            }
        };

        RequestSingleton.getInstance(this).addToRequestQueue(objectRequest);

    }

    public void parseVolleyError(VolleyError error) {
        try {
            String responseBody = new String(error.networkResponse.data, "utf-8");
            Log.i("ParseVolleyError", responseBody);
            JSONObject data = new JSONObject(responseBody);
            Log.i("ParseVolleyError", String.valueOf(data));
            String message = data.getString("message");
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        } catch (JSONException e) {

            Log.e("JSONException", e.getMessage());

        } catch (UnsupportedEncodingException uee) {

            Log.e("UEE", uee.getMessage());
        }
    }

    public Bitmap bitmapConvert(Drawable drawable) {
        try {
            Bitmap bitmap;

            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);

            return bitmap;

        } catch (OutOfMemoryError e) {
            // Handle the error
            return null;
        }
    }
}
