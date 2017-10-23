package com.example.jingluo.minesweeper;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import static android.R.attr.clickable;
import static android.R.attr.max;
import static android.R.attr.soundEffectsEnabled;
import static java.lang.Math.log;

public class MainActivity extends AppCompatActivity {
    private String data[] = new String[81];//current situation of the game
    private String results[] = new String[81];//the answer of the game

    private celladapter adapter;

    public TextView timertext;
    private int timecount;
    private Timer timer;

    private boolean resetselected = true;//whether reset is clicked

    private int numofmine;//the number of currently revealed mine
    private TextView numofflagedmine;
    private int number_of_mines = 0;//the total number of mines
    private int checkednum = 0;//records the number of clicked cells

    private ImageView expression;

    private MediaPlayer mp3;//background sound
    private MediaPlayer bomb;//bomb sound
    private MediaPlayer congratulations;//congratulation sound

    private Button hintbutton;
    private GridView gridview;
    private Button back;
    private Button startbutton;
    private Button resetbutton;

    private float normalizedvolume = -1;//represent volume(0-1)
    private int lefthintnum = -1;//the number of available hints

    private SQlitehelper db;//used to store game records in SQlite


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initial setting
        initsetting();

        //start button
        startbuttonsetting();

        //reset button
        resetbuttonsetting();

        //set hint button
        hintbuttonsetting();

        //set back button
        backbuttonsetting();


    }
    public void initsetting(){
        db = new SQlitehelper(this);

        //initialize data, results and timecount.
        resetdata();
        resetresults(globalvariables.level);
        timecount = 0;
        timer = null;

        //set hintbutton
        hintbutton = (Button) findViewById(R.id.hint);
        hintbutton.setEnabled(false);
        lefthintnum = globalvariables.hintnum;
        hintbuttonupdate();

        //expression image setting
        expression = (ImageView) findViewById(R.id.expression);

        //num of flaged mine
        numofflagedmine = (TextView) findViewById(R.id.numofmine);
        resetunflaggednum();

        //set backgroundimage
        setbackgroundimage();

        //set volume
        normalizedvolume = (float) log(globalvariables.volume+1)/10;

        //set grid view
        gridviewsetting();
        //timer
        timertext = (TextView) findViewById(R.id.timer);
    }
    public void resetunflaggednum(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                switch (globalvariables.level){
                    case 0: numofflagedmine.setText("008");
                        break;
                    case 1: numofflagedmine.setText("024");
                        break;
                    case 2: numofflagedmine.setText("040");
                        break;
                    default: break;
                }

            }
        });
    }
    public void gridviewsetting(){
        //grid view setting
        gridview = (GridView) findViewById(R.id.minegrid);
        adapter = new celladapter(this, data);
        gridview.setAdapter(adapter);
        gridview.setEnabled(false);

        //long tap
        gridview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){

            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(data[i] == "-1") {//flag an unchecked cell
                    checkednum++;
                    data[i] = "F";
                    adapter.clicked[i] = 2;
                    numofmine++;
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            updatenumofflagedmine();
                        }
                    });
                    adapter.refresh(data);
                    judge();//judge whether the user wins

                }else if(data[i] == "F"){//cancel the flag
                    checkednum--;
                    data[i] = "-1";
                    numofmine--;
                    adapter.clicked[i] = 0;
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            updatenumofflagedmine();
                        }
                    });
                    adapter.refresh(data);
                }else{//click a revealed non-mine cell
                    Toast.makeText(MainActivity.this,"Please choose an unchecked position", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });


        //single tap
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                resetselected = false;
                if(data[position] == "-1") {//this cell has not yet been clicked
                    adapter.clicked[position] = 1;
                    checkednum++;
                    if(results[position] == "*"){//if it is mine, pop up an alert showing that he lost the game
                        adapter.clicked[position] = 3;
                        adapter.refresh(data);
                        loseaction();



                    }else if (!results[position].equals("0")) { //if there is mine in surrounding and it is not mine, reveal the cell.


                        data[position] = results[position];
                        adapter.refresh(data);

                    } else {//if there is no mine in surrounding
                        //reveal the surrounding
                        data[position] = results[position];
                        revealsurrounding(position);
                    }
                    //if user has filled in all cells, check whether he wins or misflags some cells.
                    judge();
                }else{//this cell has been clicked

                    Toast.makeText(MainActivity.this, "Please choose an unchecked position", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    //start button setting
    public void startbuttonsetting(){
        //start button
        startbutton = (Button) findViewById(R.id.start);
        startbutton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                //check whether reset is clicked first
                if(resetselected == true){
                    //set and start background sound track
                    switch(globalvariables.backgroundmusic){
                        case "quickmusic":  mp3 = MediaPlayer.create(MainActivity.this, R.raw.quick);
                            break;
                        case "flexmusic":  mp3 = MediaPlayer.create(MainActivity.this, R.raw.flex);
                            break;
                        case "othersmusic":  mp3 = MediaPlayer.create(MainActivity.this, R.raw.flex);//add new one later?????
                            break;
                        default: break;
                    }
                    mp3.setLooping(true);
                    mp3.setVolume(normalizedvolume, normalizedvolume);
                    mp3.start();
                    //enable gridview, hintbutton
                    gridview.setEnabled(true);
                    hintbutton.setEnabled(true);
                    timertext.setText("000");
                    resetselected = false;
                    timecount = 0;
                    numofmine = 0;
                    //start timer
                    timer = new Timer();
                    timer.scheduleAtFixedRate(new TimerTask() {


                        @Override
                        public void run() {
                            timecount++;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(timecount<10)

                                    {
                                        timertext.setText("00" + String.valueOf(timecount));

                                    }else if(timecount<100)

                                    {
                                        timertext.setText("0" + String.valueOf(timecount));

                                    }else if(timecount<1000)

                                    {
                                        timertext.setText(String.valueOf(timecount));

                                    }else{
                                        alert("Time out. If you want to play a new game, please click reset and start.");
                                        if(timer != null) {
                                            timer.cancel();
                                        }
                                        if(mp3!= null){
                                            mp3.release();
                                        }
                                    }
                                }
                            });
                        }
                    }, 0, 1*1000);

                }else{
                    Toast.makeText(MainActivity.this, "Please click reset first", Toast.LENGTH_SHORT).show();

                }

            }
        });

    }
    //reset button setting
    public void resetbuttonsetting(){
        resetbutton = (Button) findViewById(R.id.reset);

        resetbutton.setOnClickListener(new View.OnClickListener() {

            public void resetparameters(int level){
                //reset resetsekected, numofmine, checkednum, data, results, update UI, hintnum and stop timer

                lefthintnum = globalvariables.hintnum;
                hintbuttonupdate();
                hintbutton.setEnabled(false);
                resetselected = true;
                numofmine = 0;
                adapter.resetclicked();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        switch (globalvariables.level){
                            case 0: numofflagedmine.setText("008");
                                break;
                            case 1: numofflagedmine.setText("024");
                                break;
                            case 2: numofflagedmine.setText("040");
                                break;
                            default: break;
                        }

                        timertext.setText("000");
                        expression.setImageResource(R.drawable.defaultexpression);

                    }
                });
                checkednum = 0;
                if(timer != null) {
                    timer.cancel();
                }

                resetresults(level);
                resetdata();
                //System.out.println(Arrays.toString(data));
                adapter.refresh(data); //pass update list

                gridview.setEnabled(false);
            }
            @Override
            public void onClick(View view) {
                //if background music is playing, release it.

                if(mp3!= null){
                    mp3.release();
                }

                resetparameters(globalvariables.level);

            }
        });
    }
    //hint button setting
    public void hintbuttonsetting(){
        hintbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lefthintnum--;

                if(lefthintnum<0){
                    Toast.makeText(MainActivity.this,"Cannot provide hints anymore", Toast.LENGTH_SHORT).show();
                }else {
                    hintbuttonupdate();
                    hint();
                }

            }
        });
    }
    //back button setting
    public void backbuttonsetting(){
        back = (Button) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mp3!= null){
                    mp3.release();
                }
                startActivity(new Intent(MainActivity.this,WelcomeView.class));
            }
        });
    }
    //update the text of hint button
    public void hintbuttonupdate(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hintbutton.setText("hint:"+Integer.toString(lefthintnum));
            }
        });
    }

    //generate the hint location
    public int generatehintindex(){
        int samecount = 0;//record the number of cells that are correctly labeled.

        Map dict = new HashMap();//a dictionary represents all the cells that can be provided as hints. (subindex(1-):index)

        int subindex = 0;

        for(int i = 0; i < 81; i++) {

            if (results[i] == "*" && data[i] == "F"){//correctly flag

                samecount++;

            }else if(results[i] == data[i]){//revealed non-mine
                samecount++;

            }else{
                subindex++;
                dict.put(subindex, i);

            }
        }

        //randomly choose an index to be the hint's location.
        int randnumrange = 81 - samecount;
        Random random = new Random();
        int randsubindex = random.nextInt(randnumrange - 1 + 1) + 1;//randsubindex[1,)

       // Log.d("randsubindex:", Integer.toString(randsubindex));

        return (int) dict.get((Integer) randsubindex);
    }
    //update the number of unrevealed mines
    public void updatenumofflagedmine() {
        int numnotrevealedmine = number_of_mines - numofmine;
        if (numnotrevealedmine > -100 && numnotrevealedmine <= -10) {
            numofflagedmine.setText(String.valueOf(numnotrevealedmine));

        } else if (numnotrevealedmine > -10 && numnotrevealedmine < 0) {
            numofflagedmine.setText("-" + String.valueOf(numnotrevealedmine));

        } else if (numnotrevealedmine < 10 && numnotrevealedmine >= 0) {
            numofflagedmine.setText("00" + String.valueOf(numnotrevealedmine));

        } else {
            numofflagedmine.setText("0" + String.valueOf(numnotrevealedmine));

        }
    }
    //provide hint
    public void hint(){
        int index = -1;//index ranging from 0 to 80

        index = generatehintindex();

        if(data[index] == "-1" && results[index] != "*"){ //reveal a non-mine which is not revealed
            checkednum++;
            adapter.clicked[index] = 1;
        }else if(data[index] == "-1" && results[index] == "*"){  //reveal a mine which is not revealed
            checkednum++;
            numofmine++;
            adapter.clicked[index] = 2;
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    updatenumofflagedmine();
                }
            });
        }else if(data[index] == "F" && results[index] != "*"){//reveal a misflagged cell: check revealed
            numofmine--;
            adapter.clicked[index] = 1;
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    updatenumofflagedmine();
                }
            });
        }
        //update data variables
        if(results[index].equals("*")){
            data[index] = "F";
        }else {
            data[index] = results[index];
        }
        adapter.refresh(data);
        judge();//check whether user wins

    }



    //alert funciton
   public void alert(String message){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
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


    //reset the result of the game
    public  void resetresults(int level){
        //initialize results
        for(int i = 0; i < 81; i++) {
            results[i] = Integer.toString(0);
        }

        //get the number of mines based on game level
        switch (level){
            case 0: number_of_mines = 8;
                    break;
            case 1: number_of_mines = 24;
                    break;
            case 2: number_of_mines = 40;
                    break;
            default: break;
        }
        //create the game results
        int mineno = 1;
        while(mineno <= number_of_mines){
            int index = getrandomminelocation();//randomly get an index
            int x = index%9;//column
            int y = (index-x)/9;//row
            //update the mine position and its surrounding
            for (int xmove=-1; xmove <= 1; xmove++){
                for (int ymove=-1; ymove <= 1; ymove++){
                    if(xmove == 0 && ymove == 0){
                        results[index] = "*";
                    }else{
                        int surroundingindex = computedsurroundingindex(index, xmove, ymove);
                        if(surroundingindex != -1){
                            int getnum = Integer.parseInt(results[surroundingindex]);
                            results[surroundingindex] = Integer.toString(getnum+1);
                        }
                    }

                }
            }
            mineno++;
        }
        //System.out.println("&&&&&&&&&&&"+Arrays.toString(results));

    };
    //reset all the data in the current gridview as -1
    public  void resetdata(){
        for(int i = 0; i < 81; i++) {
            data[i] = Integer.toString(-1);
        }
    };
    //randomly generate an index of which results[index] is not a mine
    public int getrandomminelocation(){
        Random random = new Random();
        int index = random.nextInt(80 - 0 + 1) + 0;

        while(results[index] == "*"){

            index = random.nextInt(80 - 0 + 1) + 0;
        }
        return index;
    }
    //compute the surrounding index given the center index, xmove and ymove
    public int computedsurroundingindex(int index, int xmove, int ymove){
        int surroundingindex = -1;
        int x = index%9;
        int y = (index-x)/9;
        int newx = x + xmove;
        int newy = y + ymove;
        if(newx < 0 || newy < 0 || newx+ 9*newy > 80|| newx > 8 || newy >8){
            ;
        }else if(results[newx+ 9*newy] == "*"){
            ;
        }else{
            surroundingindex = newx+ 9*newy;
        }
        return surroundingindex;
    }
    public boolean checkresults(){
        int i;
        for(i = 0; i < 81; i++){
            if(results[i] == "*"){
                //flagged
                continue;
            }
            if(results[i] != data[i]){
                break;
            }
        }
        if(i <=80){
            //flag something wrong
            return false;
        }else{
            return true;
        }
    }
    public void judge(){
         if(checkednum == 81){//if user clicked all cells in the gridview
             //check whether all the cells are correctly revealed.
            if(checkresults()){//correct
              winaction();
            }else{//not correct(flag something wrong)
                Toast.makeText(MainActivity.this, "you have flaged something wrong", Toast.LENGTH_SHORT).show();
            }

        }else if((number_of_mines - numofmine)==0){//if users checked all the mines
            //check whether the user correctly flaged all the mine
            int correctedflagednum = 0;
            for(int i = 0; i < 81; i++){
               if(results[i].equals("*")){
                   if(data[i].equals("F")){
                       correctedflagednum++;
                   }
               }
            }

            if(correctedflagednum == number_of_mines){//correctly check all the mines
               winaction();
            }

        }
    }
    public void winaction(){
        alert("You win");
        winsoundcontrol();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                expression.setImageResource(R.drawable.happyexpression);
            }

        });

        if(timer!=null){
            timer.cancel();
        }
        gridview.setEnabled(false);
        db.insertrecord(Integer.parseInt(timertext.getText().toString()), globalvariables.level);


    }
    public void loseaction(){
        //stop timer
        if(timer != null) {
            timer.cancel();
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                expression.setImageResource(R.drawable.badexpression);

            }

        });
        alert("You lose. If you want to play a new game, click reset and start.");
        gridview.setEnabled(false);
        losesoundcontrol();
    }
    public void losesoundcontrol(){
        mp3.release();
        bomb = MediaPlayer.create(this, R.raw.bomb);
        bomb.setVolume(normalizedvolume,normalizedvolume);
        bomb.start();
        hintbutton.setEnabled(false);

    }
    public void winsoundcontrol(){
        mp3.release();
        congratulations = MediaPlayer.create(this, R.raw.congratulations);
        congratulations.setVolume(normalizedvolume,normalizedvolume);
        congratulations.start();
        hintbutton.setEnabled(false);

    }
    public void setbackgroundimage() {
        ImageView imageview = (ImageView) findViewById(R.id.backgroundimage);
        switch (globalvariables.backgroundimage) {
            case "blue":
                imageview.setBackgroundResource(R.drawable.gamebackgroundblue);
                break;
            case "green":
                imageview.setBackgroundResource(R.drawable.gamebackgroundgreen);
                break;
            case "catoon":
                imageview.setBackgroundResource(R.drawable.gamebackgroundcatoon);
                break;
            case "moutain":
                imageview.setBackgroundResource(R.drawable.gamebackgroundmountain);
                break;
            case "undersea":
                imageview.setBackgroundResource(R.drawable.gamebackgroundundersea);
                break;
            default:
                break;
        }

    }
    public void revealsurrounding(int position){
        Vector<Integer> surroundingpositions = new Vector<Integer>();

        surroundingpositions = getsurroundingpositions(position);
        for(int i = 0; i < surroundingpositions.size(); i++){
            int nearbyposition = surroundingpositions.elementAt(i);
            if(adapter.clicked[nearbyposition] == 0){
                //the cell has neither been revealed nor flagged
                checkednum++;
                data[nearbyposition] = results[nearbyposition];
                adapter.clicked[nearbyposition] = 1;//reveal it
                if( data[nearbyposition].equals("0")){
                    revealsurrounding(nearbyposition);
                }
                adapter.refresh(data);

            }
        }

    }
    public Vector<Integer> getsurroundingpositions(int position){
        Vector<Integer> surroundingpositions = new Vector<Integer>();
        int currentrow = position/9;
        int currentcolumn = position%9;
        for(int i = -1; i <= 1; i ++){
            for(int j = -1; j <= 1; j++){
                int row = currentrow + i;
                int column = currentcolumn + j;
                if(row < 0 || column < 0 || column > 8 || row > 8){
                    ;
                }else if(i == 0 && j==0){
                    ;
                }else{
                    int newindex = computesurroundingindex(row, column);
                    surroundingpositions.addElement(newindex);
                }
            }
        }

        return surroundingpositions;
    }
    public int computesurroundingindex(int row, int column){
       return row*9 +column;
    }

}
