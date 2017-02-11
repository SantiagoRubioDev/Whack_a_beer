package com.example.santiago.whack_a_beer;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Debug;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GameActivity extends AppCompatActivity implements GameView.Callback, View.OnClickListener{

    //declaring gameview
    private GameView gameView;

    //declaring layout
    private FrameLayout game;
    private LinearLayout gameWidgets;

    //image button
    private ImageButton bucket1;
    private ImageButton bucket2;
    private ImageButton bucket3;
    private ImageButton bucket4;
    private ImageButton bucket5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("onCreate","Created my game activity");
        //setting the orientation to landscape
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        game = new FrameLayout(this);
        gameView = new GameView (this,this);
        gameWidgets = new LinearLayout (this);
        gameWidgets.setGravity(Gravity.CENTER);
        gameWidgets.setOrientation(LinearLayout.HORIZONTAL);

        TextView myText = new TextView(this);


        bucket1 = new ImageButton(this);
        bucket2 = new ImageButton(this);
        bucket3 = new ImageButton(this);
        bucket4 = new ImageButton(this);
        bucket5 = new ImageButton(this);
        init_bucket(this, this, bucket1,Gravity.CENTER);
        init_bucket(this, this, bucket2,Gravity.CENTER);
        init_bucket(this, this, bucket3,Gravity.CENTER);
        init_bucket(this, this, bucket4,Gravity.CENTER);
        init_bucket(this, this, bucket5,Gravity.CENTER);


        //myText.setText("rIZ..i");
        gameWidgets.addView(myText);

        game.addView(gameWidgets);
        game.addView(gameView);



        setContentView(game);
    }

    //pausing the game when activity is paused
    @Override
    protected void onPause() {
        super.onPause();
        gameView.pause();
    }

    //running the game when activity is resumed
    @Override
    protected void onResume() {
        super.onResume();
        gameView.resume();
    }

    //buttons from xml
    @Override
    public void onClick(View v) {

        if (v == bucket1) {
            Log.d("D","bucket1 tapped");
            gameView.tapped(1);
        }
        if (v == bucket2) {
            Log.d("D","bucket2 tapped");
            gameView.tapped(2);
        }
        if (v == bucket3) {
            Log.d("D","bucket3 tapped");
            gameView.tapped(3);
        }
        if (v == bucket4) {
            Log.d("D","bucket4 tapped");
            gameView.tapped(4);
        }
        if (v == bucket5) {
            Log.d("D","bucket5 tapped");
            gameView.tapped(5);
        }
    }

    private void init_bucket(Activity act, View.OnClickListener v, ImageButton b, int g){

        //b = new ImageButton(act);
        b.setMaxHeight(600);
        b.setForegroundGravity(g);
        b.setAdjustViewBounds(true);
        b.setScaleType(ImageView.ScaleType.FIT_CENTER);
        b.setImageResource(R.drawable.icebucket_front);
        b.setBackgroundResource(0);//background null
        b.setOnClickListener(v);

        LinearLayout l = new LinearLayout (act);
        l.setMinimumWidth(0);
        l.setWeightSum(1);
        l.setOrientation(LinearLayout.HORIZONTAL);
        l.setGravity(Gravity.CENTER);
        l.addView(b);
        gameWidgets.addView(l);
    }

    public void gameOver(){
        finish();
    }

}
