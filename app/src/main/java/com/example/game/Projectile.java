package com.example.game;

import java.util.ArrayList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Projectile
{


    //projectile
    float initialX, initialY;  // acts as the (0,0) point relative to the ball.
    float x, y;
    short width, height;
    Bitmap projectileBitmap;
    Bitmap transparentBitmap;

    //physics
    float angle;
    float v ,vx, vy, v0y; // velocity, velocityX, velocityY, initialVelocityY
    float time;
    float GRAVITY;
    boolean isThrown;
    final float meter; // for graspable ratio.


    ArrayList<Float> dotArrayListX;
    ArrayList<Float> dotArrayListY;

    boolean toRemove; // for removal from the array list in GameView
    byte damage;


    float screenX, screenY, groundHeight;


    /* TODO: don't remove the projectile even when needed so you can still see the path, instead make the bitmap null, and damage = 0 */


    public Projectile (Resources res, int screenX, int screenY, float aimX, float aimY, float groundHeight, byte metersInTheScreen)
    {
        this.screenX = screenX;
        this.screenY = screenY;
        this.groundHeight = groundHeight;
        toRemove = false;


        meter = (float) screenX / metersInTheScreen; // TODO: remember that if you scale this up, the ball will NOT move in the same ratio!!

        isThrown = false;

        x = aimX ;
        y = aimY;


        width = (short) (0.35 * meter);
        height = (short) (0.35 * meter);


        // Draw the ball:
        projectileBitmap = BitmapFactory.decodeResource(res, R.drawable.projectile);
        projectileBitmap = Bitmap.createScaledBitmap(projectileBitmap, width, height, false);


        // to nullify the original bitmap
        transparentBitmap = BitmapFactory.decodeResource(res, R.drawable.projectile);
        transparentBitmap = Bitmap.createScaledBitmap(transparentBitmap, 1, 1, false);


        // Physics-related stuff: todo -> NOT NEGATIVE GRAVITY,  also:   ball.vy = ball.v0y + ball.GRAVITY * ball.time;
        GRAVITY =  9.8f * 6.3f * meter; // should be negative due to the earth's gravity pulling it downwards.
        v = 25 * 1.4f * meter; // also max pull | meters per second.
        time = 0;



        dotArrayListX = new ArrayList<>();
        dotArrayListY = new ArrayList<>();



        damage = 1;

    }





    float findAngle(float Tx, float Ty, float playerX, float playerY) // Find Angle When Finger Is Touching Outside, T - Touch point || * returns a radian
    {return (float) (Math.atan2(playerY +  - Ty, playerX - Tx ));}
/*
    The atan() and atan2() functions calculate the arc-tangent of x and y/x, respectively.
    The atan() function returns a value in the range -π/2 to π/2 radians.
    The atan2() function returns a value in the range -π to π radians.
 */




    public void didHit (int groundHeight, Enemy enemy)
    {

        // hit enemy
        if (enemy != null)
            if (x + width >= enemy.x && x <= enemy.x + enemy.width && y + height >= enemy.y && y <= enemy.y + enemy.height)
            {
                if (enemy.type.equals("crusader"))
                {
                    if ( ! enemy.shielded) {
                        enemy.hearts -= damage;
                        enemy.shielded = true;
                    }
                }

                else
                    enemy.hearts -= damage;

                toRemove = true;
                return;
            }


        // hit ground
        if ( y + height >= screenY - groundHeight)
            toRemove = true;


    }

    public void physics () // issue: physics #25
    {

        vy = v0y + GRAVITY * time;

        x = initialX + vx * time; // x0 + Vx * t
        y = initialY + vy * time - GRAVITY * time * time / 2; // y0 + Vy * t - g * t² / 2
    }


}
