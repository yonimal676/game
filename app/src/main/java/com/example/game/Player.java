package com.example.game;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

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
    float midX, midY;
    byte hearts;
    byte maxHearts;
    byte ammo;
    byte maxAmmo;
    int iterationForAmmoRegeneration;
    int AmmoRegenerationPace;
    Bitmap bobNormalBitmap;
    Bitmap bobThrowingBitmap;
    Bitmap heartBitmap;
    Bitmap emptyHeartBitmap;
    Bitmap ammoBitmap;
    Bitmap emptyAmmoBitmap;





    public Player (Resources res, int screenX, int screenY, int groundHeight)
    {
        width = screenX / 121f * 5; // 107 x 121 are the proportions of the character
        height = screenX / 107f * 5; // discussion: screen ratios #21

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

        maxHearts = 3;
        hearts = 3;

        heartBitmap = BitmapFactory.decodeResource(res, R.drawable.heart);
        heartBitmap = Bitmap.createScaledBitmap(heartBitmap, crosshairSize, crosshairSize,false);

        emptyHeartBitmap = BitmapFactory.decodeResource(res, R.drawable.empty_heart);
        emptyHeartBitmap = Bitmap.createScaledBitmap(emptyHeartBitmap, crosshairSize, crosshairSize,false);


        maxAmmo = 3;
        ammo = 3;
        AmmoRegenerationPace = 50;
        iterationForAmmoRegeneration = AmmoRegenerationPace;

        ammoBitmap = BitmapFactory.decodeResource(res, R.drawable.projectile);
        ammoBitmap = Bitmap.createScaledBitmap(ammoBitmap,  (int) (crosshairSize/1.3) , (int) (crosshairSize/1.3), false);

        emptyAmmoBitmap = BitmapFactory.decodeResource(res, R.drawable.empty_ammo);
        emptyAmmoBitmap = Bitmap.createScaledBitmap(emptyAmmoBitmap,  (int) (crosshairSize/1.3) , (int) (crosshairSize/1.3), false);




    }


    void setCrosshairPosition (float x, float y) {
        this.aimX = x;
        this.aimY = y;
    }




}
