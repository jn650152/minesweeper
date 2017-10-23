package com.example.jingluo.minesweeper;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;

import java.util.List;
//ranking view
public class ranking extends AppCompatActivity {
    private SQlitehelper db;
    private rankingadapter adapter;
    private GridView gridview;
    List<record> records;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        //initialize db
        db = new SQlitehelper(this);

        //ranking top 10 for different levels
        records = db.getAllDatasforlevel(0);//default: the lowest level
        for(record one : records){
            Log.d("one level: ",Integer.toString(one.time));
        }
        //populate data in grid view
        adapter = new rankingadapter(this, records);
        gridview = (GridView) findViewById(R.id.rankinggridview);
        gridview.setAdapter(adapter);

        //settitng three buttons for different levels
        Button button1 = (Button) findViewById(R.id.level1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                records.clear();
                records = db.getAllDatasforlevel(0);
                adapter.refresh(records);
            }
        });
        Button button2 = (Button) findViewById(R.id.level2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                records.clear();
                records = db.getAllDatasforlevel(1);
                adapter.refresh(records);
            }
        });
        Button button3 = (Button) findViewById(R.id.level3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                records.clear();
                records = db.getAllDatasforlevel(2);
                adapter.refresh(records);
            }
        });
        //back button
        Button backbutton = (Button) findViewById(R.id.backbutton);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ranking.this, WelcomeView.class));
            }
        });



    }



}
