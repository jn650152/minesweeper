package com.example.jingluo.minesweeper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by jingluo on 2017-10-16.
 */

public class rankingadapter extends BaseAdapter {
    private Context c;
    private List<record> records;
    private int num = -1;//the number of records in the current ranking view

    public rankingadapter(Context c, List<record> records) {
        this.c = c;
        this.records = records;
    }

    @Override
    public int getCount() {
        num = records.size();
        if(num == 0){
            return 1;
        }else {
            return records.size();
        }
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View grid;
        TextView rankingnum;
        TextView time;
        LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(num != 0) {//records exist
            if (convertView == null) {
                grid = new View(c);
                grid = inflater.inflate(R.layout.custom_ranking_cell, null);


            } else {
                grid = (View) convertView;
            }
            rankingnum = (TextView) grid.findViewById(R.id.rankingnum);
            time = (TextView) grid.findViewById(R.id.time);
            rankingnum.setText(Integer.toString(position + 1) + ". ");
            time.setText(Integer.toString(records.get(position).time) + " sec");
        }else{//no record exists
            grid = new View(c);
            grid = inflater.inflate(R.layout.custom_ranking_cell, null);
            rankingnum = (TextView) grid.findViewById(R.id.rankingnum);
            time = (TextView) grid.findViewById(R.id.time);
            rankingnum.setText( "No ");
            time.setText("Record");
        }
        return grid;
    }
    public void refresh(List<record> records)//update grid view
    {

        this.records = records;
        notifyDataSetChanged();

    }

}
