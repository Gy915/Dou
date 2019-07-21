package com.bytedance.androidcamp.network.dou;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class signup2 extends AppCompatActivity {

    public TextView v_single;
    public TextView v_double;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup2);
        v_single = findViewById(R.id.young);
        v_double = findViewById(R.id.old);
        initbtton();

    }

    public void initbtton() {
        v_single.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v_single.setVisibility(View.INVISIBLE);
                v_double.setVisibility(View.INVISIBLE);
                Intent intent = new Intent(signup2.this, MainActivity.class);
                startActivity(intent);
            }

        });
        v_double.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                v_single.setVisibility(View.INVISIBLE);
                v_double.setVisibility(View.INVISIBLE);

                Intent intent = new Intent(signup2.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
