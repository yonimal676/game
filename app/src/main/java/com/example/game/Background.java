package com.example.game;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Background
{
    Bitmap backgroundBitmap, devBackgroundBitmap;

    public Background(Resources res, int screenX, int screenY, byte metersInTheScreen)
    {
        backgroundBitmap = BitmapFactory.decodeResource(res, R.drawable.background);
        backgroundBitmap = Bitmap.createScaledBitmap(backgroundBitmap, screenX, screenY, false);

        devBackgroundBitmap = BitmapFactory.decodeResource(res, R.drawable.black);
        devBackgroundBitmap = Bitmap.createScaledBitmap(devBackgroundBitmap, screenX, screenY, false);


    }
}
