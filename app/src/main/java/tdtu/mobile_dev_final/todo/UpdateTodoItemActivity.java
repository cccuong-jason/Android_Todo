package tdtu.mobile_dev_final.todo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import tdtu.mobile_dev_final.todo.Fragments.EditOrAddItem;
import tdtu.mobile_dev_final.todo.Utils.ToolsFunction;

public class UpdateTodoItemActivity extends AppCompatActivity implements View.OnClickListener {

    Todo itemDetail;
    FloatingActionButton fabGeneral, fabUpdate, fabDelete;
    Animation fabOpen, fabClose, rotateForward, rotateBackward;
    Boolean isOpen = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        setContentView(R.layout.update_layout_activity);

        String extras = getIntent().getStringExtra("itemDetail");
        Gson gson = new Gson();
        itemDetail = gson.fromJson(extras, Todo.class);

        setFragment();

        init();
        fabGeneral.setOnClickListener(this);
    }

    protected void setFragment() {
        // Begin the transaction
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        // Replace the contents of the container with the new fragment
        ft.replace(R.id.updateItemFragment, new EditOrAddItem());
        // or ft.add(R.id.your_placeholder, new ABCFragment());
        // Complete the changes added above
        ft.commit();
    }

    public Bundle getMyData() {
        Bundle hm = new Bundle();
        Gson gson = new Gson();
        hm.putString("todoItem", gson.toJson(itemDetail));
        return hm;
    }

    public void init() {
        fabGeneral = findViewById(R.id.fabGeneral);
        fabUpdate = findViewById(R.id.fabUpdate);
        fabDelete = findViewById(R.id.fabDelete);

        fabOpen = AnimationUtils.loadAnimation(UpdateTodoItemActivity.this, R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(UpdateTodoItemActivity.this, R.anim.fab_close);

        rotateForward = AnimationUtils.loadAnimation(UpdateTodoItemActivity.this, R.anim.rotate_forward);
        rotateBackward = AnimationUtils.loadAnimation(UpdateTodoItemActivity.this, R.anim.rotate_backward);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.fabGeneral:
                animateFab();
                break;
            case R.id.fabDelete:
                break;
            case R.id.fabUpdate:
                break;
        }
    }

    private void animateFab() {
        if (isOpen) {
            fabGeneral.startAnimation(rotateForward);
            fabUpdate.startAnimation(fabClose);
            fabDelete.startAnimation(fabClose);
            fabUpdate.setClickable(false);
            fabDelete.setClickable(false);
            isOpen = false;
        } else {
            fabGeneral.startAnimation(rotateBackward);
            fabUpdate.startAnimation(fabOpen);
            fabDelete.startAnimation(fabOpen);
            fabUpdate.setClickable(true);
            fabDelete.setClickable(true);
            isOpen = true;
        }
    }
}