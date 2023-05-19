package com.example.game;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Player
{



    //crosshair (aim)
    float aimX, aimY;
    short crosshairSize;  // width/height not needed because it's a square
    Bitmap crosshairBitmap;




    float x,y;
    float width, height;
    float midX, midY;
    int iteration_of_throw;

    Bitmap bobNormalBitmap;
    Bitmap bobThrowingBitmap;

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





    }


    void setCrosshairPosition (float x, float y) {
        this.aimX = x;
        this.aimY = y;
    }




}
