package com.example.game;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Player
{
    float x,y;
    float width, height;
    int iteration_of_throw;

    Bitmap bobNormalBitmap;
    Bitmap bobThrowingBitmap;

    public Player (Resources res, int screenX, int screenY, int groundHeight)
    {
        width = screenX / 121f * 5; // 107 x 121 are the proportions of the character
        height = screenX / 107f * 5; // discussion: screen ratios #21

        x = screenX / 20f;
        y = (short) (screenY - height - groundHeight);


        bobNormalBitmap = BitmapFactory.decodeResource(res, R.drawable.bob_normal);
        bobNormalBitmap = Bitmap.createScaledBitmap(bobNormalBitmap, (int) width, (int) height, false);




        bobThrowingBitmap = BitmapFactory.decodeResource(res, R.drawable.bob_throwing);
        bobThrowingBitmap = Bitmap.createScaledBitmap(bobThrowingBitmap, (int) width, (int) height, false);



    }





}
