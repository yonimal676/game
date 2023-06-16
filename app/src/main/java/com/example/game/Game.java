package com.example.game;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import java.util.ArrayList;
import java.util.Random;

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
    int timeOfResurrection;
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
        timeOfResurrection = 450;


        circle_paint = new Paint();
        circle_paint.setColor(Color.argb(100, 70, 160, 110)); // upgrades

        res_paint = new Paint();
        res_paint.setColor(Color.argb(200, 80, 210, 120)); // resurrect button


        text_paint = new Paint();
        text_paint.setColor(Color.WHITE);


        circleRadius = screenX/20 ; // to fit characters (no word is longer than 10 characters [5 = radius])
        whenOutOfScreen = 0;



        skullBitmap = BitmapFactory.decodeResource(res, R.drawable.skull);
        skullBitmap = Bitmap.createScaledBitmap(skullBitmap, screenX/30 , screenX/42, false);



        waves = new ArrayList<>();
        deadX = new ArrayList<>();
        skeletons = new ArrayList<>();
        upgrades = new ArrayList<>();
        cx_arr = new ArrayList<>();
        cy_arr = new ArrayList<>();
        upgrades_costs = new ArrayList<>();


        for (int i = 0; i < 10; i++)
            waves.add(new ArrayList<>());








        waves.get(0).add(new Enemy(res, screenX, screenY, groundHeight, 1, "regular",0, bobX));
        waves.get(0).add(new Enemy(res, screenX, screenY, groundHeight, 2, "regular",0, bobX));
        waves.get(0).add(new Enemy(res, screenX, screenY, groundHeight, 3, "regular",0, bobX));
        waves.get(0).add(new Enemy(res, screenX, screenY, groundHeight, 4, "regular",0, bobX));
        waves.get(0).add(new Enemy(res, screenX, screenY, groundHeight, 15, "regular",0, bobX));
        waves.get(0).add(new Enemy(res, screenX, screenY, groundHeight, 16, "regular",0, bobX));
        waves.get(0).add(new Enemy(res, screenX, screenY, groundHeight, 17, "regular",0, bobX));
        waves.get(0).add(new Enemy(res, screenX, screenY, groundHeight, 19, "regular",0, bobX));
        waves.get(0).add(new Enemy(res, screenX, screenY, groundHeight, 20, "regular",0, bobX));


        waves.get(1).add(new Enemy(res, screenX, screenY, groundHeight, 1, "regular",0, bobX));
        waves.get(1).add(new Enemy(res, screenX, screenY, groundHeight, 2, "regular",0, bobX));
        waves.get(1).add(new Enemy(res, screenX, screenY, groundHeight, 3, "regular",0, bobX));
        waves.get(1).add(new Enemy(res, screenX, screenY, groundHeight, 4, "ghost",0, bobX));
        waves.get(1).add(new Enemy(res, screenX, screenY, groundHeight, 5, "ghost",0, bobX));
        waves.get(1).add(new Enemy(res, screenX, screenY, groundHeight, 17, "ghost",0, bobX));
        waves.get(1).add(new Enemy(res, screenX, screenY, groundHeight, 19, "ghost",0, bobX));
        waves.get(1).add(new Enemy(res, screenX, screenY, groundHeight, 25, "ghost",0, bobX));
        waves.get(1).add(new Enemy(res, screenX, screenY, groundHeight, 27, "ghost",0, bobX));
        waves.get(1).add(new Enemy(res, screenX, screenY, groundHeight, 29, "ghost",0, bobX));


        waves.get(2).add(new Enemy(res, screenX, screenY, groundHeight, 1, "giant",0, bobX));
        waves.get(2).add(new Enemy(res, screenX, screenY, groundHeight, 3, "giant",0, bobX));
        waves.get(2).add(new Enemy(res, screenX, screenY, groundHeight, 9, "crusader",0, bobX));


        for (int i = 0; i < 27; i++)
            waves.get(3).add(new Enemy(res, screenX, screenY, groundHeight, (i * 0.25f), "regular",0, bobX));
        waves.get(3).add(new Enemy(res, screenX, screenY, groundHeight, 40, "crusader",0, bobX));
        waves.get(3).add(new Enemy(res, screenX, screenY, groundHeight, 47, "crusader",0, bobX));


        waves.get(4).add(new Enemy(res, screenX, screenY, groundHeight, 1, "giant",0, bobX));
        waves.get(4).add(new Enemy(res, screenX, screenY, groundHeight, 1.6f, "giant",0, bobX));
        waves.get(4).add(new Enemy(res, screenX, screenY, groundHeight, 2.5f, "giant",0, bobX));
        waves.get(4).add(new Enemy(res, screenX, screenY, groundHeight, 3, "giant",0, bobX));
        waves.get(4).add(new Enemy(res, screenX, screenY, groundHeight, 4, "guard",0, bobX));
        waves.get(4).add(new Enemy(res, screenX, screenY, groundHeight, 5, "guard",0, bobX));


        waves.get(5).add(new Enemy(res, screenX, screenY, groundHeight, 1, "ghost",0, bobX));
        waves.get(5).add(new Enemy(res, screenX, screenY, groundHeight, 2, "ghost",0, bobX));
        waves.get(5).add(new Enemy(res, screenX, screenY, groundHeight, 3, "ghost",0, bobX));
        waves.get(5).add(new Enemy(res, screenX, screenY, groundHeight, 4.5f, "ghost",0, bobX));
        waves.get(5).add(new Enemy(res, screenX, screenY, groundHeight, 5, "ghost",0, bobX));
        waves.get(5).add(new Enemy(res, screenX, screenY, groundHeight, 6, "crusader",0, bobX));


        waves.get(6).add(new Enemy(res, screenX, screenY, groundHeight, 1, "giant",0, bobX));
        waves.get(6).add(new Enemy(res, screenX, screenY, groundHeight, 2, "giant",0, bobX));
        waves.get(6).add(new Enemy(res, screenX, screenY, groundHeight, 6, "crusader",0, bobX));
        waves.get(6).add(new Enemy(res, screenX, screenY, groundHeight, 7, "crusader",0, bobX));
        waves.get(6).add(new Enemy(res, screenX, screenY, groundHeight, 2, "guard",0, bobX));
        waves.get(6).add(new Enemy(res, screenX, screenY, groundHeight, 2, "guard",0, bobX));
        waves.get(6).add(new Enemy(res, screenX, screenY, groundHeight, 2, "guard",0, bobX));



        waves.get(7).add(new Enemy(res, screenX, screenY, groundHeight, 1, "crusader",0, bobX));
        waves.get(7).add(new Enemy(res, screenX, screenY, groundHeight, 2, "guard",0, bobX));
        waves.get(7).add(new Enemy(res, screenX, screenY, groundHeight, 3, "crusader",0, bobX));
        waves.get(7).add(new Enemy(res, screenX, screenY, groundHeight, 4, "guard",0, bobX));
        waves.get(7).add(new Enemy(res, screenX, screenY, groundHeight, 5, "crusader",0, bobX));
        waves.get(7).add(new Enemy(res, screenX, screenY, groundHeight, 6, "guard",0, bobX));
        waves.get(7).add(new Enemy(res, screenX, screenY, groundHeight, 7, "crusader",0, bobX));
        waves.get(7).add(new Enemy(res, screenX, screenY, groundHeight, 8, "guard",0, bobX));
        waves.get(7).add(new Enemy(res, screenX, screenY, groundHeight, 9, "crusader",0, bobX));
        waves.get(7).add(new Enemy(res, screenX, screenY, groundHeight, 10,"guard",0, bobX));





        for (int i = 0; i < 50; i++)
            waves.get(3).add(new Enemy(res, screenX, screenY, groundHeight, (i * 0.2f), "regular",0, bobX));
        for (int i = 0; i < 8; i++)
            waves.get(3).add(new Enemy(res, screenX, screenY, groundHeight, (i * 1.5f), "crusader",0, bobX));





        waves.get(9).add(new Enemy(res, screenX, screenY, groundHeight, 1, "seated king",0, bobX));







        for (int i = 1; i <= 9; i++)
            upgrades.add(new ArrayList<>());

        for (int i = 1; i <= 9; i++)
            upgrades_costs.add(new ArrayList<>());



        upgrades.get(0).add("Health");
        upgrades.get(0).add("Recharge");
        upgrades.get(0).add("Heal");
        upgrades.get(0).add("Freeze");
        upgrades_costs.get(0).add(40);
        upgrades_costs.get(0).add(45);
        upgrades_costs.get(0).add(30);
        upgrades_costs.get(0).add(135);


        upgrades.get(1).add("Damage");
        upgrades.get(1).add("Health");
        upgrades.get(1).add("Recharge");
        upgrades.get(1).add("Heal");
        upgrades_costs.get(1).add(500);
        upgrades_costs.get(1).add(100);
        upgrades_costs.get(1).add(150);
        upgrades_costs.get(1).add(65);


        upgrades.get(2).add("Recharge");
        upgrades.get(2).add("Freeze");
        upgrades.get(2).add("Heal");
        upgrades_costs.get(2).add(250);
        upgrades_costs.get(2).add(550);
        upgrades_costs.get(2).add(205);


        upgrades.get(3).add("Health");
        upgrades.get(3).add("Recharge");
        upgrades.get(3).add("Bleed");
        upgrades.get(3).add("Heal");
        upgrades.get(3).add("Impatience");
        upgrades_costs.get(3).add(311);
        upgrades_costs.get(3).add(502);
        upgrades_costs.get(3).add(650);
        upgrades_costs.get(3).add(145);
        upgrades_costs.get(3).add(1001);


        upgrades.get(4).add("Health");
        upgrades.get(4).add("Damage");
        upgrades.get(4).add("Recharge");
        upgrades.get(4).add("Heal");
        upgrades.get(4).add("Bleed");
        upgrades_costs.get(4).add(350);
        upgrades_costs.get(4).add(722);
        upgrades_costs.get(4).add(350);
        upgrades_costs.get(4).add(215);
        upgrades_costs.get(4).add(1000);



        upgrades.get(5).add("Recharge");
        upgrades.get(5).add("Magnet");
        upgrades.get(5).add("Bleed");
        upgrades.get(5).add("Heal");
        upgrades.get(5).add("Freeze");
        upgrades_costs.get(5).add(478);
        upgrades_costs.get(5).add(1243);
        upgrades_costs.get(5).add(1211);
        upgrades_costs.get(5).add(129);
        upgrades_costs.get(5).add(1112);


        upgrades.get(6).add("Recharge");
        upgrades.get(6).add("Magnet");
        upgrades.get(6).add("Bleed");
        upgrades.get(6).add("Heal");
        upgrades.get(6).add("Freeze");
        upgrades.get(6).add("Impatience");
        upgrades_costs.get(6).add(512);
        upgrades_costs.get(6).add(1322);
        upgrades_costs.get(6).add(1321);
        upgrades_costs.get(6).add(245);
        upgrades_costs.get(6).add(2000);
        upgrades_costs.get(6).add(1892);



        upgrades.get(7).add("Health");
        upgrades.get(7).add("Recharge");
        upgrades.get(7).add("Heal");
        upgrades.get(7).add("Damage");
        upgrades.get(7).add("Freeze");
        upgrades_costs.get(7).add(411);
        upgrades_costs.get(7).add(422);
        upgrades_costs.get(7).add(270);
        upgrades_costs.get(7).add(2174);
        upgrades_costs.get(7).add(2255);


        upgrades.get(8).add("Recharge");
        upgrades.get(8).add("Heal");
        upgrades.get(8).add("Impatience");
        upgrades_costs.get(8).add(1643);
        upgrades_costs.get(8).add(1402);
        upgrades_costs.get(8).add(2738);














        // The following code:
        // 1. find the largest words' length
        // determine the size of the words within by that

        String largestWord = "";

        for (int i = 0; i < 9; i++)
            for (int j = 0; j < upgrades.get(i).size(); j++)
                if (upgrades.get(i).get(j).length() > largestWord.length())
                    largestWord = upgrades.get(i).get(j);


        textSize = (int) (res.getDisplayMetrics().scaledDensity * (circleRadius / largestWord.length() - 1)); // -2 so there will be some padding

        scaledSizeInPixels = textSize * res.getDisplayMetrics().scaledDensity;
        text_paint.setTextSize(scaledSizeInPixels);



    }







    void crusader(Enemy crusader , Resources res)
    {

        if ( ! crusader.isFreezing ) {
            if (crusader.shield_counter > 0)
                crusader.shield_counter--;
            else
                crusader.shield_counter = 100;

            if (crusader.shield_counter == 0)
                crusader.shielded = false;

        }
        if (!crusader.shielded)
        {
            crusader.waitForJump = 20;
            crusader.added_damage = 3;

            crusader.enemyBitmap = BitmapFactory.decodeResource(res, R.drawable.crusader_attack);
            crusader.enemyBitmap = Bitmap.createScaledBitmap(crusader.enemyBitmap, (int) crusader.width * 2, (int) crusader.height, false);
        }
        else {
            crusader.waitForJump = 14;
            crusader.added_damage = 0;

            crusader.enemyBitmap = BitmapFactory.decodeResource(res, R.drawable.crusader_shielded);
            crusader.enemyBitmap = Bitmap.createScaledBitmap(crusader.enemyBitmap, (int) crusader.width, (int) crusader.height, false);
        }

    }


    void guard (Player bob, Enemy guard, Resources res, int screenX, int screenY, float groundHeight, byte metersInTheScreen, float x, float y)
    {
        Random random = new Random();
        double randomAngle = (4 +random.nextDouble()) * Math.PI / 4;

        if ( ! guard.isFreezing ) {
            if (guard.shootCounter > 0) {
                guard.shootCounter--;

                if (guard.shootCounter > 95)
                    guard.toShowShot = true;
                else
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
                guard.enemyBitmap = BitmapFactory.decodeResource(res, R.drawable.guard_throwing);
                guard.enemyBitmap = Bitmap.createScaledBitmap(guard.enemyBitmap, (int) (guard.width * 1.5f), (int) (guard.height), false);
            }
            else {
                guard.enemyBitmap = BitmapFactory.decodeResource(res, R.drawable.guard);
                guard.enemyBitmap = Bitmap.createScaledBitmap(guard.enemyBitmap, (int) (guard.width * 1.3f), (int) (guard.height), false);
            }


        }

        // guard shooting
        for (int i = 0; i < guard.guard_projectiles.size(); i++) // updating the already-shot projectiles ( and their dots )
        {
            guard.guard_projectiles.get(i).didHit((int) groundHeight, null, bob.damage, bob);

            guard.guard_projectiles.get(i).physics(false, 0,0);

            if (guard.guard_projectiles.get(i).toRemove)
                guard.guard_projectiles.remove(guard.guard_projectiles.get(i));
        }


    }





}

