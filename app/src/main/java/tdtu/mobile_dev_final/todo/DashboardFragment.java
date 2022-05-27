package tdtu.mobile_dev_final.todo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import com.android.volley.RequestQueue;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import org.json.JSONException;
import org.json.JSONObject;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;
import me.ibrahimsn.lib.SmoothBottomBar;
import tdtu.mobile_dev_final.todo.Adapter.TodoAdapter;
import tdtu.mobile_dev_final.todo.Services.RequestService;
import tdtu.mobile_dev_final.todo.Utils.ToolsFunction;

public class DashboardFragment extends Fragment implements TodoAdapter.OnTodoListener {

    String accessToken, refreshToken;
    List<Todo> listTodo;
    RecyclerView rvTodos;
    RecyclerView.RecycledViewPool sharedPool;
    ToolsFunction toolsFunction;
    TodoAdapter adapter;
    ShimmerFrameLayout shimmerFrameLayout;
    TextView tvTaskProgress, tvProgressBar;
    CircularProgressBar taskProgressBar;
    CircleImageView profileImage;
    MaterialButton btnCount;
    ItemTouchHelper.SimpleCallback simpleCallback;
    JSONObject paginatorInfo;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    View view;
    Context context;
    Gson gson = new Gson();
    SmoothBottomBar bottomNav;
    Animation slideUp, slideDown;
    SwipeRefreshLayout swipeRefreshLayout;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.dashboard_activity, container, false);
        context = container.getContext();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);

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

                        editor.remove("updateTitle");
                        editor.remove("updateDescription");
                        editor.remove("updateStartDate");
                        editor.remove("updateEndDate");
                        editor.putBoolean("updateStatus", true);
                        editor.apply();

                        updateTodo(deletedTodo.getId());

                        listTodo.remove(deletedTodo);
                        adapter.notifyItemRemoved(position);

                        try {
                            makeProgressTask(listTodo, paginatorInfo);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Todo finalDeletedTodo = deletedTodo;
                        Snackbar.make(rvTodos, deletedTodo.getName(), Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                updateTodoFalse(finalDeletedTodo.getId());
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
            public void onChildDraw (@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive){
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftActionIcon(R.drawable.ic_checklist)
                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(context, R.color.pastelGreen))
                        .addSwipeLeftLabel("Done Task")
                        .setSwipeLeftActionIconTint(ContextCompat.getColor(context, R.color.doneText))
                        .setSwipeLeftLabelColor(ContextCompat.getColor(context, R.color.doneText))
                        .setSwipeLeftLabelTextSize(TypedValue.COMPLEX_UNIT_DIP, 24)
                        .setSwipeLeftLabelTypeface(ResourcesCompat.getFont(context, R.font.poppins_bold))
                        .create()
                        .decorate();

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(rvTodos);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    shimmerFrameLayout.setVisibility(View.VISIBLE);
                    rvTodos.setVisibility(View.GONE);
                    shimmerFrameLayout.startShimmer();
                    getAllTask();
                    swipeRefreshLayout.setRefreshing(false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        sp = context.getSharedPreferences("TodoPrefs", Context.MODE_PRIVATE);
        editor = sp.edit();
    }
    public void init(View view) {
        accessToken = sp.getString("accessToken", "");
        refreshToken = sp.getString("refreshToken", "");
        sharedPool = new RecyclerView.RecycledViewPool();
        rvTodos = view.findViewById(R.id.rvTodoList);
        tvTaskProgress = view.findViewById(R.id.tvTaskProgress);
        taskProgressBar = view.findViewById(R.id.taskProgressBar);
        tvProgressBar = view.findViewById(R.id.tvProgressBar);
        toolsFunction = new ToolsFunction();
        btnCount = view.findViewById(R.id.btnCount);
        bottomNav = view.findViewById(R.id.bottomAppBar);
        slideUp = AnimationUtils.loadAnimation(context, R.anim.slide_up_animation);
        slideDown = AnimationUtils.loadAnimation(context, R.anim.slide_down_animation);
        profileImage = view.findViewById(R.id.profileImage);
        swipeRefreshLayout = view.findViewById(R.id.swiperefresh);
        shimmerFrameLayout = view.findViewById(R.id.shimmerLayoutHorizontal);
        shimmerFrameLayout.startShimmer();
        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = sp.edit();
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

            int progressBar = Math.round(Math.round((float)completeTodo/listTodo.size() * 100));

            tvTaskProgress.setText(ss + " Task", TextView.BufferType.SPANNABLE);
            tvProgressBar.setText(String.valueOf(progressBar) + "%\nDone");
            tvProgressBar.setTextColor(ColorStateList.valueOf(DashboardFragment.this.getResources().getColor(R.color.title)));

            taskProgressBar.setProgress(progressBar);
            btnCount.setText(String.valueOf(listTodo.size()));
    }

    public void getAllTask() throws JSONException {

        RequestQueue queue = RequestSingleton.getInstance(requireContext()).getRequestQueue();
        String url = "http://192.168.1.212:8000/api/todos?page=1&status=false";

        RequestService requestService = new RequestService(queue, url, "GET", accessToken, refreshToken,"AllTodo");
        requestService.makeRequest(requireContext());


        Type type = new TypeToken<List<Todo>>() {}.getType();
        Type paginatorType = new TypeToken<JSONObject>() {}.getType();

        try {


            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    String listTodoString = sp.getString("todoListItem", "");
                    listTodo = gson.fromJson(listTodoString, type);
                    paginatorInfo = gson.fromJson(sp.getString("paginator", ""), paginatorType);
                    try {
                        makeProgressTask(listTodo, paginatorInfo);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    rvTodos.setVisibility(View.VISIBLE);

                    adapter = new TodoAdapter(context, listTodo, DashboardFragment.this);
                    rvTodos.setLayoutManager(new LinearLayoutManager(context));
                    rvTodos.setRecycledViewPool(sharedPool);
                    rvTodos.setNestedScrollingEnabled(false);
                    rvTodos.setAdapter(adapter);
                }
            }, 1500);


        } catch (Exception e) {
            Log.e("Exception", String.valueOf(e));
            e.printStackTrace();
        }
    }

    public void updateTodo(String id) {
        RequestQueue queue = RequestSingleton.getInstance(requireContext()).getRequestQueue();
        String url = "http://192.168.1.212:8000/api/todos/" + id;

        RequestService requestService = new RequestService(queue, url, "PATCH", accessToken, refreshToken,"EditTodo");
        requestService.makeRequest(requireContext());
    }

    public void updateTodoFalse(String id) {
        RequestQueue queue = RequestSingleton.getInstance(requireContext()).getRequestQueue();
        String url = "http://192.168.1.212:8000/api/todos/" + id;

        RequestService requestService = new RequestService(queue, url, "PATCH", accessToken, refreshToken,"EditTodoFalse");
        requestService.makeRequest(requireContext());
    }

    @Override
    public void onTodoClick(int position) {

        Todo t = listTodo.get(position);
        Intent itemDetail = new Intent(context, ItemDetail.class);
        itemDetail.putExtra("itemDetail", gson.toJson(t));
        startActivity(itemDetail);
    }



}