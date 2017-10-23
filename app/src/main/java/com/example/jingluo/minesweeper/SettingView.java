package com.example.jingluo.minesweeper;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;

public class SettingView extends AppCompatActivity {
    //music choices
    CheckBox flexmusic;
    CheckBox quickmusic;
    CheckBox othermusic;

    //level choices
    CheckBox beginner;
    CheckBox medium;
    CheckBox advanced;

    //background choices
    CheckBox undersea;
    CheckBox green;
    CheckBox blue;
    CheckBox mountain;
    CheckBox catoon;

    //volume selection
    SeekBar volumeseekbar;
    EditText hintnumedittext;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_view);

        //dismiss keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        //get UI controls
        getuicontrols();

        //update current setting
        updatecurrentsetting();

        setbackgroundmusicfield();

        setlevelfield();

        setbackgroundimagefield();

        savebuttonaction();



    }
    public void getuicontrols(){
        flexmusic = (CheckBox) findViewById(R.id.flex);
        quickmusic = (CheckBox) findViewById(R.id.quick);
        othermusic = (CheckBox) findViewById(R.id.others);


        beginner = (CheckBox) findViewById(R.id.checkboxbeginner);
        medium = (CheckBox) findViewById(R.id.checkboxmedium);
        advanced = (CheckBox) findViewById(R.id.checkboxadvanced);

        undersea = (CheckBox) findViewById(R.id.gamebackgroundundersea);
        green = (CheckBox) findViewById(R.id.gamebackgroundgreen);
        blue = (CheckBox) findViewById(R.id.gamebackgroundblue);
        mountain = (CheckBox) findViewById(R.id.gamebackgroundmountain);
        catoon = (CheckBox) findViewById(R.id.gamebackgroundcatoon);

        volumeseekbar = (SeekBar) findViewById(R.id.seekBar);
        hintnumedittext = (EditText) findViewById(R.id.hintnum);
    }
    public void updatecurrentsetting(){
        switch(globalvariables.level){
            case 0: beginner.setChecked(true);
                //Log.d("beginner level:", "$$$$$$$");
                break;
            case 1: medium.setChecked(true);
                //Log.d("medium level:", "$$$$$$$");
                break;
            case 2: advanced.setChecked(true);
                //Log.d("advanced level:", "$$$$$$$");
                break;
            default: break;
        }
        switch(globalvariables.backgroundmusic){
            case "flexmusic": flexmusic.setChecked(true);
                break;
            case "quickmusic": quickmusic.setChecked(true);
                break;
            case "othersmusic": othermusic.setChecked(true);
                break;

        }
        switch(globalvariables.backgroundimage) {
            case "undersea": undersea.setChecked(true);
                break;
            case "green": green.setChecked(true);
                break;
            case "blue": blue.setChecked(true);
                break;
            case "moutain": mountain.setChecked(true);
                break;
            case "catoon": catoon.setChecked(true);
                break;
            default: break;
        }


        hintnumedittext.setText(Integer.toString(globalvariables.hintnum));
        volumeseekbar.setProgress((int)globalvariables.volume -1);

    }
    public void setbackgroundmusicfield(){
        //set checkboxes for background music(only one checkbox can be selected)

        flexmusic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(flexmusic.isChecked()) {
                    quickmusic.setChecked(false);
                    othermusic.setChecked(false);
                    globalvariables.backgroundmusic = "flexmusic";
                }
            }
        });
        quickmusic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(quickmusic.isChecked()) {
                    flexmusic.setChecked(false);
                    othermusic.setChecked(false);
                    globalvariables.backgroundmusic = "quickmusic";
                }
            }
        });
        othermusic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(othermusic.isChecked()) {
                    quickmusic.setChecked(false);
                    flexmusic.setChecked(false);
                    globalvariables.backgroundmusic = "othersmusic";
                }
            }
        });
    }
    public void setbackgroundimagefield(){
        //set checkboxes for level(only one checkbox can be selected)
        undersea.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(undersea.isChecked()) {
                    green.setChecked(false);
                    blue.setChecked(false);
                    mountain.setChecked(false);
                    catoon.setChecked(false);
                    globalvariables.backgroundimage = "undersea";
                }
            }
        });
        green.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(green.isChecked()) {
                    blue.setChecked(false);
                    undersea.setChecked(false);
                    mountain.setChecked(false);
                    catoon.setChecked(false);
                    globalvariables.backgroundimage = "green";
                }
            }
        });
        blue.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(blue.isChecked()) {
                    green.setChecked(false);
                    undersea.setChecked(false);
                    mountain.setChecked(false);
                    catoon.setChecked(false);
                    globalvariables.backgroundimage = "blue";
                }
            }
        });
        mountain.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(mountain.isChecked()) {
                    green.setChecked(false);
                    undersea.setChecked(false);
                    catoon.setChecked(false);
                    blue.setChecked(false);
                    globalvariables.backgroundimage = "moutain";
                }
            }
        });
        catoon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(catoon.isChecked()) {
                    green.setChecked(false);
                    undersea.setChecked(false);
                    blue.setChecked(false);
                    mountain.setChecked(false);
                    globalvariables.backgroundimage = "catoon";
                }
            }
        });
    }
    public void setlevelfield(){
        //set checkboxes for level(only one checkbox can be selected)

        beginner.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(beginner.isChecked()) {
                    medium.setChecked(false);
                    advanced.setChecked(false);
                    globalvariables.level = 0;
                }
            }
        });
        medium.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(medium.isChecked()) {
                    beginner.setChecked(false);
                    advanced.setChecked(false);
                    globalvariables.level = 1;
                }
            }
        });
        advanced.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(advanced.isChecked()) {
                    beginner.setChecked(false);
                    medium.setChecked(false);
                    globalvariables.level = 2;
                }
            }
        });

    }
    public void savebuttonaction(){
        //save button
        Button save = (Button) findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //set hint num
                if(hintnumedittext.getText().toString() == ""){
                    globalvariables.hintnum = -1;
                }else {
                    globalvariables.hintnum = Integer.parseInt(hintnumedittext.getText().toString());
                }

                //set volume
                globalvariables.volume = volumeseekbar.getProgress();

                //checking each fields
                Boolean levelcheckedflag = beginner.isChecked()||medium.isChecked()||advanced.isChecked();
                Boolean backgroundmusicflag = flexmusic.isChecked()||quickmusic.isChecked()||othermusic.isChecked();
                Boolean backgroundimageflag = undersea.isChecked()||green.isChecked()||blue.isChecked()||mountain.isChecked()||catoon.isChecked();
                if( globalvariables.hintnum != -1&&levelcheckedflag&&backgroundmusicflag&&backgroundimageflag){//setting is correct
                    startActivity(new Intent(SettingView.this, WelcomeView.class));
                }else{//setting is not correct
                    if( globalvariables.hintnum > 40){
                        alert("Sorry, we can only provide 40 hints at most for each game.");
                    }else {
                        alert("Each field needs to be filled in.");
                    }

                }


            }
        });
    }
    //alert funciton
    public void alert(String message){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(SettingView.this);
        builder1.setMessage(message);
        builder1.setCancelable(true);
        builder1.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();

    }

}
