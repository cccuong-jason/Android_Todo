package tdtu.mobile_dev_final.todo.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.switchmaterial.SwitchMaterial;
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

import tdtu.mobile_dev_final.todo.Adapter.SubtaskAdapter;
import tdtu.mobile_dev_final.todo.ItemDetail;
import tdtu.mobile_dev_final.todo.R;
import tdtu.mobile_dev_final.todo.SubTask;
import tdtu.mobile_dev_final.todo.Todo;
import tdtu.mobile_dev_final.todo.UpdateTodoItemActivity;
import tdtu.mobile_dev_final.todo.Utils.ToolsFunction;

public class EditOrAddItem extends Fragment implements View.OnClickListener {

    View view;
    TextInputEditText tiUpdateTitle, tiUpdateDescription, tiUpdateStartDate, tiUpdateEndDate, tiTagsNameAdd;
    TextInputLayout tlTagsAddName;
    ChipGroup updateTagsChipGroup;
    UpdateTodoItemActivity itemDetailActivity;
    RecyclerView rvTaskList;
    RecyclerView.RecycledViewPool sharepool;
    LottieAnimationView swStatus;
    MaterialButton btnAddTags;
    List<SubTask> subTaskList;
    Bundle bundle;
    String itemDetail;
    Gson gson;
    Todo itemDetailObject;
    Context context;
    Calendar calendar;
    Boolean show;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.update_todo_item, container, false);
        context = container.getContext();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        SimpleDateFormat stringFormat = new SimpleDateFormat("dd/MM/yyyy", new Locale("vi-VN"));
        init();

        tiUpdateTitle.setText(itemDetailObject.getName());
        tiUpdateDescription.setText(itemDetailObject.getDescription());

        tiUpdateDescription.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (view.getId() == R.id.tiUpdateDescription) {

                    view.getParent().requestDisallowInterceptTouchEvent(true);

                    switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_UP:
                            view.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }
                }
                return false;
            }
        });

        try {
            tiUpdateStartDate.setText(stringFormat.format(Objects.requireNonNull(inputFormat.parse(itemDetailObject.getStartDate()))));
            tiUpdateEndDate.setText(stringFormat.format(Objects.requireNonNull(inputFormat.parse(itemDetailObject.getEndDate()))));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        for (String tag: itemDetailObject.getTags()) {
            updateTagsChipGroup.addView(ToolsFunction.makeChip(context, tag));
        }

        if (itemDetailObject.getStatus()) {
            swStatus.setMinAndMaxProgress(0.0f, 0.5f);
            swStatus.playAnimation();
        } else {
            swStatus.setMinAndMaxProgress(0.5f, 1.0f);
            swStatus.playAnimation();
        }

        subTaskList = new ArrayList<>();
        subTaskList.addAll(itemDetailObject.getTaskList());
        rvTaskList.setLayoutManager(new LinearLayoutManager(context));
        rvTaskList.setRecycledViewPool(sharepool);
        SubtaskAdapter adapter = new SubtaskAdapter(context, subTaskList);
        rvTaskList.setAdapter(adapter);

        tiUpdateStartDate.setOnClickListener(this);
        tiUpdateEndDate.setOnClickListener(this);
        btnAddTags.setOnClickListener(this);
        swStatus.setOnClickListener(this);

        return view;
    }

    public void init() {
        tiUpdateTitle = view.findViewById(R.id.tiUpdateTitle);
        tiUpdateDescription = view.findViewById(R.id.tiUpdateDescription);
        tiUpdateStartDate = view.findViewById(R.id.tiUpdateStartDate);
        tiUpdateEndDate = view.findViewById(R.id.tiUpdateEndDate);
        itemDetailActivity = (UpdateTodoItemActivity) getActivity();
        updateTagsChipGroup = view.findViewById(R.id.updateTagsChipGroup);
        rvTaskList = view.findViewById(R.id.tvTaskList);
        sharepool = new RecyclerView.RecycledViewPool();
        btnAddTags = view.findViewById(R.id.btnAddTags);
        tlTagsAddName = view.findViewById(R.id.tlTagsNameAdd);
        swStatus = view.findViewById(R.id.swTags);
        tiTagsNameAdd = view.findViewById(R.id.tiTagsNameAdd);
        bundle = itemDetailActivity.getMyData();
        show = false;
        itemDetail = bundle.getString("todoItem");
        gson = new Gson();
        itemDetailObject = gson.fromJson(itemDetail, Todo.class);
        calendar = Calendar.getInstance();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void datePicker(TextInputEditText textInputEditText) {
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(context, R.style.App_Theme_DatePickerDialog, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = day + "/" + month + "/" + year;
                textInputEditText.setText(date);
            }
        }, year, month, day);
        datePickerDialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View view)  {
        switch(view.getId())

    {
        case R.id.tiUpdateStartDate:
            datePicker(tiUpdateStartDate);
            break;
        case R.id.tiUpdateEndDate:
            datePicker(tiUpdateEndDate);
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

                    updateTagsChipGroup.addView(ToolsFunction.makeChip(context, tiTagsNameAdd.getText().toString().trim()));
                    tlTagsAddName.setVisibility(View.GONE);
                    show = false;
                }
            });
            break;
        case R.id.swTags:

            Toast.makeText(context, "Clicked", Toast.LENGTH_SHORT).show();
            if (itemDetailObject.getStatus()) {
                swStatus.setMinAndMaxProgress(0.5f, 1.0f);
                swStatus.playAnimation();
                itemDetailObject.setStatus(false);
            } else {
                swStatus.setMinAndMaxProgress(0.0f, 0.5f);
                swStatus.playAnimation();
                itemDetailObject.setStatus(true);
            }

            Log.i("LottieStatus", String.valueOf(itemDetailObject.getStatus()));

    }
}
}