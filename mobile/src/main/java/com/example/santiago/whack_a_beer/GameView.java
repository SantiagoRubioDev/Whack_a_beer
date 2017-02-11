package com.example.santiago.whack_a_beer;

/**
 * Created by Santiago on 2/5/2017.
 */
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceView;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Canvas;
import android.view.SurfaceHolder;
import android.graphics.Color;
import android.os.Handler;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Random;

public class GameView extends SurfaceView implements Drinks.Callback, Runnable {

    volatile boolean playing;
    private Thread gameThread = null;

    //screen size (display metrics)
    DisplayMetrics dm;
    int bucket_width;
    int bucket_height;
    int buck_space;
    int buck1_pos_x;
    int buck1_pos_y;
    int buck2_pos_x;
    int buck2_pos_y;
    int buck3_pos_x;
    int buck3_pos_y;
    int buck4_pos_x;
    int buck4_pos_y;
    int buck5_pos_x;
    int buck5_pos_y;

    int heart_width;
    int heart_height;
    int heart_space;
    int heart1_pos_x;
    int heart1_pos_y;
    int heart2_pos_x;
    int heart2_pos_y;
    int heart3_pos_x;
    int heart3_pos_y;

    //adding the player to this class
    //private Player player;
    private Drinks[] drinks;
    private Handler handler;
    private Random rand;
    private int wait_time;
    private int min_wait_time;

    //These objects will be used for drawing
    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder surfaceHolder;
    private Resources res;
    private Bitmap background_bm;
    private Bitmap icebucket_back_bm;
    private Bitmap icebucket_front_bm;
    private Bitmap heart_bm;

    //gameplay variables
    private int lives;
    private int points;
    private ArrayList<Long>  reaction_times;
    private OutputStreamWriter file_out;
    private Callback observer;

    public GameView(Callback _observer, Context context) {
        super(context);
        observer=_observer;

        //display metrics
        dm = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager()
                .getDefaultDisplay()
                .getMetrics(dm);
        bucket_width = 250*dm.widthPixels/1280;
        bucket_height = 400*dm.heightPixels/720;
        buck_space = dm.widthPixels/256;
        buck1_pos_x=buck_space;
        buck1_pos_y= (dm.heightPixels-bucket_height)/2;
        buck2_pos_x= bucket_width+2*buck_space;
        buck2_pos_y= (dm.heightPixels-bucket_height)/2;
        buck3_pos_x= 2*bucket_width+3*buck_space;
        buck3_pos_y=(dm.heightPixels-bucket_height)/2;
        buck4_pos_x= 3*bucket_width+4*buck_space;
        buck4_pos_y=(dm.heightPixels-bucket_height)/2;
        buck5_pos_x= 4*bucket_width+5*buck_space;
        buck5_pos_y=(dm.heightPixels-bucket_height)/2;

        heart_width = 110*dm.widthPixels/1280;
        heart_height = 110*dm.heightPixels/720;
        heart_space = dm.widthPixels/150;
        heart1_pos_x=heart_space+buck_space;
        heart1_pos_y= dm.heightPixels/70;
        heart2_pos_x= heart_width+2*buck_space+heart_space;
        heart2_pos_y= dm.heightPixels/70;
        heart3_pos_x= 2*heart_width+3*buck_space+heart_space;
        heart3_pos_y= dm.heightPixels/70;

        //initializing player object
        //player = new Player(context);
        drinks = new Drinks[5];
        drinks[0] = new Drinks(this, context,dm,buck1_pos_x,buck1_pos_y);
        drinks[1] = new Drinks(this, context,dm,buck2_pos_x,buck2_pos_y);
        drinks[2] = new Drinks(this, context,dm,buck3_pos_x,buck3_pos_y);
        drinks[3] = new Drinks(this, context,dm,buck4_pos_x,buck4_pos_y);
        drinks[4] = new Drinks(this, context,dm,buck5_pos_x,buck5_pos_y);


        //initializing drawing objects
        surfaceHolder = getHolder();
        paint = new Paint();
        res = getResources();

        background_bm = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, R.drawable.background),dm.widthPixels,dm.heightPixels,false);
        icebucket_back_bm = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, R.drawable.icebucket_back),bucket_width,bucket_height,false);
        icebucket_front_bm = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, R.drawable.icebucket_front),bucket_width,bucket_height,false);
        heart_bm = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, R.drawable.heart),heart_width,heart_height,false);

        lives=3;
        points = 0;
        reaction_times = new ArrayList<>();

        try {
            file_out = new OutputStreamWriter(context.openFileOutput("reaction.txt", Context.MODE_PRIVATE));
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }



        rand = new Random();
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                actions();
            }
        },500);

        min_wait_time = 50;
        wait_time = 2000;
    }

    private void actions(){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                wait_time -= 50;
                if(drinks[rand.nextInt(drinks.length)].start() == false){
                    if(drinks[rand.nextInt(drinks.length)].start() == false){
                        if(drinks[rand.nextInt(drinks.length)].start() == false){
                            wait_time +=50;
                        }
                    }
                }
                if(wait_time<min_wait_time){
                    wait_time=min_wait_time;
                }
                actions();
            }
        },wait_time);
    }

    public void drink_click_event(long timeElapsed, Item item){
        if(item == Item.empty){
            lives--;
        }else if(item == Item.beer){
            points+=100;
            reaction_times.add(timeElapsed);
        }else if(item == Item.cocktail){
            points+=500;
        }else if(item == Item.bomb){
            lives=0;
        }
    }

    public void drink_miss_event(Item item){
        if(item == Item.empty){
            points+=50;
        }else if(item == Item.beer){
            lives--;
        }else if(item == Item.cocktail){
            //points+=500;
        }else if(item == Item.bomb){
            points+=50;
        }
    }

    @Override
    public void run() {
        while (playing) {
            update();
            draw();
            control();
        }
    }

    private void update() {
        //updating player position
        //player.update();
        for(int i=0;i<drinks.length;i++){
            drinks[i].update();
        }
        if(lives<=0){
            gameOver();
        }
    }

    private void draw() {
        //checking if surface is valid
        if (surfaceHolder.getSurface().isValid()) {
            //locking the canvas
            canvas = surfaceHolder.lockCanvas();
            //drawing a background
            canvas.drawBitmap(background_bm,0,0,paint);//.drawColor(Color.BLACK);
            canvas.drawBitmap(icebucket_back_bm, buck1_pos_x, buck1_pos_y, paint);
            canvas.drawBitmap(icebucket_back_bm, buck2_pos_x, buck2_pos_y, paint);
            canvas.drawBitmap(icebucket_back_bm, buck3_pos_x, buck3_pos_y, paint);
            canvas.drawBitmap(icebucket_back_bm, buck4_pos_x, buck4_pos_y, paint);
            canvas.drawBitmap(icebucket_back_bm, buck5_pos_x, buck5_pos_y, paint);
            //Drawing the player
            for(int i=0;i<drinks.length;i++){
                canvas.drawBitmap(
                        drinks[i].getBitmap(),
                        drinks[i].getX(),
                        drinks[i].getY(),
                        paint);
            }

            //drawing front bucket
            canvas.drawBitmap(icebucket_front_bm, buck1_pos_x, buck1_pos_y, paint);
            canvas.drawBitmap(icebucket_front_bm, buck2_pos_x, buck2_pos_y, paint);
            canvas.drawBitmap(icebucket_front_bm, buck3_pos_x, buck3_pos_y, paint);
            canvas.drawBitmap(icebucket_front_bm, buck4_pos_x, buck4_pos_y, paint);
            canvas.drawBitmap(icebucket_front_bm, buck5_pos_x, buck5_pos_y, paint);
            //drwaing hearts
            if(lives>=1) {
                canvas.drawBitmap(heart_bm, heart1_pos_x, heart1_pos_y, paint);
            }
            if(lives>=2){
                canvas.drawBitmap(heart_bm, heart2_pos_x, heart2_pos_y, paint);
            }
            if(lives >=3){
                canvas.drawBitmap(heart_bm, heart3_pos_x, heart3_pos_y, paint);
            }

            paint.setColor(Color.WHITE);
            paint.setTextSize(canvas.getHeight()/15);
            canvas.drawText("Score: "+Integer.toString(points),7*canvas.getWidth()/10,canvas.getHeight()/15,paint);

            //Unlocking the canvas
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    private void control() {
        try {
            gameThread.sleep(17);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void pause() {
        playing = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
        }
    }

    public void resume() {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void tapped(int bucket){
        drinks[bucket-1].click();
    }

    public void gameOver(){
        try {
            OutputStreamWriter outputStreamWriter = file_out;
            for(int i=0;i<reaction_times.size();i++){
                outputStreamWriter.write(Long.toString(reaction_times.get(i)));
            }
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
        observer.gameOver();

    }

    interface Callback {
        public void gameOver();
    }
}