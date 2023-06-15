package com.example.game;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.Random;

public class background
{
    int x,y;
    int groundHeight;
    int screenX, screenY;

    Bitmap backgroundBitmap;
    Bitmap groundBitmap;

    ArrayList<Float> particleX,  particleY;
    Paint particle_paint;


    public background(Resources res,int screenX, int screenY)
    {
        this.screenX = screenX;
        this.screenY = screenY;

        groundHeight = (short) (0.045 * screenY); // discussion: screen ratios #21

        x = 0;
        y = (short) (screenY - groundHeight);

        backgroundBitmap = BitmapFactory.decodeResource(res, R.drawable.background);
        backgroundBitmap = Bitmap.createScaledBitmap(backgroundBitmap, screenX, screenY - groundHeight, false);


        groundBitmap = BitmapFactory.decodeResource(res, R.drawable.ground);
        groundBitmap = Bitmap.createScaledBitmap(groundBitmap, screenX, groundHeight, false);


        particle_paint = new Paint();
        particle_paint.setColor(Color.argb(75, 153, 154, 181));

        particleX = new ArrayList<>();
        particleY = new ArrayList<>();

    } //(⌐■_■)✧


    void drawParticles(Canvas canvas)
    {
        int particleSize = 2; // Size of each particle
        int numOfParticles = 60; // Number of particles to draw

        Random random = new Random();

        for (int i = 0; i < numOfParticles; i++) {
            if (particleX.size() <= i) {
                particleX.add((float) random.nextInt(screenX));
                particleY.add((float) random.nextInt(screenY));
            }



                float vx = (random.nextFloat() - 0.5f) * 2;
                float vy = (random.nextFloat() - 0.5f) * 2;

                float newParticleX = particleX.get(i) + vx;
                float newParticleY = particleY.get(i) + vy;

                // Check if the particle is out of bounds and adjust its position
                if (newParticleX < 0 || newParticleX > screenX || newParticleY < 0 || newParticleY > screenY) {
                    particleX.set(i, (float) random.nextInt(screenX));
                    particleY.set(i, (float) random.nextInt(screenY));
                }
                else {
                    particleX.set(i, newParticleX);
                    particleY.set(i, newParticleY);
                }


                canvas.drawCircle(particleX.get(i), particleY.get(i), particleSize, particle_paint);
            }
        }



}

