package com.bytedance.androidcamp.network.dou;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class signup1 extends AppCompatActivity {

    public TextView v_single;
    public TextView v_double;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup1);
        v_single = findViewById(R.id.danshengou);
        v_double = findViewById(R.id.xianchong);
        initbtton();

    }

    public void initbtton() {
        v_single.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v_single.setVisibility(View.INVISIBLE);
                v_double.setVisibility(View.INVISIBLE);
                Intent intent = new Intent(signup1.this, signup2.class);
                startActivity(intent);
            }

        });
        v_double.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                v_single.setVisibility(View.INVISIBLE);
                v_double.setVisibility(View.INVISIBLE);
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        //do something
                    }
                }, 1000);    //延时1s执行

                Intent intent = new Intent(signup1.this, signup2.class);
                startActivity(intent);
            }
        });
    }
}
