package com.example.luispaulo.ibeacons.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;

import com.example.luispaulo.ibeacons.R;

public class Chooser extends AppCompatActivity {

    private AppCompatButton btnPooling;
    private AppCompatButton btnService;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chooser);
        toolbar = (Toolbar)findViewById(R.id.toolbar_chooser);
        setSupportActionBar(toolbar);

        btnPooling = (AppCompatButton)findViewById(R.id.chooser_btnPooling);
        btnService = (AppCompatButton)findViewById(R.id.chooser_btnService);

        btnPooling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Chooser.this, ScanPoolling.class));
            }
        });

        btnService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Chooser.this, ServiceRasp.class));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chooser, menu);
        return true;
    }
}
