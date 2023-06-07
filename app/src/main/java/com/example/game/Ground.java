package com.example.game;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Ground
{
    int x,y;
    int width, height;

    final float meter; // for graspable ratio.

    Bitmap groundBitmap;

    Bitmap backgroundBitmap;


    public Ground(Resources res, int screenX, int screenY, byte metersInTheScreen)
    {

        meter = (float) screenX / metersInTheScreen;

        width = (short) screenX;
        height = (short) (0.4 * meter); // discussion: screen ratios #21

        x = 0;
        y = (short) (screenY - height);


        groundBitmap = BitmapFactory.decodeResource(res, R.drawable.ground);
        groundBitmap = Bitmap.createScaledBitmap(groundBitmap, width, height, false);

        backgroundBitmap = BitmapFactory.decodeResource(res, R.drawable.background);
        backgroundBitmap = Bitmap.createScaledBitmap(backgroundBitmap, screenX, screenY, false);

    }//(⌐■_■)✧

}
