package com.example.jeandan.smart_task;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {

    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //txt = (TextView) findViewById(R.id.textView3);
        btn = (Button) findViewById(R.id.button);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity2();

            }
        });
        /*Intent myIntent = new Intent(MainActivity.this,DeclenchorActivity.class);
        startService(myIntent);**/

    }

    public void openActivity2(){
        Intent intent = new Intent(this, DeclenchorActivity.class);
        startActivity(intent);

    }

    public void goToTorchActivity(View view){
        Intent torchIntent = new Intent(this, TorchActivity.class);
        startActivity(torchIntent);

    }


}
