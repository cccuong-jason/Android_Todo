package tdtu.mobile_dev_final.todo;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.chip.ChipGroup;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import tdtu.mobile_dev_final.todo.Adapter.SubtaskAdapter;
import tdtu.mobile_dev_final.todo.Utils.ToolsFunction;

public class ItemDetail extends AppCompatActivity {

    TextView tvItemTitle, tvStartDate, tvEnDate, tvDescription;
    RecyclerView rvTaskList;
    RecyclerView.RecycledViewPool sharepool;
    Toolbar toolbar;
    Todo itemDetail;
    AppBarLayout appBarLayout;
    CollapsingToolbarLayout collapsingToolbarLayout;
    ChipGroup chipGroup;
    List<SubTask> subTaskList;
    SubtaskAdapter adapter;
    Button editButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.todo_detail);

        String extras = getIntent().getStringExtra("itemDetail");
        Gson gson = new Gson();
        itemDetail = gson.fromJson(extras, Todo.class);

        init();

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle(itemDetail.getName());
                    isShow = true;
                } else if(isShow) {
                    collapsingToolbarLayout.setTitle(" ");
                    isShow = false;
                }
            }
        });

        @SuppressLint("SimpleDateFormat") SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        SimpleDateFormat stringFormat = new SimpleDateFormat("dd/MM/yyyy", new Locale("vi-VN"));
        tvItemTitle.setText(itemDetail.getName());
        tvDescription.setText(itemDetail.getDescription());
        try {
            tvStartDate.setText(stringFormat.format(Objects.requireNonNull(inputFormat.parse(itemDetail.getStartDate()))));
            tvEnDate.setText(stringFormat.format(Objects.requireNonNull(inputFormat.parse(itemDetail.getEndDate()))));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        for (String tag: itemDetail.getTags()) {
            chipGroup.addView(ToolsFunction.makeChip(ItemDetail.this, tag));
        }

        subTaskList = new ArrayList<>();
        subTaskList.addAll(itemDetail.getTaskList());
        rvTaskList.setLayoutManager(new LinearLayoutManager(ItemDetail.this));
        rvTaskList.setRecycledViewPool(sharepool);
        adapter = new SubtaskAdapter(ItemDetail.this, subTaskList);
        rvTaskList.setAdapter(adapter);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemEditButton:
                Intent updateItem = new Intent(ItemDetail.this, UpdateTodoItemActivity.class);
                Gson gson = new Gson();
                updateItem.putExtra("itemDetail", gson.toJson(itemDetail));
                startActivity(updateItem);
                Animatoo.animateCard(ItemDetail.this); //fire the slide left animation

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        Animatoo.animateSlideLeft(ItemDetail.this); //fire the slide left animation
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    public void init() {
        tvItemTitle = findViewById(R.id.tvItemTitle);
        tvStartDate = findViewById(R.id.tvStartdate);
        tvEnDate = findViewById(R.id.tvEndDate);
        tvDescription = findViewById(R.id.tvDescription);
        rvTaskList = findViewById(R.id.tvTaskList);
        sharepool = new RecyclerView.RecycledViewPool();
        toolbar =  findViewById(R.id.toolBar);
        appBarLayout = findViewById(R.id.appBarLayout);
        collapsingToolbarLayout = findViewById(R.id.collapsingToolbarLayout);
        editButton = findViewById(R.id.itemEditButton);
        chipGroup = findViewById(R.id.chipGroup);
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.updateItemFragment, fragment);
        fragmentTransaction.commit();
    }
}