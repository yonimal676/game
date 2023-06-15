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
    String type;
    int hearts;
    int xpOfEnemy, xp;


    Bitmap enemyBitmap;
    Bitmap heartBitmap;
    int[] randomRegularBitmap = {R.drawable.enemy1, R.drawable.enemy2, R.drawable.enemy3, R.drawable.enemy4};


    int screenY;
    int groundHeight;




    int waitForJump; // 👇
    int jumpCounter; // Both are responsible for timing the jumps
    float jumpLength;  // number of pixels per iteration
    float jumpHeight;  // number of pixels per iteration
    int jumpIterations; // number of iterations per jump



    boolean shielded;
    int shield_counter;
    int added_damage;
    int shootCounter;
    ArrayList<Projectile> guard_projectiles;

    boolean toShowShot;

    int shootFrequency;

    boolean isBleeding;
    int bleed_frequency; // for each enemy who is effected by the blood effect - set a counter


    public Enemy(Resources res, int screenX, int screenY, int groundHeight, float relativeInWave, String type, float deadX, float bobX)
    {

        this.type = type;
        this.groundHeight = groundHeight;
        this.screenY = screenY;


        shield_counter = 100;
        shielded = false;
        toShowShot = false;
        shootFrequency = 150;
        bleed_frequency = 160;
        jumpLength = screenX / 100f;
        jumpHeight = jumpLength * 0.4f; // creates an angle of the jump (  tan^-1 (0.4/1) = 21.801°  )
        jumpIterations = 10;

        switch (type)
        {
            case "regular":
                width = screenX / 25f;
                height = width * 1.35f;

                hearts = 1;
                x = screenX - width + (relativeInWave * width * 1.5f);
                y = screenY - groundHeight - height;


                jumpCounter = 20;
                waitForJump = jumpCounter;

                added_damage = 0;


                Random random = new Random();
                int randomIndex = random.nextInt(randomRegularBitmap.length);

                int randomImageResource = randomRegularBitmap[randomIndex];


                enemyBitmap = BitmapFactory.decodeResource(res, randomImageResource);
                enemyBitmap = Bitmap.createScaledBitmap(enemyBitmap, (int) width, (int) height, false);

                xp = 14;
                xpOfEnemy = xp;
                break;


            case "ghost":

                width = screenX / 25f;
                height = width * 1.4f;

                hearts = 2;
                added_damage = 0;

                x = screenX - width + (relativeInWave * width * 1.5f);
                y = screenY - groundHeight - height;


                enemyBitmap = BitmapFactory.decodeResource(res, R.drawable.ghost);
                enemyBitmap = Bitmap.createScaledBitmap(enemyBitmap, (int) width, (int) height, false);

                xp = 20;
                xpOfEnemy = xp;
                break;


            case "giant":
                width = screenX / 12f;
                height = width * 1.2f;
                hearts = 10;

                added_damage = 0;


                x = screenX - width + (relativeInWave * width * 1.5f);
                y = screenY - groundHeight - height;


                jumpCounter = 35;
                waitForJump = jumpCounter;


                enemyBitmap = BitmapFactory.decodeResource(res, R.drawable.giant);
                enemyBitmap = Bitmap.createScaledBitmap(enemyBitmap, (int) width, (int) height, false);

                xp = 20;
                xpOfEnemy = xp;
                break;


            case "skeleton": //not an enemy but could be here

                width = screenX / 30f;
                height = width * 1.45f;

                hearts = 1;

                x = deadX;
                y = screenY - groundHeight - height;

                jumpCounter = 10;
                waitForJump = jumpCounter;


                enemyBitmap = BitmapFactory.decodeResource(res, R.drawable.skeleton);
                enemyBitmap = Bitmap.createScaledBitmap(enemyBitmap, (int) width, (int) height, false);

                xp = 0;
                break;


            case "crusader":

                width = screenX / 22f;
                height = width * 1.65f;

                hearts = 5;

                added_damage = 0;


                x = screenX - width + (relativeInWave * width * 1.5f);
                y = screenY - groundHeight - height;

                jumpCounter = 30;
                waitForJump = jumpCounter;


                enemyBitmap = BitmapFactory.decodeResource(res, R.drawable.crusader_shielded);
                enemyBitmap = Bitmap.createScaledBitmap(enemyBitmap, (int) width, (int) height, false);

                xp = 70;
                xpOfEnemy = xp;
                break;

            case "guard":
                width = screenX / 22f;
                height = width * 1.8f;

                hearts = 3;

                added_damage = 0;

                x = screenX - width + (relativeInWave * width * 1.5f);
                y = screenY - groundHeight - height;

                jumpCounter = 150;
                waitForJump = jumpCounter;



                shootCounter = shootFrequency;

                enemyBitmap = BitmapFactory.decodeResource(res, R.drawable.guard);
                enemyBitmap = Bitmap.createScaledBitmap(enemyBitmap, (int) width, (int) height, false);

                guard_projectiles = new ArrayList<>();

                xp = 70;
                xpOfEnemy = xp;
                break;






            case "seated king":
                width = screenX / 15f;
                height = width;

                hearts = 50;

                added_damage = 0;

                x = screenX - width + (relativeInWave * width * 1.5f);
                y = screenY - groundHeight - height;

                jumpCounter = 150;
                waitForJump = jumpCounter;




                enemyBitmap = BitmapFactory.decodeResource(res, R.drawable.seated_king);
                enemyBitmap = Bitmap.createScaledBitmap(enemyBitmap, (int) width, (int) height, false);


                xp = 10000000;
                xpOfEnemy = xp;
                break;



            case "king":
                width = screenX / 25f;
                height = width * 1.6f;

                hearts = 50;

                added_damage = 0;

                x = screenX - width + (relativeInWave * width * 1.5f);
                y = screenY - groundHeight - height;

                jumpCounter = 150;
                waitForJump = jumpCounter;



                shootCounter = shootFrequency;

                enemyBitmap = BitmapFactory.decodeResource(res, R.drawable.king);
                enemyBitmap = Bitmap.createScaledBitmap(enemyBitmap, (int) width, (int) height, false);


                xp = 10000000;
                xpOfEnemy = xp;
                break;








        }



        heartBitmap = BitmapFactory.decodeResource(res, R.drawable.heart);
        heartBitmap = Bitmap.createScaledBitmap(heartBitmap, groundHeight, groundHeight,false);

    }


    void displayHearts (Canvas canvas, Paint paint)
    {
        heartBitmap = Bitmap.createScaledBitmap(heartBitmap, groundHeight,  groundHeight,false);

        if (hearts <= 5)
            for (int i = 1; i <= hearts; i++)
                canvas.drawBitmap(heartBitmap, x +  (groundHeight * i), screenY - groundHeight, paint);

        else
        {
            canvas.drawText(hearts + "", x, screenY - groundHeight, paint);
            canvas.drawBitmap(heartBitmap, x + paint.measureText(hearts+""), screenY - groundHeight, paint);
        }
    }


    void XPWaveMultiplier (int wave)
    {
        if (xpOfEnemy == xp)
            xp *= wave + 1; // only multiply once
    }

}

