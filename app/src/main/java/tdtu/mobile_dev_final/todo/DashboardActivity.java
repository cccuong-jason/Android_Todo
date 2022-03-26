package tdtu.mobile_dev_final.todo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import com.android.volley.RequestQueue;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import org.json.JSONException;
import org.json.JSONObject;
import java.lang.reflect.Type;
import java.util.List;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;
import tdtu.mobile_dev_final.todo.Adapter.TodoAdapter;
import tdtu.mobile_dev_final.todo.Services.RequestService;
import tdtu.mobile_dev_final.todo.Utils.ToolsFunction;

public class DashboardActivity extends AppCompatActivity implements TodoAdapter.OnTodoListener {

    String accessToken, refreshToken;
    List<Todo> listTodo;
    RecyclerView rvTodos;
    ToolsFunction toolsFunction;
    TodoAdapter adapter;
    TextView tvTaskProgress, tvProgressBar;
    CircularProgressBar taskProgressBar;
    MaterialButton btnCount;
    ItemTouchHelper.SimpleCallback simpleCallback;
    SharedPreferences sp;
    RecyclerView.RecycledViewPool sharedPool;
    Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        setContentView(R.layout.dashboard_activity);

        init();

        try {
            getAllTask();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                final int position = viewHolder.getAdapterPosition();
                Todo deletedTodo = null;

                switch (direction) {
                    case ItemTouchHelper.LEFT:
                        deletedTodo = listTodo.get(position);
                        listTodo.remove(deletedTodo);
                        adapter.notifyItemRemoved(position);

                        Todo finalDeletedTodo = deletedTodo;
                        Snackbar.make(rvTodos, deletedTodo.getName(), Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                listTodo.add(position, finalDeletedTodo);
                                adapter.notifyItemInserted(position);
                            }
                        }).show();
                        break;
                    case ItemTouchHelper.RIGHT:

                        break;
                }
            }

            @Override
            public void onChildDraw (Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,float dX, float dY,int actionState, boolean isCurrentlyActive){
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftActionIcon(R.drawable.ic_checklist)
                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(DashboardActivity.this, R.color.pastelGreen))
                        .addSwipeLeftLabel("Done Task")
                        .setSwipeLeftActionIconTint(ContextCompat.getColor(DashboardActivity.this, R.color.doneText))
                        .setSwipeLeftLabelColor(ContextCompat.getColor(DashboardActivity.this, R.color.doneText))
                        .setSwipeLeftLabelTextSize(TypedValue.COMPLEX_UNIT_DIP, 24)
                        .setSwipeLeftLabelTypeface(ResourcesCompat.getFont(DashboardActivity.this, R.font.poppins_bold))
                        .create()
                        .decorate();

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(rvTodos);

    }

    public void init() {
        sp = getApplicationContext().getSharedPreferences("TodoPrefs", Context.MODE_PRIVATE);
        accessToken = sp.getString("accessToken", "");
        refreshToken = sp.getString("refreshToken", "");
        sharedPool = new RecyclerView.RecycledViewPool();
        rvTodos = findViewById(R.id.rvTodoList);
        tvTaskProgress = findViewById(R.id.tvTaskProgress);
        taskProgressBar = findViewById(R.id.taskProgressBar);
        tvProgressBar = findViewById(R.id.tvProgressBar);
        toolsFunction = new ToolsFunction();
        btnCount = findViewById(R.id.btnCount);
    }

    public void makeProgressTask(@NonNull List<Todo> listTodo, JSONObject paginatorInfo) throws JSONException {


        int completeTodo = 0;
        for (int i=0; i < listTodo.size(); i++) {
            if (listTodo.get(i).getStatus()) {
                completeTodo++;
            }
        }


        String displayTaskProgress = String.valueOf(completeTodo) + '/' + String.valueOf(paginatorInfo.getInt("itemCount"));
        SpannableString ss = new SpannableString(displayTaskProgress);
        ss.setSpan(R.color.title, 0, ss.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        int progressBar = Math.round(Math.round((float)completeTodo/paginatorInfo.getInt("itemCount") * 100));

        tvTaskProgress.setText(ss + " Task", TextView.BufferType.SPANNABLE);
        tvProgressBar.setText(String.valueOf(progressBar) + "%\nDone");
        tvProgressBar.setTextColor(ColorStateList.valueOf(DashboardActivity.this.getResources().getColor(R.color.title)));

        taskProgressBar.setProgress(progressBar);
        btnCount.setText(String.valueOf(paginatorInfo.getInt("itemCount")));
    }

    public void getAllTask() throws JSONException {
        RequestQueue queue = RequestSingleton.getInstance(this.getApplicationContext()).getRequestQueue();
        String url = "http://192.168.1.212:8000/api/todos?page=1&status=false";

        RequestService requestService = new RequestService(queue, url, "GET", accessToken, refreshToken);
        requestService.makeRequest(DashboardActivity.this);


        Type type = new TypeToken<List<Todo>>() {}.getType();
        Type paginatorType = new TypeToken<JSONObject>() {}.getType();
        String listTodoString = sp.getString("todoListItem", "");
        try {
            listTodo = gson.fromJson(listTodoString, type);
            JSONObject paginatorInfo = gson.fromJson(sp.getString("paginator", ""), paginatorType);

            makeProgressTask(listTodo, paginatorInfo);

            adapter = new TodoAdapter(DashboardActivity.this, listTodo, DashboardActivity.this);
            rvTodos.setLayoutManager(new LinearLayoutManager(DashboardActivity.this));
            rvTodos.setRecycledViewPool(sharedPool);
            rvTodos.setNestedScrollingEnabled(false);
            rvTodos.setAdapter(adapter);

        } catch (Exception e) {
            Log.e("Exception", String.valueOf(e));
        }


//
//

//        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        try {
//
//                            JSONObject headers = response.getJSONObject("headers");
//
//                            if (headers.has("x-access-token")) {
//                                String x_access_token = headers.getString("x-access-token");
//                                toolsFunction.setToken(x_access_token);
//                            }
//
//                            JSONArray jsonArray =  response.getJSONArray("content");
//                            JSONObject paginatorInfo = (JSONObject) jsonArray.remove(8);
//                            listTodo = Todo.mapData(jsonArray);
//
//                            rvTodos.setLayoutManager(new LinearLayoutManager(DashboardActivity.this));
//                            rvTodos.setRecycledViewPool(sharedPool);
//                            rvTodos.setNestedScrollingEnabled(false);
//                            adapter = new TodoAdapter(DashboardActivity.this, listTodo, DashboardActivity.this);
//                            rvTodos.setAdapter(adapter);
//
//                            makeProgressTask(listTodo, paginatorInfo);
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }, new Response.ErrorListener() {
//
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(DashboardActivity.this, "Error Detected", Toast.LENGTH_SHORT).show();
//            }
//        }
//        ){
//
//                @Override
//                public String getBodyContentType() {
//                    return "application/json; charset=utf-8";
//                }
//
//                @Override
//                public Map<String, String> getHeaders() throws AuthFailureError {
//                    Map<String,String> params = new HashMap<String, String>();
//                    params.put("Content-Type","application/json; charset=utf-8");
//                    params.put("x-refresh", refreshToken);
//                    params.put("Authorization", "Bearer " + accessToken);
//                    return params;
//                }
//
//                @Override
//                protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
//                try {
//                    String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
//
//                    JSONObject jsonResponse = new JSONObject(jsonString);
//                    assert response.headers != null;
//                    jsonResponse.put("headers", new JSONObject(response.headers));
//
//                    return Response.success(jsonResponse, HttpHeaderParser.parseCacheHeaders(response));
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                    return Response.error(new ParseError(e));
//                } catch (JSONException je) {
//                    je.printStackTrace();
//                    return Response.error(new ParseError(je));
//                }
//            }
//        };
//
//        RequestSingleton.getInstance(this).addToRequestQueue(objectRequest);
    }

    @Override
    public void onTodoClick(int position) {

        Todo t = listTodo.get(position);
        Intent itemDetail = new Intent(DashboardActivity.this, ItemDetail.class);
        itemDetail.putExtra("itemDetail", gson.toJson(t));
        startActivity(itemDetail);
    }



}