package tdtu.mobile_dev_final.todo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.google.gson.Gson;

import tdtu.mobile_dev_final.todo.Fragments.EditOrAddItem;
import tdtu.mobile_dev_final.todo.Utils.ToolsFunction;

public class UpdateTodoItemActivity extends AppCompatActivity {

    Todo itemDetail;


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
}