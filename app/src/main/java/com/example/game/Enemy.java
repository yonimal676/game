package com.example.game;


import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.Random;

public class Enemy
{

    float x, y;
    float width, height;
    int hearts;
    int speed; // number of pixels per iteration
    Bitmap enemyBitmap;
    Bitmap heartBitmap;
    int[] randomRegularBitmap = {R.drawable.enemy1, R.drawable.enemy2, R.drawable.enemy3, R.drawable.enemy4};


    final float meter; // for graspable ratio.
    int groundHeight;




    public Enemy(Resources res, int screenX, int screenY, int groundHeight, byte metersInTheScreen, float relativeInWave, String type, float deadX)
    {

        meter = (float) screenX / metersInTheScreen; // TODO: remember that if you scale this up, the ball will NOT move in the same ratio!!
        this.groundHeight = groundHeight;

        if (type.equals("regular")) {
            width = meter;
            height = meter * 1.2f;

            hearts = 1;
            x = screenX - width + (relativeInWave * width * 1.5f);
            y = screenY - groundHeight - height;
            speed = 5; // pixels per iteration


            Random random = new Random();
            int randomIndex = random.nextInt(randomRegularBitmap.length);

            int randomImageResource = randomRegularBitmap[randomIndex];


            enemyBitmap = BitmapFactory.decodeResource(res, randomImageResource);
            enemyBitmap = Bitmap.createScaledBitmap(enemyBitmap, (int) width, (int) height, false);
        }

        if (type.equals("ghost")) {

            width = meter;
            height = meter * 1.2f;

            hearts = 2;

            x = screenX - width + (relativeInWave * width * 1.5f);
            y = screenY - groundHeight - height;
            speed = 8; // pixels per iteration


            enemyBitmap = BitmapFactory.decodeResource(res, R.drawable.ghost);
            enemyBitmap = Bitmap.createScaledBitmap(enemyBitmap, (int) width, (int) height, false);
        }


        if (type.equals("giant"))
        {

            width = meter * 1.75f;
            height = meter * 2f;

            hearts = 5;

            x = screenX - width + (relativeInWave * width * 1.5f);
            y = screenY - groundHeight - height;
            speed = 3; // pixels per iteration


            enemyBitmap = BitmapFactory.decodeResource(res, R.drawable.giant);
            enemyBitmap = Bitmap.createScaledBitmap(enemyBitmap, (int) width, (int) height, false);
        }



        if (type.equals("skeleton")) //not an enemy but could be here
        {

            width = meter * 0.6f;
            height = meter * 0.9f;

            hearts = 1;

            x = deadX;
            y = screenY - groundHeight - height;

            speed = 2; // pixels per iteration


            enemyBitmap = BitmapFactory.decodeResource(res, R.drawable.skeleton);
            enemyBitmap = Bitmap.createScaledBitmap(enemyBitmap, (int) width, (int) height, false);
        }





        heartBitmap = BitmapFactory.decodeResource(res, R.drawable.heart);
        heartBitmap = Bitmap.createScaledBitmap(heartBitmap, (int) (width/2), (int) (width/2),false);

    }


    void displayHearts (Canvas canvas, Paint paint)
    {
        for (int i = 1; i <= hearts; i++)
        {
            heartBitmap = Bitmap.createScaledBitmap(heartBitmap, groundHeight,  groundHeight,false);

            canvas.drawBitmap(heartBitmap, x +  (groundHeight * i), y + height, paint);
        }
    }



    void jump (int iteration)
    {

    }

}

