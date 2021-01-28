package com.example.jamiaaty;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreen extends AppCompatActivity {
    ImageView imageView;
    TextView nameTv, name2Tv;
    long animationTime = 2000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

        imageView = findViewById(R.id.iv_spalash_logo);
        nameTv = findViewById(R.id.tv_spalsh_name);
        name2Tv = findViewById(R.id.tv_spalsh_name2);

        ObjectAnimator animatorY = ObjectAnimator.ofFloat(imageView,"y", 400f);
        ObjectAnimator animatorName = ObjectAnimator.ofFloat(nameTv,"x",90f);
        animatorY.setDuration(animationTime);
        animatorName.setDuration(animationTime);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animatorName,animatorY);
        animatorSet.start();


    }

    @Override
    protected void onStart() {
        super.onStart();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(user != null){
                        Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }else {
                        Intent intent = new Intent(SplashScreen.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }

                }
            },2000);
    }
}