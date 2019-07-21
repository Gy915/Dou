package com.bytedance.androidcamp.network.dou;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

public class sign_up extends AppCompatActivity {

    public ImageView v_boy;
    public ImageView v_girl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        v_boy = findViewById(R.id.boy);
        v_girl = findViewById(R.id.girl);
        initbtton();

    }

    public void initbtton() {
        v_boy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v_boy.setVisibility(View.INVISIBLE);
                v_girl.setVisibility(View.INVISIBLE);
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        //do something
                    }
                }, 1000);    //延时1s执行

                Intent intent = new Intent(sign_up.this, signup1.class);
                startActivity(intent);

            }

        });
        v_girl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                v_boy.setVisibility(View.INVISIBLE);
                v_girl.setVisibility(View.INVISIBLE);
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        //do something
                    }
                }, 1000);    //延时1s执行

                Intent intent = new Intent(sign_up.this, signup1.class);
                startActivity(intent);
            }
        });
    }
}
