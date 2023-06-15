package com.example.game;

import java.util.ArrayList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class Projectile
{


    //projectile
    float initialX, initialY;  // acts as the (0,0) point relative to the ball.
    float x, y;
    short width, height;
    Bitmap projectileBitmap, projectile1Bitmap, projectile2Bitmap;
    Bitmap pBloodBitmap, p1BloodBitmap, p2BloodBitmap;

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

        pBloodBitmap = BitmapFactory.decodeResource(res, R.drawable.p1b);
        pBloodBitmap = Bitmap.createScaledBitmap(pBloodBitmap, width, height, false);

        p1BloodBitmap = BitmapFactory.decodeResource(res, R.drawable.p2b);
        p1BloodBitmap = Bitmap.createScaledBitmap(p1BloodBitmap, width, height, false);

        p2BloodBitmap = BitmapFactory.decodeResource(res, R.drawable.p3b);
        p2BloodBitmap = Bitmap.createScaledBitmap(p2BloodBitmap, width, height, false);


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
                if (((x + width >= enemy.x && x + width <= enemy.x) || ((x <= enemy.x + enemy.width && x >= enemy.x)) && y + height >= enemy.y))
                {
                    if (bob.hasBleed)
                        enemy.hasBleed = true;

                    if (enemy.type.equals("crusader")) {
                        if (!enemy.shielded) {
                            enemy.hearts -= damage;
                            enemy.shielded = true;
                        }
                    }
                    else
                        enemy.hearts -= damage;

                    toRemove = true;
                }
        }

        else if (type == 2)
            if (x <= bob.x + bob.width && x >= bob.x && y + height >= bob.y) {
                bob.hearts --;
                toRemove = true;
            }

        // hit ground
        if ( y + height >= screenY - groundHeight)
            toRemove = true;
    }



    public void physics(boolean magnetToEnemy, float enemyX, float enemyY)
    {
        vy = v0y + GRAVITY * time;

        if (magnetToEnemy && distance(x, y, enemyX, enemyY) < screenX / 4) {
            float directionX = enemyX - x;
            float directionY = enemyY - y;
            float distanceSquared = distance(x, y, enemyX, enemyY) * distance(x, y, enemyX, enemyY);

            float forceMagnitude = GRAVITY * 1000 / distanceSquared;

            float ax = forceMagnitude * directionX / distance(x, y, enemyX, enemyY);
            float ay = forceMagnitude * directionY / distance(x, y, enemyX, enemyY);

            // Apply maximum speed limit
            float maxSpeed = v;
            float currentSpeed = (float) Math.sqrt(vx * vx + vy * vy);

            if (currentSpeed > maxSpeed) {
                float scale = maxSpeed / currentSpeed;
                vx *= scale;
                vy *= scale;
            }

            // Apply dampening effect when close
            if (distance(x, y, enemyX, enemyY) < screenX/4) {
                float dampeningFactor = 0.7f;
                ax *= dampeningFactor;
                ay *= dampeningFactor;

                // Update the velocity components instead of teleporting the projectile
                vx += ax * time;
                vy += ay * time;
            } else {
                // If not close, update the velocity components as usual
                vx += ax * time;
                vy += ay * time;
            }
        }

        x = initialX + vx * time; // x0 + Vx * t
        y = initialY + vy * time - GRAVITY * time * time / 2; // y0 + Vy * t - g * t² /
    }



    float distance (float x1, float y1, float x2, float y2)
    { return (float) Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));}



}
