package com.example.jingluo.minesweeper;


import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import android.widget.Toast;

//import com.hitomi.cmlibrary.CircleMenu;
//import com.hitomi.cmlibrary.OnMenuSelectedListener;

public class WelcomeView extends AppCompatActivity {

    private String logos[] = {"ranking","setting","enter game","share", "help"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_view);


        //circle menu setting
        CircleMenu circlemenu = (CircleMenu) findViewById(R.id.circlemenu);

        circlemenu.setMainMenu(Color.parseColor("#CDCDCD"),R.drawable.addlogo, R.drawable.removelogo)
                .addSubMenu(Color.parseColor("#7d1460"), R.drawable.rankinglogo)
                .addSubMenu(Color.parseColor("#76bcf2"), R.drawable.settinglogo)
                .addSubMenu(Color.parseColor("#ff0000"), R.drawable.entergame)
                .addSubMenu(Color.parseColor("#b0dca3"), R.drawable.sharelogo)
                .addSubMenu(Color.parseColor("#dce0fd"), R.drawable.helplogo)
                .setOnMenuSelectedListener(new OnMenuSelectedListener() {
                    @Override
                    public void onMenuSelected(int i) {

                        Toast.makeText(WelcomeView.this,"clicked "+logos[i], Toast.LENGTH_SHORT).show();
                        final int index =i;
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                jumptootherview(index);
                            }
                            public void jumptootherview(int index){
                                switch(index){
                                    case 0: startActivity(new Intent(WelcomeView.this, ranking.class));//ranking
                                        break;
                                    case 1: startActivity(new Intent(WelcomeView.this, SettingView.class));//setting
                                        break;
                                    case 2: startActivity(new Intent(WelcomeView.this, MainActivity.class));//enter a game
                                        break;
                                    case 3:shareresult();//share
                                        break;
                                    case 4: startActivity(new Intent(WelcomeView.this, help.class));//help
                                        break;
                                    default: break;

                                }
                            }
                        }, 1000);

                    }
                });
    }
    public void shareresult(){//the function that is called when you select share in the menu
       Intent shareintent = new Intent(android.content.Intent.ACTION_SEND);
        shareintent.setType("text/plain");
        String sharebody = "I like the minesweeper game. Join with me.";
        shareintent.putExtra(Intent.EXTRA_SUBJECT, "A fantastic minesweeper game");
        shareintent.putExtra(Intent.EXTRA_TEXT, sharebody);
        startActivity(Intent.createChooser(shareintent,"Share by"));
    }
}
