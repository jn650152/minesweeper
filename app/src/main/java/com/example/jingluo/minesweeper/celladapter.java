package com.example.jingluo.minesweeper;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * Created by jingluo on 2017-10-02.
 */

public class celladapter extends BaseAdapter{
    private Context c;
    private String data[];
    public int clicked[] = new int[81];//0: not tapped, 1: short tapped, 2: long tapped

    public celladapter(Context c, String[] data) {
        this.c = c;
        this.data = data;
        resetclicked();
    }
    public void refresh(String[] data)//update grid
    {

        this.data = data;
        notifyDataSetChanged();

    }

    @Override
    public int getCount() {
        return data.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {//populate cells
        TextView textview;

        if (view == null){
            textview = new TextView(c);

        }else{
            textview = (TextView) view;
        }
        textview.setBackgroundResource(0);
        textview.setText("");

        if(clicked[i] == 1){//short tap cell without mine
            textview.setBackgroundColor(Color.parseColor("#d3d3d3"));
            if(!data[i].equals("0")){
                textview.setText(data[i]);
            }

        }else if(clicked[i] == 2){//long tap cell
            textview.setBackgroundResource(R.drawable.flagbackground);

        }else if(clicked[i] == 3){//short tap cell with mine
            textview.setBackgroundResource(R.drawable.minebackground);

        }else{//not clicked cell
            textview.setBackgroundResource(R.drawable.coveredbackground);
        }
        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(110,110);
        textview.setLayoutParams(lp);
        textview.setGravity(Gravity.CENTER);
        return textview;
    }
    public void resetclicked(){//reset clicked
        for(int i = 0; i < 81; i++) {
            clicked[i] = 0;
        }
    }

}
