package tdtu.mobile_dev_final.todo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.android.volley.RequestQueue;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import tdtu.mobile_dev_final.todo.Fragments.EditOrAddItem;
import tdtu.mobile_dev_final.todo.Services.RequestService;
import tdtu.mobile_dev_final.todo.Utils.ToolsFunction;

public class UpdateTodoItemActivity extends AppCompatActivity implements View.OnClickListener, EditOrAddItem.IEditItemOrAddData {

    Todo itemDetail;
    MaterialButton  btnBack, btnUpdate, btnDelete;
    SharedPreferences sp;
    String title, description, startDate, endDate;
    List<String> tags;
    String accessToken, refreshToken;
    ArrayList<String> listTask;
    SharedPreferences.Editor editor;
    boolean status;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.update_layout_activity);

        String extras = getIntent().getStringExtra("itemDetail");
        Gson gson = new Gson();
        itemDetail = gson.fromJson(extras, Todo.class);

        init();

        btnUpdate.setOnClickListener(this);
        btnDelete.setOnClickListener(this);btnBack.setOnClickListener(this);

        setFragment();

        Fragment frInsert;
        frInsert = getSupportFragmentManager().findFragmentById(R.id.updateItemFragment);

    }

    public void init() {
        btnBack = findViewById(R.id.backBtn);
        btnDelete = findViewById(R.id.deleteBtn);
        btnUpdate = findViewById(R.id.updateBtn);

        sp = getSharedPreferences("TodoPrefs", Context.MODE_PRIVATE);
        editor = sp.edit();

        accessToken = sp.getString("accessToken", "");
        refreshToken = sp.getString("refreshToken", "");
    }

    protected void setFragment() {
        // Begin the transaction
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.updateItemFragment, new EditOrAddItem());
        ft.commit();
    }

    public Bundle getMyData() {
        Bundle hm = new Bundle();
        Gson gson = new Gson();
        hm.putString("todoItem", gson.toJson(itemDetail));
        return hm;
    }

    @Override
    public void onClick(View view) {
       switch (view.getId()) {
           case R.id.backBtn:
               super.onBackPressed();
               Animatoo.animateCard(UpdateTodoItemActivity.this);
               break;

           case R.id.deleteBtn:
               // Delete
               break;

           case R.id.updateBtn:
               updateTodo();
               break;
       }
    }


    @Override
    public void getTitle(String title) {
        editor.putString("updateTitle", title);
        editor.apply();
    }

    @Override
    public void getDescription(String description) {
        editor.putString("updateDescription", description);
        editor.apply();
    }

    @Override
    public void getStartDate(String startDate) {
        editor.putString("updateStartDate", startDate);
        editor.apply();
    }

    @Override
    public void getEndDate(String endDate) {
        editor.putString("updateEndDate", endDate);
        editor.apply();
    }

    @Override
    public void getTags(List<String> tags) {

    }

    @Override
    public void getSubTask(ArrayList<String> subTask) {

    }

    @Override
    public void getStatus(boolean status) {
        editor.putBoolean("updateStatus", status);
    }

    public void updateTodo() {
        RequestQueue queue = RequestSingleton.getInstance(UpdateTodoItemActivity.this).getRequestQueue();
        String url = "http://192.168.1.212:8000/api/todos/" + itemDetail.getId();

        RequestService requestService = new RequestService(queue, url, "PATCH", accessToken, refreshToken,"EditTodo");
        requestService.makeRequest(UpdateTodoItemActivity.this);
    }
}