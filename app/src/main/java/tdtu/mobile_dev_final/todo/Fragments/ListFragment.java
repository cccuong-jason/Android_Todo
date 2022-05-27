package tdtu.mobile_dev_final.todo.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;
import tdtu.mobile_dev_final.todo.Adapter.TodoAdapter;
import tdtu.mobile_dev_final.todo.DashboardFragment;
import tdtu.mobile_dev_final.todo.ItemDetail;
import tdtu.mobile_dev_final.todo.R;
import tdtu.mobile_dev_final.todo.RequestSingleton;
import tdtu.mobile_dev_final.todo.Services.RequestService;
import tdtu.mobile_dev_final.todo.Todo;

public class ListFragment extends Fragment implements TodoAdapter.OnTodoListener{

    View view;
    HorizontalCalendar horizontalCalendar;
    RecyclerView rvTodos;
    SharedPreferences sp;
    String accessToken, refreshToken;
    SharedPreferences.Editor editor;
    JSONObject paginatorInfo;
    RecyclerView.RecycledViewPool sharedPool;
    TodoAdapter adapter;
    List<Todo> listTodo;
    ShimmerFrameLayout shimmerFrameLayout;
    Gson gson = new Gson();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_list, container, false);
        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);

        try {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            SimpleDateFormat inputFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
            Date currentTime = Calendar.getInstance().getTime();
            try {
                String startOfDay = outputFormat.format(Objects.requireNonNull(inputFormat.parse(String.valueOf(getStartOfDay(currentTime)))));
                String endOfDay = outputFormat.format(Objects.requireNonNull(inputFormat.parse(String.valueOf(getEndOfDay(currentTime)))));
                getAllTask(startOfDay, endOfDay);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void init(View view) {
        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.MONTH, 0);

        /* ends after 1 month from now */
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.MONTH, 1);

        horizontalCalendar = new HorizontalCalendar.Builder(view, R.id.calendarView)
                .range(startDate, endDate)
                .datesNumberOnScreen(6)
                .configure().selectorColor(R.color.overlay)
                .end()
                .build();

        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {
                @SuppressLint("SimpleDateFormat") SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                SimpleDateFormat inputFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);

                try {
                    date.add(Calendar.DATE, 1);
                    String startOfDay = outputFormat.format(Objects.requireNonNull(inputFormat.parse(String.valueOf(getStartOfDay(date.getTime())))));
                    String endOfDay = outputFormat.format(Objects.requireNonNull(inputFormat.parse(String.valueOf(getEndOfDay(date.getTime())))));
                    Log.i("Date", startOfDay + "/" + endOfDay);
                    shimmerFrameLayout.setVisibility(View.VISIBLE);
                    shimmerFrameLayout.startShimmer();
                    rvTodos.setVisibility(View.GONE);
                    getAllTask(startOfDay, endOfDay);
                } catch (ParseException | JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        accessToken = sp.getString("accessToken", "");
        refreshToken = sp.getString("refreshToken", "");
        sharedPool = new RecyclerView.RecycledViewPool();
        rvTodos = view.findViewById(R.id.rvTodoList);
        shimmerFrameLayout = view.findViewById(R.id.shimmerLayoutHorizontal);
        shimmerFrameLayout.startShimmer();
    }

    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        sp = context.getSharedPreferences("TodoPrefs", Context.MODE_PRIVATE);
        editor = sp.edit();
    }

    public void getAllTask(String startDate, String endDate) throws JSONException {

        RequestQueue queue = RequestSingleton.getInstance(requireContext()).getRequestQueue();
        String url = "http://192.168.1.212:8000/api/todos?page=1&status=false&interval=" + startDate + "," + endDate ;
        Log.i("Url", url);

        RequestService requestService = new RequestService(queue, url, "GET", accessToken, refreshToken,"AllTodo");
        requestService.makeRequest(requireContext());


        Type type = new TypeToken<List<Todo>>() {}.getType();
        Type paginatorType = new TypeToken<JSONObject>() {}.getType();
        editor.remove("todoListItem").apply();
        editor.remove("paninator").apply();

        try {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    String listTodoString = sp.getString("todoListItem", "");
                    listTodo = gson.fromJson(listTodoString, type);
//                    Log.i("ListTodo", String.valueOf(listTodo.size()));
                    paginatorInfo = gson.fromJson(sp.getString("paginator", ""), paginatorType);
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    rvTodos.setVisibility(View.VISIBLE);
                    adapter = new TodoAdapter(requireContext(), listTodo, ListFragment.this);
                    rvTodos.setLayoutManager(new LinearLayoutManager(requireContext()));
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

    @Override
    public void onTodoClick(int position) {
        Todo t = listTodo.get(position);
        Intent itemDetail = new Intent(requireContext(), ItemDetail.class);
        itemDetail.putExtra("itemDetail", gson.toJson(t));
        startActivity(itemDetail);
    }

    public static Date getStartOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date getEndOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }
}