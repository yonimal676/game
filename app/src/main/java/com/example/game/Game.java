package com.example.game;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.function.BiConsumer;

public class Game
{



    ArrayList<ArrayList<Enemy>> waves;
    byte currentWave;
    boolean isInWave;
    boolean didContinue; // did exit upgrade screen?


    // upgrades
    Paint circle_paint;
    Paint text_paint;
    int circleRadius;
    int textSize;
    float scaledSizeInPixels;
    int whenOutOfscreen; // upgrade bubbles
    ArrayList<ArrayList<String>> upgrades;
    String explainUpgrade;
    float cx, cy; // circle x,y
    float Ccx, Ccy; // Continue circle x,y
    ArrayList<Float> cx_arr;
    ArrayList<Float> cy_arr;

    ArrayList<ArrayList<Integer>> upgrades_costs;


    ArrayList<Float> deadX; // save x values of the killed enemies to later resurrect.
    ArrayList<Enemy> skeletons;
    Bitmap skullBitmap;
    boolean didResurrect;



    public Game (Resources res, int screenX, int screenY, int groundHeight, byte metersInTheScreen)
    {
        didContinue = false;
        isInWave = true;
        currentWave = 0;
        explainUpgrade = "";
        didResurrect = false;


        circle_paint = new Paint();
        circle_paint.setColor(Color.argb(150, 78, 166, 135));


        text_paint = new Paint();
        text_paint.setColor(Color.WHITE);


        textSize = 15;

        scaledSizeInPixels = textSize * res.getDisplayMetrics().scaledDensity;
        text_paint.setTextSize(scaledSizeInPixels);


        circleRadius = textSize * 10; // to fit characters (no word is longer than 10 characters [5 = radius])
        whenOutOfscreen = 0;


        skullBitmap = BitmapFactory.decodeResource(res, R.drawable.skull);
        skullBitmap = Bitmap.createScaledBitmap(skullBitmap, screenX/40 , screenY/40, false);



        waves = new ArrayList<>();
        deadX = new ArrayList<>();
        skeletons = new ArrayList<>();
        upgrades = new ArrayList<>();
        cx_arr = new ArrayList<>();
        cy_arr = new ArrayList<>();
        upgrades_costs = new ArrayList<>();



        for (int i = 1; i <= 10; i++)
            waves.add(new ArrayList<>());


        waves.get(0).add(new Enemy(res, screenX, screenY, groundHeight, metersInTheScreen, 1, "regular",0));
        waves.get(0).add(new Enemy(res, screenX, screenY, groundHeight, metersInTheScreen, 2, "regular",0));
        waves.get(0).add(new Enemy(res, screenX, screenY, groundHeight, metersInTheScreen, 3, "regular",0));



        waves.get(1).add(new Enemy(res, screenX, screenY, groundHeight, metersInTheScreen, 1, "regular",0));
        waves.get(1).add(new Enemy(res, screenX, screenY, groundHeight, metersInTheScreen, 2, "regular",0));
        waves.get(1).add(new Enemy(res, screenX, screenY, groundHeight, metersInTheScreen, 3, "regular",0));
        waves.get(1).add(new Enemy(res, screenX, screenY, groundHeight, metersInTheScreen, 4, "ghost",0));
        waves.get(1).add(new Enemy(res, screenX, screenY, groundHeight, metersInTheScreen, 5, "ghost",0));



        waves.get(2).add(new Enemy(res, screenX, screenY, groundHeight, metersInTheScreen, 1, "giant",0));
        waves.get(2).add(new Enemy(res, screenX, screenY, groundHeight, metersInTheScreen, 2, "giant",0));


        waves.get(3).add(new Enemy(res, screenX, screenY, groundHeight, metersInTheScreen, 1, "regular",0));
        waves.get(3).add(new Enemy(res, screenX, screenY, groundHeight, metersInTheScreen, 1.3f, "regular",0));
        waves.get(3).add(new Enemy(res, screenX, screenY, groundHeight, metersInTheScreen, 1.5f, "regular",0));
        waves.get(3).add(new Enemy(res, screenX, screenY, groundHeight, metersInTheScreen, 2.2f, "regular",0));
        waves.get(3).add(new Enemy(res, screenX, screenY, groundHeight, metersInTheScreen, 2.5f, "regular",0));
        waves.get(3).add(new Enemy(res, screenX, screenY, groundHeight, metersInTheScreen, 2.8f, "regular",0));
        waves.get(3).add(new Enemy(res, screenX, screenY, groundHeight, metersInTheScreen, 4, "regular",0));
        waves.get(3).add(new Enemy(res, screenX, screenY, groundHeight, metersInTheScreen, 3.4f, "regular",0));
        waves.get(3).add(new Enemy(res, screenX, screenY, groundHeight, metersInTheScreen, 4.2f, "regular",0));
        waves.get(3).add(new Enemy(res, screenX, screenY, groundHeight, metersInTheScreen, 4.5f, "regular",0));
        waves.get(3).add(new Enemy(res, screenX, screenY, groundHeight, metersInTheScreen, 4.8f, "regular",0));
        waves.get(3).add(new Enemy(res, screenX, screenY, groundHeight, metersInTheScreen, 5, "regular",0));
        waves.get(3).add(new Enemy(res, screenX, screenY, groundHeight, metersInTheScreen, 5.5f, "regular",0));
        waves.get(3).add(new Enemy(res, screenX, screenY, groundHeight, metersInTheScreen, 5.8f, "regular",0));
        waves.get(3).add(new Enemy(res, screenX, screenY, groundHeight, metersInTheScreen, 6.2f, "regular",0));
        waves.get(3).add(new Enemy(res, screenX, screenY, groundHeight, metersInTheScreen, 6.5f, "regular",0));
        waves.get(3).add(new Enemy(res, screenX, screenY, groundHeight, metersInTheScreen, 7, "regular",0));


        waves.get(4).add(new Enemy(res, screenX, screenY, groundHeight, metersInTheScreen, 1, "giant",0));
        waves.get(4).add(new Enemy(res, screenX, screenY, groundHeight, metersInTheScreen, 2, "giant",0));
        waves.get(4).add(new Enemy(res, screenX, screenY, groundHeight, metersInTheScreen, 3, "giant",0));
        waves.get(4).add(new Enemy(res, screenX, screenY, groundHeight, metersInTheScreen, 4, "giant",0));



        waves.get(5).add(new Enemy(res, screenX, screenY, groundHeight, metersInTheScreen, 1, "crusader",0));


        waves.get(6).add(new Enemy(res, screenX, screenY, groundHeight, metersInTheScreen, 1, "regular",0));


        waves.get(7).add(new Enemy(res, screenX, screenY, groundHeight, metersInTheScreen, 1, "regular",0));

        waves.get(8).add(new Enemy(res, screenX, screenY, groundHeight, metersInTheScreen, 1, "regular",0));


        waves.get(9).add(new Enemy(res, screenX, screenY, groundHeight, metersInTheScreen, 1, "regular",0));







        for (int i = 1; i <= 10; i++)
            upgrades.add(new ArrayList<>());

        for (int i = 1; i <= 10; i++)
            upgrades_costs.add(new ArrayList<>());


        upgrades_costs.get(0).add(25);
        upgrades_costs.get(0).add(25);
        upgrades_costs.get(0).add(25);

        /*upgrades_costs.get(1).add(25);
        upgrades_costs.get(1).add(25);
        upgrades_costs.get(1).add(25);
        upgrades_costs.get(1).add(25);
        upgrades_costs.get(1).add(25);
*/


        upgrades.get(0).add("Health");
        upgrades.get(0).add("Heal");
        upgrades.get(0).add("Recharge");






        upgrades.get(1).add("Damage");
        upgrades.get(1).add("Health");
        upgrades.get(1).add("Health");



        upgrades.get(2).add("Recharge");
        upgrades.get(3).add("Recharge");
        upgrades.get(4).add("Recharge");
        upgrades.get(5).add("Recharge");
        upgrades.get(6).add("Recharge");
        upgrades.get(7).add("Recharge");
        upgrades.get(8).add("Recharge");
        upgrades.get(9).add("Recharge");








    }











}