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
    Bitmap projectile1Bitmap;
    Bitmap projectile2Bitmap;
    Bitmap guardProjectileBitmap;

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


    float screenX, screenY, groundHeight;

    int type; // 1 = normal, 2 = guard



    /* TODO: don't remove the projectile even when needed so you can still see the path, instead make the bitmap null, and damage = 0 */


    public Projectile (Resources res, int screenX, int screenY, float aimX, float aimY, float groundHeight, byte metersInTheScreen, int type)
    {
        this.screenX = screenX;
        this.screenY = screenY;
        this.groundHeight = groundHeight;
        this.type = type;
        toRemove = false;


        meter = (float) screenX / metersInTheScreen; // TODO: remember that if you scale this up, the ball will NOT move in the same ratio!!

        width = (short) (0.35 * meter);
        height = (short) (0.35 * meter);


        projectileBitmap = BitmapFactory.decodeResource(res, R.drawable.projectile);
        projectileBitmap = Bitmap.createScaledBitmap(projectileBitmap, width, height, false);

        projectile1Bitmap = BitmapFactory.decodeResource(res, R.drawable.projectile2);
        projectile1Bitmap = Bitmap.createScaledBitmap(projectile1Bitmap, width, height, false);

        projectile2Bitmap = BitmapFactory.decodeResource(res, R.drawable.projectile3);
        projectile2Bitmap = Bitmap.createScaledBitmap(projectile2Bitmap, width, height, false);


        guardProjectileBitmap = BitmapFactory.decodeResource(res, R.drawable.guard_projectile);
        guardProjectileBitmap = Bitmap.createScaledBitmap(guardProjectileBitmap, width, height, false);

        GRAVITY =  9.8f * 6.3f * meter; // should be negative due to the earth's gravity pulling it downwards.
        v = 28 * 1.4f * meter; // also max pull | meters per second.
        time = 0;


        if (type == 1) {
            isThrown = false;

            x = aimX;
            y = aimY;

            dotArrayListX = new ArrayList<>();
            dotArrayListY = new ArrayList<>();
        }


        else if (type == 2)
        {
            isThrown = true;

            x = aimX;
            y = aimY;
        }



    }





    float findAngle(float Tx, float Ty, float playerX, float playerY) // Find Angle When Finger Is Touching Outside, T - Touch point || * returns a radian
    {return (float) (Math.atan2(playerY +  - Ty, playerX - Tx ));}
/*
    The atan() and atan2() functions calculate the arc-tangent of x and y/x, respectively.
    The atan() function returns a value in the range -π/2 to π/2 radians.
    The atan2() function returns a value in the range -π to π radians.
 */






    public void didHit (int groundHeight, Enemy enemy, byte damage, Player bob)
    {

        if (type == 1) {
            if (enemy != null)
                if (x + width >= enemy.x && x <= enemy.x + enemy.width && y + height >= enemy.y && y <= enemy.y + enemy.height) {
                    if (enemy.type.equals("crusader")) {
                        if (!enemy.shielded) {
                            enemy.hearts -= damage;
                            enemy.shielded = true;
                        }
                    }
                    else
                        enemy.hearts -= damage;

                    toRemove = true;
                    return;
                }
        }

        else if (type == 2)
            if (x + width >= bob.x && x <= bob.x + bob.width && y + height >= bob.y && y <= bob.y + bob.height) {
                bob.hearts --;
                toRemove = true;
            }

        // hit ground
        if ( y + height >= screenY - groundHeight)
            toRemove = true;
    }

    public void physics ()
    {
        vy = v0y + GRAVITY * time;

        x = initialX + vx * time; // x0 + Vx * t

        y = initialY + vy * time - GRAVITY * time * time / 2; // y0 + Vy * t - g * t² / 2
    }


}
