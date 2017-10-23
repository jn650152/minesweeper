package com.example.jingluo.minesweeper;

/**
 * Created by jingluo on 2017-10-13.
 */

public class record {
    public int time = -1;//time taken for the game record
    public int level = -1;//game level for the record
    public int id = -1;//record id

    public record(int id, int time, int level){
        this.id = id;
        this.time = time;
        this.level = level;
    }
}
