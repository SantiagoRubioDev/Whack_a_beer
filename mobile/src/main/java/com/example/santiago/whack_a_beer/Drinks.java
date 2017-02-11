package com.example.santiago.whack_a_beer;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.os.Handler;
import android.os.SystemClock;

import java.util.Random;


import static java.security.AccessController.getContext;

/**
 * Created by Santiago on 2/10/2017.
 */
enum States{
    idle,
    start,
    up,
    wait,
    down,
    end,
    clicked,
    flying
};

enum Item{
    beer,
    empty,
    cocktail,
    bomb,
    nothing
}

class Drink{
    private Bitmap bm;
    private int speed;
    private Item item;

    public Drink(){
        bm=Bitmap.createBitmap(100,100, Bitmap.Config.ARGB_8888);
        speed=0;
        item=Item.nothing;
    }

    public Drink(Item _item, int _speed, Bitmap _bm){
        bm=_bm;
        speed=_speed;
        item=_item;
    }

    public Bitmap getBm() {
        return bm;
    }

    public int getSpeed() {
        return speed;
    }

    public Item getItem() {
        return item;
    }
}

public class Drinks {
    //Bitmap to get character from image
    private Bitmap boom_bm;

    //drink items
    private Drink[] drink_item;
    private Drink cur_drink_item;

    //handler
    private Handler handler;
    private long timer;
    private Callback observer;

    //random
    private int[] prob_arr;
    private Random rand;

    //resources
    private Resources res;

    //screen size (display metrics)
    DisplayMetrics dm;
    int bm_width;
    int bm_height;

    //coordinates
    private int x;
    private int y;
    private int max_y;
    private int min_y;

    //motion speed of the character
    private States state;
    private int speed_lvl;
    private int max_speed_lvl;

    //constructor
    public Drinks(Callback _observer, Context context, DisplayMetrics _dm, int init_pos_x, int init_pos_y) {

        handler  = new Handler();

        state = States.idle;

        dm = _dm;
        res = context.getResources();

        bm_width = 170*dm.widthPixels/1280;
        bm_height = 170*dm.heightPixels/720;

        x = init_pos_x+3*bm_width/10;
        y = init_pos_y+bm_height;
        min_y = init_pos_y+bm_height/5;
        max_y = y;

        //Getting bitmap from drawable resource
        boom_bm = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, R.drawable.boom),bm_width,bm_height,false);

        drink_item = new Drink[4];
        drink_item[0] = new Drink(Item.empty,10,Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, R.drawable.empty_glass),bm_width,bm_height,false));
        drink_item[1] = new Drink(Item.beer,10,Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, R.drawable.beer_glass),bm_width,bm_height,false));
        drink_item[2] = new Drink(Item.cocktail,20,Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, R.drawable.cocktail_glass),bm_width,bm_height,false));
        drink_item[3] = new Drink(Item.bomb,15,Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, R.drawable.bomb),bm_width,bm_height,false));

        cur_drink_item = new Drink();

        //empty[0]=2, beer[1]=6, cocktail[2]=1, bomb[3]=1, total=10
        rand = new Random();
        prob_arr = new int[]{0,0,1,1,1,1,1,1,2,3};

        speed_lvl = 1;
        max_speed_lvl = 50;

        timer = 0;
        observer = _observer;
    }

    //Method to update coordinate of character
    public void update(){
        if(state==States.start){
            cur_drink_item = rand_drink_item();
            state=States.up;
            timer=SystemClock.elapsedRealtime();
        }else if(state==States.up){
            if(y<=min_y){
                state=States.wait;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(state==States.wait){
                            state=States.down;
                        }
                    }
                },500-speed_lvl*9);
            }else{
                y-=(cur_drink_item.getSpeed()+speed_lvl);
            }
        }else if(state==States.down){
            if(y>=max_y){
                state=States.end;
                observer.drink_miss_event(cur_drink_item.getItem());
            }else {
                y+=(cur_drink_item.getSpeed()+speed_lvl);
            }
        }else if (state==States.end){
            state=States.idle;
            cur_drink_item = new Drink();
            y=max_y;
            if(speed_lvl<max_speed_lvl){
                speed_lvl++;
            }
        }else if(state==States.clicked) {
            observer.drink_click_event(timer, cur_drink_item.getItem());
            state = States.flying;
        }else if(state==States.flying){
            y -= 80;
            if (y < -200) {
                state = States.end;
            }
        }
    }

    private Drink rand_drink_item(){
        return drink_item[prob_arr[rand.nextInt(prob_arr.length)]];
    }


    public boolean start(){
        if(state==States.idle){
            state=States.start;
            return true;
        }
        return false;
    }

    public void click(){
        if(state==States.up ||state==States.wait ||state==States.down ){
            state=States.clicked;
            timer = SystemClock.elapsedRealtime()-timer;
        }
    }
    /*
    * These are getters you can generate it autmaticallyl
    * right click on editor -> generate -> getters
    * */
    public Bitmap getBitmap() {
        return cur_drink_item.getBm();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    interface Callback {
        void drink_click_event(long timeElapsed, Item _item);
        void drink_miss_event(Item _item);
    }

}
