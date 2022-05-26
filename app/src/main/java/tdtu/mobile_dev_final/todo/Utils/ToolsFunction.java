package tdtu.mobile_dev_final.todo.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.google.android.material.chip.Chip;

import java.util.concurrent.atomic.AtomicInteger;

import tdtu.mobile_dev_final.todo.R;

public class ToolsFunction extends FragmentActivity {

    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);
    SharedPreferences sp;

    @SuppressLint("ObsoleteSdkInt")
    public static int generateViewId() {
        if (Build.VERSION.SDK_INT < 17) {
            for (;;) {
                final int result = sNextGeneratedId.get();
                int newValue = result + 1;
                if (newValue > 0x00FFFFFF)
                    newValue = 1; // Roll over to 1, not 0.
                if (sNextGeneratedId.compareAndSet(result, newValue)) {
                    return result;
                }
            }
        } else {
            return View.generateViewId();
        }
    }

    public static Chip makeChip(Context context, String tag) {

        Chip tagChip = new Chip(context);

        tagChip.setText(tag);
        tagChip.setChipBackgroundColorResource(R.color.title);
        tagChip.setTextColor(context.getResources().getColor(R.color.overlay));

        return tagChip;
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.updateItemFragment, fragment);
        fragmentTransaction.commit();
    }

    public void setToken(Context context, String accessToken) {
        sp = context.getSharedPreferences("TodoPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("accessToken", accessToken);
        editor.commit();
    }

    public int getRequestMethod(String method) {
        switch (method) {
            case "GET":
                return Request.Method.GET;
            case "POST":
                return Request.Method.POST;
            case "PATCH":
                return Request.Method.PATCH;
            case "PUT":
                return Request.Method.PUT;
            case "DELETE":
                return  Request.Method.DELETE;
            default:
                return Request.Method.GET;
        }
    }
}
