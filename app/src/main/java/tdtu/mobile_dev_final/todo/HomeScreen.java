package tdtu.mobile_dev_final.todo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Window;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import me.ibrahimsn.lib.OnItemSelectedListener;
import me.ibrahimsn.lib.SmoothBottomBar;
import tdtu.mobile_dev_final.todo.Fragments.CreateFragment;
import tdtu.mobile_dev_final.todo.Fragments.ListFragment;

public class HomeScreen extends AppCompatActivity {

    SmoothBottomBar smoothBottomBar;
    ConstraintLayout constraintLayout;
    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        setContentView(R.layout.activity_home_screen);

        init();

        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new DashboardFragment()).commit();

        smoothBottomBar.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public boolean onItemSelect(int i) {
                switch (i) {
                    case 0:
                        replace(new DashboardFragment());
                        break;
                    case 1:
                        replace(new ListFragment());
                        break;
                    case 2:
                        replace(new CreateFragment());
                        break;
//                    case 3:
//                        replace(new AccountFragment());
//                        break;
                }
                return true;
            }
        });
    }

    public void init() {
        smoothBottomBar = findViewById(R.id.bottomAppBar);
        constraintLayout = findViewById(R.id.homeScreen);
    }

    public void replace(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout, fragment);
        transaction.commit();
    }

    private void exitFromApp() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }


    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            exitFromApp();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}