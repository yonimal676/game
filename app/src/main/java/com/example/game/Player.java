package com.example.game;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.ArrayList;

public class Player
{

    //shoot
    float aimX, aimY;
    short crosshairSize;  // width/height not needed because it's a square
    int iteration_of_throw;
    Bitmap crosshairBitmap;

    //player
    float x,y;
    float width, height;
    final float meter; // for graspable ratio.
    float midX, midY;
    Bitmap bobNormalBitmap;
    Bitmap bobThrowingBitmap;

    byte hearts;
    byte maxHearts;
    Bitmap heartBitmap;
    Bitmap emptyHeartBitmap;

    byte ammo;
    byte maxAmmo;
    int iterationForAmmoRegeneration;
    int AmmoRegenerationPace;
    Bitmap ammoBitmap;
    Bitmap emptyAmmoBitmap;
    byte damage;


    int xp;

    boolean hasMagnet;
    Bitmap magnetBitmap;

    boolean hasBleed;
    Bitmap bloodBitmap;

    boolean hasFreeze;
    Bitmap snowflakeBitmap;

    boolean hasImpatience;




    public Player (Resources res, int screenX, int screenY, int groundHeight, byte metersInTheScreen)
    {
        xp = 0;
        damage = 1;

        hasMagnet = false;
        hasBleed = false;
        hasFreeze = false;
        hasImpatience = false;

        meter = (float) screenX / metersInTheScreen;

        width = meter;
        height = meter * 1.2f; // discussion: screen ratios #21

        x = screenX / 20f;
        y = (short) (screenY - height - groundHeight);


        midX = x + width / 2;
        midY = y + height / 2;


        bobNormalBitmap = BitmapFactory.decodeResource(res, R.drawable.bob_normal);
        bobNormalBitmap = Bitmap.createScaledBitmap(bobNormalBitmap, (int) width, (int) height, false);

        bobThrowingBitmap = BitmapFactory.decodeResource(res, R.drawable.bob_throwing);
        bobThrowingBitmap = Bitmap.createScaledBitmap(bobThrowingBitmap, (int) width, (int) height, false);


        aimX = (float) ( midX + Math.cos(45) * height);
        aimY = (float) ( midY - Math.sin(45) * height);// so that the starting position would be similar to the first shot


        crosshairSize = (short) (screenX/60f);


        crosshairBitmap = BitmapFactory.decodeResource(res, R.drawable.crosshair);
        crosshairBitmap = Bitmap.createScaledBitmap(crosshairBitmap, crosshairSize, crosshairSize, false);

        magnetBitmap = BitmapFactory.decodeResource(res, R.drawable.magnet);
        magnetBitmap = Bitmap.createScaledBitmap(magnetBitmap, crosshairSize, crosshairSize, false);

        bloodBitmap = BitmapFactory.decodeResource(res, R.drawable.blood_drop);
        bloodBitmap = Bitmap.createScaledBitmap(bloodBitmap, crosshairSize, crosshairSize, false);

        snowflakeBitmap = BitmapFactory.decodeResource(res, R.drawable.snowflake);
        snowflakeBitmap = Bitmap.createScaledBitmap(snowflakeBitmap, crosshairSize, crosshairSize, false);


        maxHearts = 4;
        hearts = 4;

        heartBitmap = BitmapFactory.decodeResource(res, R.drawable.heart);
        heartBitmap = Bitmap.createScaledBitmap(heartBitmap, crosshairSize, crosshairSize,false);

        emptyHeartBitmap = BitmapFactory.decodeResource(res, R.drawable.empty_heart);
        emptyHeartBitmap = Bitmap.createScaledBitmap(emptyHeartBitmap, crosshairSize, crosshairSize,false);


        maxAmmo = 4;
        ammo = 4;
        AmmoRegenerationPace = 40;
        iterationForAmmoRegeneration = AmmoRegenerationPace;

        ammoBitmap = BitmapFactory.decodeResource(res, R.drawable.projectile);
        ammoBitmap = Bitmap.createScaledBitmap(ammoBitmap,  (int) (crosshairSize/1.3) , (int) (crosshairSize/1.3), false);

        emptyAmmoBitmap = BitmapFactory.decodeResource(res, R.drawable.empty_ammo);
        emptyAmmoBitmap = Bitmap.createScaledBitmap(emptyAmmoBitmap,  (int) (crosshairSize/1.3) , (int) (crosshairSize/1.3), false);



        iteration_of_throw = -1; // = "null"

    }


    void setCrosshairPosition (float x, float y) {
        this.aimX = x;
        this.aimY = y;
    }



    public void regenerateAmmo ()
    {
        if (ammo < maxAmmo) //needs filling
        {
            if (iterationForAmmoRegeneration == 0) {// regen now
                ammo++;
                iterationForAmmoRegeneration = AmmoRegenerationPace; // regen once at a time
            }
            else
                iterationForAmmoRegeneration--; // countdown till regen
        }

        else //filling unneeded
            iterationForAmmoRegeneration = AmmoRegenerationPace;
    }



}
