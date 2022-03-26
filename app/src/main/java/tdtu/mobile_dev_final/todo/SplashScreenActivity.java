package tdtu.mobile_dev_final.todo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

public class SplashScreenActivity extends AppCompatActivity {

    private static int SPLASH_SCREEN = 4000;
    Animation topAnim, botAnim, blinkAnim;
    LottieAnimationView image;
    TextView title, slogan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        setContentView(R.layout.splash_screen_activity);

        init();

        image.playAnimation();
        title.setAnimation(botAnim);
        slogan.setAnimation(botAnim);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                Intent dashboard = new Intent(SplashScreenActivity.this, LoginActivity.class);
                startActivity(dashboard);
                finish();
            }
        }, SPLASH_SCREEN);
    }

    public void init() {
        topAnim = AnimationUtils.loadAnimation(SplashScreenActivity.this, R.anim.top_animation);
        botAnim = AnimationUtils.loadAnimation(SplashScreenActivity.this, R.anim.bottom_animation);
        blinkAnim = AnimationUtils.loadAnimation(SplashScreenActivity.this, R.anim.blink_animation);
        image = findViewById(R.id.ivSplashImage);
        title = findViewById(R.id.tvSplashTitle);
        slogan = findViewById(R.id.tvSplashSlogan);
    }

}