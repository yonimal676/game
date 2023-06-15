package com.example.game;


import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.Random;

public class Enemy
{

    float x, y;
    float width, height;
    Bitmap enemyBitmap;
    Bitmap heartBitmap;
    int[] randomRegularBitmap = {R.drawable.enemy1, R.drawable.enemy2, R.drawable.enemy3, R.drawable.enemy4};

    int hearts;
    float speed; // number of pixels per iteration


    int initXP, xp;

    final float meter; // for graspable ratio.
    int groundHeight;

    String type;
    boolean shielded;
    int shield_counter;
    int added_damage;
    int shootCounter;
    ArrayList<Projectile> guard_projectiles;

    boolean toShowShot;

    int shootFrequency;

    boolean hasBleed;
    int bleed_frequency; // for each enemy who is effected by the blood effect - set a counter


    public Enemy(Resources res, int screenX, int screenY, int groundHeight, byte metersInTheScreen, float relativeInWave, String type, float deadX, float bobX)
    {

        this.type = type;
        this.groundHeight = groundHeight;

        meter = (float) screenX / metersInTheScreen; // TODO: remember that if you scale this up, the ball will NOT move in the same ratio!!

        shield_counter = 100;
        shielded = false;
        toShowShot = false;
        shootFrequency = 150;
        bleed_frequency = 160;

        switch (type)
        {
            case "regular":
                width = meter;
                height = meter * 1.2f;

                hearts = 1;
                x = screenX - width + (relativeInWave * width * 1.5f);
                y = screenY - groundHeight - height;

                speed = 400 / meter; // pixels per iteration
//                speed = 0;


                added_damage = 0;


                Random random = new Random();
                int randomIndex = random.nextInt(randomRegularBitmap.length);

                int randomImageResource = randomRegularBitmap[randomIndex];


                enemyBitmap = BitmapFactory.decodeResource(res, randomImageResource);
                enemyBitmap = Bitmap.createScaledBitmap(enemyBitmap, (int) width, (int) height, false);

                xp = 10;
                initXP = xp;
                break;


            case "ghost":

                width = meter;
                height = meter * 1.2f;

                hearts = 2;
                added_damage = 0;

                x = screenX - width + (relativeInWave * width * 1.5f);
                y = screenY - groundHeight - height;

                speed = 900 / meter; // pixels per iteration


                enemyBitmap = BitmapFactory.decodeResource(res, R.drawable.ghost);
                enemyBitmap = Bitmap.createScaledBitmap(enemyBitmap, (int) width, (int) height, false);

                xp = 20;
                initXP = xp;
                break;


            case "giant":

                width = meter * 1.75f;
                height = meter * 2f;

                hearts = 10;

                added_damage = 0;


                x = screenX - width + (relativeInWave * width * 1.5f);
                y = screenY - groundHeight - height;


                speed = 100 / meter; // pixels per iteration


                enemyBitmap = BitmapFactory.decodeResource(res, R.drawable.giant);
                enemyBitmap = Bitmap.createScaledBitmap(enemyBitmap, (int) width, (int) height, false);

                xp = 20;
                initXP = xp;
                break;


            case "skeleton": //not an enemy but could be here


                width = meter * 0.6f;
                height = meter * 0.9f;

                hearts = 1;

                x = deadX;
                y = screenY - groundHeight - height;

                speed = 400 / meter; // pixels per iteration


                enemyBitmap = BitmapFactory.decodeResource(res, R.drawable.skeleton);
                enemyBitmap = Bitmap.createScaledBitmap(enemyBitmap, (int) width, (int) height, false);

                xp = 0;
                break;


            case "crusader":
                width = meter * 1.2f;
                height = meter * 1.6f;

                hearts = 5;

                added_damage = 0;


                x = screenX - width + (relativeInWave * width * 1.5f);
                y = screenY - groundHeight - height;

                speed = 500 / meter; // pixels per iteration


                enemyBitmap = BitmapFactory.decodeResource(res, R.drawable.crusader_shielded);
                enemyBitmap = Bitmap.createScaledBitmap(enemyBitmap, (int) width, (int) height, false);

                xp = 70;
                initXP = xp;
                break;

            case "guard":
                width = meter;
                height = meter * 1.2f;

                hearts = 5;

                added_damage = 0;

                x = screenX - width + (relativeInWave * width * 1.5f);
                y = screenY - groundHeight - height;

                speed = 100 / meter; // pixels per iteration
                shootCounter = shootFrequency;

                enemyBitmap = BitmapFactory.decodeResource(res, R.drawable.guard);
                enemyBitmap = Bitmap.createScaledBitmap(enemyBitmap, (int) width, (int) height, false);

                guard_projectiles = new ArrayList<>();

                xp = 70;
                initXP = xp;
                break;
        }



        heartBitmap = BitmapFactory.decodeResource(res, R.drawable.heart);
        heartBitmap = Bitmap.createScaledBitmap(heartBitmap, (int) (width/2), (int) (width/2),false);

    }


    void displayHearts (Canvas canvas, Paint paint)
    {
        heartBitmap = Bitmap.createScaledBitmap(heartBitmap, groundHeight,  groundHeight,false);

        if (hearts <= 5)
            for (int i = 1; i <= hearts; i++)
                canvas.drawBitmap(heartBitmap, x +  (groundHeight * i), y + height, paint);

        else
        {
            canvas.drawText(hearts + "", x, y + height + groundHeight, paint);
            canvas.drawBitmap(heartBitmap, x + paint.measureText(hearts+""), y + height, paint);
        }
    }

    void XPWaveMultiplier (int wave)
    {
        if (initXP == xp)
            xp *= wave + 1; // only multiply once
    }

}

