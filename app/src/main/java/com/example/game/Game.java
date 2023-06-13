package com.example.game;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Game
{

    ArrayList<ArrayList<Enemy>> waves;
    byte currentWave;
    boolean isInWave;
    boolean didContinue; // did exit upgrade screen?


    // upgrades
    Paint circle_paint;
    Paint text_paint;
    Paint res_paint;

    int circleRadius;
    int textSize;
    float scaledSizeInPixels;
    int whenOutOfScreen; // upgrade bubbles
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

    int canResurrect;
    ArrayList<String> upgradesBought;
    float bobX, bobY;



    public Game (Resources res, int screenX, int screenY, int groundHeight, byte metersInTheScreen, float bobX, float bobY)
    {
        this.bobX = bobX;
        this.bobY = bobY;

        isInWave = true;
        currentWave = 0;
        explainUpgrade = "";
        didResurrect = false;
        canResurrect = 0; // can resurrect at the beginning
        didContinue = false;

        circle_paint = new Paint();
        circle_paint.setColor(Color.argb(100, 70, 160, 110)); // upgrades

        res_paint = new Paint();
        res_paint.setColor(Color.argb(200, 80, 210, 120)); // resurrect button


        text_paint = new Paint();
        text_paint.setColor(Color.WHITE);


        circleRadius = screenX/20 ; // to fit characters (no word is longer than 10 characters [5 = radius])
        whenOutOfScreen = 0;



        skullBitmap = BitmapFactory.decodeResource(res, R.drawable.skull);
        skullBitmap = Bitmap.createScaledBitmap(skullBitmap, screenX/40 , screenY/40, false);



        waves = new ArrayList<>();
        deadX = new ArrayList<>();
        skeletons = new ArrayList<>();
        upgrades = new ArrayList<>();
        cx_arr = new ArrayList<>();
        cy_arr = new ArrayList<>();
        upgrades_costs = new ArrayList<>();
        upgradesBought = new ArrayList<>();


        for (int i = 1; i <= 10; i++)
            waves.add(new ArrayList<>());



        waves.get(0).add(new Enemy(res, screenX, screenY, groundHeight, metersInTheScreen, 1, "guard",0, bobX));
        waves.get(0).add(new Enemy(res, screenX, screenY, groundHeight, metersInTheScreen, 2, "guard",0, bobX));
        waves.get(0).add(new Enemy(res, screenX, screenY, groundHeight, metersInTheScreen, 3, "guard",0, bobX));

      /*  waves.get(0).add(new Enemy(res, screenX, screenY, groundHeight, metersInTheScreen, 1, "regular",0, bobX));
        waves.get(0).add(new Enemy(res, screenX, screenY, groundHeight, metersInTheScreen, 2, "regular",0, bobX));
        waves.get(0).add(new Enemy(res, screenX, screenY, groundHeight, metersInTheScreen, 3, "regular",0, bobX));
        waves.get(0).add(new Enemy(res, screenX, screenY, groundHeight, metersInTheScreen, 15, "regular",0, bobX));
        waves.get(0).add(new Enemy(res, screenX, screenY, groundHeight, metersInTheScreen, 16, "regular",0, bobX));
        waves.get(0).add(new Enemy(res, screenX, screenY, groundHeight, metersInTheScreen, 17, "regular",0, bobX));
        waves.get(0).add(new Enemy(res, screenX, screenY, groundHeight, metersInTheScreen, 19, "regular",0, bobX));
        waves.get(0).add(new Enemy(res, screenX, screenY, groundHeight, metersInTheScreen, 20, "regular",0, bobX));*/



        waves.get(1).add(new Enemy(res, screenX, screenY, groundHeight, metersInTheScreen, 1, "regular",0, bobX));
        waves.get(1).add(new Enemy(res, screenX, screenY, groundHeight, metersInTheScreen, 2, "regular",0, bobX));
        waves.get(1).add(new Enemy(res, screenX, screenY, groundHeight, metersInTheScreen, 3, "regular",0, bobX));
        waves.get(1).add(new Enemy(res, screenX, screenY, groundHeight, metersInTheScreen, 4, "ghost",0, bobX));
        waves.get(1).add(new Enemy(res, screenX, screenY, groundHeight, metersInTheScreen, 5, "ghost",0, bobX));
        waves.get(1).add(new Enemy(res, screenX, screenY, groundHeight, metersInTheScreen, 17, "ghost",0, bobX));
        waves.get(1).add(new Enemy(res, screenX, screenY, groundHeight, metersInTheScreen, 19, "ghost",0, bobX));


        waves.get(2).add(new Enemy(res, screenX, screenY, groundHeight, metersInTheScreen, 1, "giant",0, bobX));
        waves.get(2).add(new Enemy(res, screenX, screenY, groundHeight, metersInTheScreen, 3, "giant",0, bobX));


        for (int i = 0; i < 20; i++)
            waves.get(3).add(new Enemy(res, screenX, screenY, groundHeight, metersInTheScreen, (i * 0.4f), "regular",0, bobX));


        waves.get(4).add(new Enemy(res, screenX, screenY, groundHeight, metersInTheScreen, 1, "giant",0, bobX));
        waves.get(4).add(new Enemy(res, screenX, screenY, groundHeight, metersInTheScreen, 1.6f, "giant",0, bobX));
        waves.get(4).add(new Enemy(res, screenX, screenY, groundHeight, metersInTheScreen, 2.5f, "giant",0, bobX));
        waves.get(4).add(new Enemy(res, screenX, screenY, groundHeight, metersInTheScreen, 3, "giant",0, bobX));


        waves.get(5).add(new Enemy(res, screenX, screenY, groundHeight, metersInTheScreen, 1, "crusader",0, bobX));

        waves.get(6).add(new Enemy(res, screenX, screenY, groundHeight, metersInTheScreen, 1, "giant",0, bobX));
        waves.get(6).add(new Enemy(res, screenX, screenY, groundHeight, metersInTheScreen, 3, "crusader",0, bobX));

        // ive yet to make these waves
        waves.get(7).add(new Enemy(res, screenX, screenY, groundHeight, metersInTheScreen, 1, "regular",0, bobX));
        waves.get(8).add(new Enemy(res, screenX, screenY, groundHeight, metersInTheScreen, 1, "regular",0, bobX));
        waves.get(9).add(new Enemy(res, screenX, screenY, groundHeight, metersInTheScreen, 1, "regular",0, bobX));







        for (int i = 1; i <= 10; i++)
            upgrades.add(new ArrayList<>());

        for (int i = 1; i <= 10; i++)
            upgrades_costs.add(new ArrayList<>());



        upgrades.get(0).add("Health");
        upgrades.get(0).add("Recharge");
        upgrades.get(0).add("Heal");
        upgrades_costs.get(0).add(25);
        upgrades_costs.get(0).add(25);
        upgrades_costs.get(0).add(25);


        upgrades.get(1).add("Damage");
        upgrades.get(1).add("Health");
        upgrades.get(1).add("Recharge");
        upgrades.get(1).add("Heal");
        upgrades_costs.get(1).add(75);
        upgrades_costs.get(1).add(25);
        upgrades_costs.get(1).add(25);
        upgrades_costs.get(1).add(25);

        upgrades_costs.get(2).add(25);
        upgrades_costs.get(3).add(25);
        upgrades_costs.get(4).add(25);
        upgrades_costs.get(5).add(25);
        upgrades_costs.get(6).add(25);
        upgrades_costs.get(7).add(25);
        upgrades_costs.get(8).add(25);
        upgrades_costs.get(9).add(25);








        upgrades.get(2).add("Recharge");
        upgrades.get(3).add("Recharge");
        upgrades.get(4).add("Recharge");
        upgrades.get(5).add("Recharge");
        upgrades.get(6).add("Recharge");
        upgrades.get(7).add("Recharge");
        upgrades.get(8).add("Recharge");
        upgrades.get(9).add("Recharge");




        // The following code:
        // 1. find the largest words' length
        // determine the size of the words within by that

        String largestWord = "";

        for (int i = 1; i < 10; i++)
            for (int j = 0; j < upgrades.get(i).size(); j++)
                if (upgrades.get(i).get(j).length() > largestWord.length())
                    largestWord = upgrades.get(i).get(j);


        textSize = (int) (res.getDisplayMetrics().scaledDensity * (circleRadius / largestWord.length() - 2)); // -2 so there will be some padding

        scaledSizeInPixels = textSize * res.getDisplayMetrics().scaledDensity;
        text_paint.setTextSize(scaledSizeInPixels);



    }







    void crusader(Enemy crusader, float meter , Resources res)
    {


        if (crusader.shield_counter > 0)
            crusader.shield_counter--;
        else
            crusader.shield_counter = 100;

        if (crusader.shield_counter == 0)
            crusader.shielded = false;


        if (!crusader.shielded)
        {
            crusader.speed = 750/meter;
            crusader.added_damage = 3;

            crusader.enemyBitmap = BitmapFactory.decodeResource(res, R.drawable.crusader_attack);
            crusader.enemyBitmap = Bitmap.createScaledBitmap(crusader.enemyBitmap, (int) crusader.width * 2, (int) crusader.height, false);
        }
        else {
            crusader.speed = 500/meter;
            crusader.added_damage = 0;

            crusader.enemyBitmap = BitmapFactory.decodeResource(res, R.drawable.crusader_shielded);
            crusader.enemyBitmap = Bitmap.createScaledBitmap(crusader.enemyBitmap, (int) crusader.width, (int) crusader.height, false);
        }

    }


    void guard (Enemy guard, Resources res, int screenX, int screenY, float groundHeight, byte metersInTheScreen, float x, float y)
    {

        double randomAngle = ThreadLocalRandom.current().nextDouble(4, 5) * Math.PI / 4;


        if (guard.shootCounter > 0)
        {
            guard.shootCounter--;

            if (guard.shootCounter < guard.shootFrequency/ 0.5 && guard.toShowShot)
                guard.toShowShot = false;
        }



        else if (guard.shootCounter == 0) // shoot
        {
            guard.toShowShot = true;

            guard.guard_projectiles.add(new Projectile(res, screenX, screenY, guard.x, guard.y, groundHeight, metersInTheScreen, 2));


            guard.guard_projectiles.get(guard.guard_projectiles.size() - 1).isThrown = true;

            guard.guard_projectiles.get(guard.guard_projectiles.size() - 1).initialX = x;
            guard.guard_projectiles.get(guard.guard_projectiles.size() - 1).initialY = y;



            guard.guard_projectiles.get(guard.guard_projectiles.size() - 1).vx
                    = (float) (Math.cos(randomAngle) * guard.guard_projectiles.get(guard.guard_projectiles.size() - 1).v);


            guard.guard_projectiles.get(guard.guard_projectiles.size() - 1).v0y
                    = (float) (Math.sin(randomAngle) * guard.guard_projectiles.get(guard.guard_projectiles.size() - 1).v);


            guard.shootCounter = guard.shootFrequency;

        }

        if (guard.toShowShot) {
            guard.enemyBitmap = BitmapFactory.decodeResource(res, R.drawable.gaurd_throwing);
            guard.enemyBitmap = Bitmap.createScaledBitmap(guard.enemyBitmap, (int) (guard.width * 1.3f), (int) (guard.width * 1.2f), false);
        } else {
            guard.enemyBitmap = BitmapFactory.decodeResource(res, R.drawable.guard);
            guard.enemyBitmap = Bitmap.createScaledBitmap(guard.enemyBitmap, (int) (guard.width * 1.2f), (int) guard.height, false);
        }

    }
}

