package tdtu.mobile_dev_final.todo.Fragments;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.RequestQueue;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;
import tdtu.mobile_dev_final.todo.Adapter.SubtaskAdapter;
import tdtu.mobile_dev_final.todo.R;
import tdtu.mobile_dev_final.todo.RequestSingleton;
import tdtu.mobile_dev_final.todo.Services.RequestService;
import tdtu.mobile_dev_final.todo.SubTask;
import tdtu.mobile_dev_final.todo.Todo;
import tdtu.mobile_dev_final.todo.UpdateTodoItemActivity;
import tdtu.mobile_dev_final.todo.Utils.ToolsFunction;

public class CreateFragment extends Fragment implements View.OnClickListener {

    View view;
    TextInputEditText tiCreateTitle, tiCreateDescription, tiCreateStartDate, tiCreateEndDate, tiTagsNameAdd;
    ChipGroup createTagsChipGroup;
    TextInputLayout tlTagsAddName, tlCreateStartDate, tlCreateEndDate;
    RecyclerView rvTaskList;
    RecyclerView.RecycledViewPool sharepool;
    MaterialButton btnAddTags, btnAddSubtask, btnCreateTodo;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    SubtaskAdapter subtaskAdapter;
    String accessToken, refreshToken;
    Todo createTodo;
    List<String> listTags;
    List<SubTask> subTaskList;
    Gson gson;
    Context context;
    Calendar calendar;
    Boolean show, isOpen, taskShow, taskIsOpen;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_create, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);

    }

    public void init(View view) {
        tiCreateTitle = view.findViewById(R.id.tiCreateTitle);
        tiCreateDescription = view.findViewById(R.id.tiCreateDescription);
        tiCreateStartDate = view.findViewById(R.id.tiCreateStartDate);
        tiCreateEndDate = view.findViewById(R.id.tiCreateEndDate);
        btnAddTags = view.findViewById(R.id.btnAddTags);
        tlTagsAddName = view.findViewById(R.id.tlTagsNameAdd);
        tlCreateStartDate = view.findViewById(R.id.tlCreateStartDate);
        tlCreateEndDate = view.findViewById(R.id.tlCreateEndDate);
        tiTagsNameAdd = view.findViewById(R.id.tiTagsNameAdd);
        btnAddSubtask = view.findViewById(R.id.subtaskAddBtn);
        btnCreateTodo = view.findViewById(R.id.todoCreateBtn);

        createTagsChipGroup = view.findViewById(R.id.createTagsChipGroup);
        gson = new Gson();
        rvTaskList = view.findViewById(R.id.tvTaskList);
        sharepool = new RecyclerView.RecycledViewPool();
        sharepool = new RecyclerView.RecycledViewPool();
        subTaskList = new ArrayList<>();
        listTags = new ArrayList<>();
        rvTaskList.setLayoutManager(new LinearLayoutManager(context));
        rvTaskList.setRecycledViewPool(sharepool);
        subtaskAdapter = new SubtaskAdapter(requireContext(), subTaskList);
        rvTaskList.setAdapter(subtaskAdapter);

        calendar = Calendar.getInstance();
        show = false;
        isOpen = false;
        taskShow = false;
        taskIsOpen = false;
        createTodo = new Todo();

        btnAddTags.setOnClickListener(this);
        btnAddSubtask.setOnClickListener(this);
        btnCreateTodo.setOnClickListener(this);
        tiCreateStartDate.setOnClickListener(this);
        tiCreateEndDate.setOnClickListener(this);

        accessToken = sp.getString("accessToken", "");
        refreshToken = sp.getString("refreshToken", "");
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void datePicker(TextInputEditText textInputEditText) {

        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), R.style.App_Theme_DatePickerDialog, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = day + "-" + month + "-" + year;
                textInputEditText.setText(date);
            }
        }, year, month, day);
        datePickerDialog.show();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        sp = context.getSharedPreferences("TodoPrefs", Context.MODE_PRIVATE);
        editor = sp.edit();
    }

    @SuppressLint("NonConstantResourceId")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View view) {
        switch(view.getId())

        {
            case R.id.tiCreateStartDate:
                datePicker(tiCreateStartDate);
                break;

            case R.id.tiCreateEndDate:
                datePicker(tiCreateEndDate);
                break;

            case R.id.btnAddTags:

                if (show) {
                    show = false;
                } else {
                    show = true;
                }
                tlTagsAddName.setVisibility(show ? View.VISIBLE : View.GONE);

                tlTagsAddName.setEndIconOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        listTags.add(tiTagsNameAdd.getText().toString().trim());
                        createTagsChipGroup.addView(ToolsFunction.makeChip(requireContext(), tiTagsNameAdd.getText().toString().trim()));
                        tiTagsNameAdd.getText().clear();
                    }
                });
                break;

            case R.id.subtaskAddBtn:
                try {
                    Log.i("OpenDialog", "Open Dialog");
                    openCreateSubTaskDialog();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.id.todoCreateBtn:
                Log.i("CreateTodo", "Clicked");
                createTodo();
                break;
        }
    }

    public void openCreateSubTaskDialog(
    ) {
        final Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.todo_dialog);

        Window window = dialog.getWindow();

        if (window == null) {
            return;
        }

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = Gravity.CENTER;
        window.setAttributes(windowAttributes);
        dialog.setCancelable(false);

        MaterialButton btnTaskCancel = dialog.findViewById(R.id.cancelBtn);
        MaterialButton btnCreateSubtask = dialog.findViewById(R.id.createTaskBtn);
        TextInputEditText tiTaskName = dialog.findViewById(R.id.tiTaskName);
        TextInputEditText tiTaskDescription = dialog.findViewById(R.id.tiTaskDescription);


        btnTaskCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btnCreateSubtask.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View view) {
                SubTask subtask = new SubTask();
                subtask.setName(tiTaskName.getText().toString());
                subtask.setDescription(tiTaskDescription.getText().toString());
                subtask.setStatus(false);
                subTaskList.add(subtask);
                subtaskAdapter.notifyDataSetChanged();
                tiTaskName.getText().clear();
                tiTaskDescription.getText().clear();
                Toast.makeText(requireContext(), "Adding Task Successfully", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }



    public void saveTags(List<String> tags, String arrayName) {
        editor.putInt(arrayName + "_size", tags.size());
        for (int i=0; i< tags.size(); i++) {
            editor.putString(arrayName + "_" + i, tags.get(i));
        }
    }

    public void saveTask(List<SubTask> subTask, String arrayName) {
        editor.putInt(arrayName + "_size", subTask.size());
        for (int i=0; i< subTask.size(); i++) {
            editor.putString(arrayName + "_" + i, gson.toJson(subTask.get(i), SubTask.class));
        }
    }

    public void createTodo() {

        try {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy", new Locale("vi-VN"));
            String startDate = outputFormat.format(Objects.requireNonNull(inputFormat.parse(tiCreateStartDate.getText().toString())));
            String endDate = outputFormat.format(Objects.requireNonNull(inputFormat.parse(tiCreateEndDate.getText().toString())));

            editor.putString("createName", tiCreateTitle.getText().toString());
            editor.putString("createDescription", tiCreateDescription.getText().toString());
            editor.putString("createStartDate", startDate);
            editor.putString("createEndDate", endDate);

            editor.apply();

            saveTags(listTags, "tag");
            saveTask(subTaskList, "subTask");

            editor.apply();

            RequestQueue queue = RequestSingleton.getInstance(requireContext()).getRequestQueue();
            String url = "http://192.168.1.212:8000/api/todos/";

            RequestService requestService = new RequestService(queue, url, "POST", accessToken, refreshToken,"CreateTodo");
            requestService.makeRequest(requireContext());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}