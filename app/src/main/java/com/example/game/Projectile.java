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
    float prevX, prevY;
    short width, height;
    Bitmap projectileBitmap;
    Bitmap transparentBitmap;

    //physics
    float angle;
    float v ,vx, vy, v0y; // velocity, velocityX, velocityY, initialVelocityY
    float time;
    float GRAVITY;
    boolean isThrown;
    final float ratioMtoPX; // discussion: Pixels to centimeters #19 || x pixels to meters.


    ArrayList<Float> dotArrayListX;
    ArrayList<Float> dotArrayListY;

    byte damage;


    float screenX, screenY, groundHeight;


    /* TODO: don't remove the projectile even when needed so you can still see the path, instead make the bitmap null, and damage = 0 */


    public Projectile (Resources res, float screenX, float screenY, float aimX, float aimY, float groundHeight)
    {
        this.screenX = screenX;
        this.screenY = screenY;
        this.groundHeight = groundHeight;

        ratioMtoPX = screenX / 14; // TODO: remember that if you scale this up, the ball will NOT move in the same ratio!!

        isThrown = false;

        x = aimX ;
        y = aimY;


        prevX = x;
        prevY = y;

        width = (short) (0.2 * ratioMtoPX);
        height = (short) (0.2 * ratioMtoPX);


        // Draw the ball:
        projectileBitmap = BitmapFactory.decodeResource(res, R.drawable.projectile);
        projectileBitmap = Bitmap.createScaledBitmap(projectileBitmap, width, height, false);


        // to nullify the original bitmap
        transparentBitmap = BitmapFactory.decodeResource(res, R.drawable.projectile);
        transparentBitmap = Bitmap.createScaledBitmap(transparentBitmap, 1, 1, false);


        // Physics-related stuff: todo -> NOT NEGATIVE GRAVITY,  also:   ball.vy = ball.v0y + ball.GRAVITY * ball.time;
        GRAVITY =  9.8f * 6.3f * ratioMtoPX; // should be negative due to the earth's gravity pulling it downwards.
        v = 21 * 1.4f * ratioMtoPX; // also max pull | meters per second.
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




}
