
/* TODO:

wizard enemy (?)
more upgrades
make the game good
text of story in-game (?)
bob's hearts don't go down
*/

package com.example.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceView;
import java.util.ArrayList;


public class GameView extends SurfaceView implements Runnable
{

    private final Paint paint1;
    private final Paint paint2;
    private final Paint white_text_paint;

    private final int screenX, screenY;
    private byte metersInScreen;

    private int iteration;
    private boolean isPlaying;
    private Thread thread;
    private final GameActivity activity;
    private final int sleep_millis;


    private float angle_of_touch;
    private byte quarter;
    private ArrayList<Projectile> p_arr;
    private float px, py;
    private final Ground ground;
    private Player player;
    private Game game;
    // Objects


    private float meter;




    public GameView(GameActivity activity, short screenX, short screenY)
    {
        super(activity);

        this.activity = activity;

        this.screenX = screenX;
        this.screenY = screenY;

        metersInScreen = 25; // screenX / 25 will be one meter
        meter = (float) screenX / metersInScreen; // TODO: remember that if you scale this up, the ball will NOT move in the same ratio!!

        isPlaying = true;

        ground = new Ground(getResources(), screenX, screenY, metersInScreen);
        player = new Player(getResources(), screenX, screenY, ground.height, metersInScreen);
        game = new Game(getResources(), screenX, screenY, ground.height, metersInScreen, player.x + player.width, player.y);

        p_arr = new ArrayList<>();




        white_text_paint = new Paint();
        white_text_paint.setColor(Color.rgb(255, 255, 255));
        white_text_paint.setTextSize(15 * getResources().getDisplayMetrics().scaledDensity);

        paint1 = new Paint();
        paint1.setColor(Color.rgb(12, 207, 152));
        paint1.setTextSize(10 * getResources().getDisplayMetrics().scaledDensity);

        paint2 = new Paint();
        paint2.setColor(Color.rgb(12, 207, 152));
        paint2.setTextSize(10 * getResources().getDisplayMetrics().scaledDensity);;

        iteration = 0;
        sleep_millis = 10;

    }


    @Override
    public void run()
    {
        while (isPlaying) // run only if playing.
        {
            sleep();//To render motion (FPS).
            update();//The components.
            draw();//Components on screen.

            iteration++;
        }

        // when not playing:
        stopGame();
    }





    public void update()
    {
        if ( player.hearts <= 0 )
            isPlaying = false;

        player.regenerateAmmo();


        if (game.canResurrect > 0) // show availability
            game.canResurrect--;

        else
            game.res_paint.setColor(Color.argb(200, 80, 210, 150));


        // wave is running
        if (game.isInWave)
        {
            game.didContinue = false;

            for (int enemy_index = 0; enemy_index < game.waves.get(game.currentWave).size(); enemy_index++) // enemies within wave
            {
                Enemy enemy = game.waves.get(game.currentWave).get(enemy_index); // temp

                enemy.x -= enemy.speed;

                enemy.XPWaveMultiplier(game.currentWave);

                // enemy reaches player
                if (enemy.x <= player.x + player.width)
                {
                    player.hearts -= (enemy.hearts + enemy.added_damage);
                    game.waves.get(game.currentWave).remove(enemy);
                }


                for (int i = 0; i < p_arr.size(); i++ )
                    p_arr.get(i).didHit(ground.height, enemy, player.damage, player); // check if any projectile hit any enemy


                if (enemy.type.equals("crusader"))
                    game.crusader(enemy, meter, getResources());



                for (int skeleton_index = 0; skeleton_index < game.skeletons.size(); skeleton_index++)
                {

                    Enemy skeleton = game.skeletons.get(skeleton_index); // temp

                    if (skeleton.x + skeleton.width >= enemy.x && skeleton.x + skeleton.width <= enemy.x + enemy.width)
                    {
                        if (enemy.hearts > skeleton.hearts)
                        {
                            enemy.hearts -= skeleton.hearts;
                            game.skeletons.remove(skeleton);
                        }

                        else if (enemy.hearts < skeleton.hearts)
                        {
                            skeleton.hearts -= (enemy.hearts + enemy.added_damage);
                            enemy.hearts = 0;
//                            game.waves.get(game.currentWave).get(enemy);
                        }

                        else {
                            skeleton.hearts = 0;
                            enemy.hearts = 0;
                            game.skeletons.remove(skeleton);
                        }
                    }

                }

                //check life for removal
                if (enemy.hearts <= 0)
                {
                    game.deadX.add(enemy.x);
                    game.waves.get(game.currentWave).remove(enemy);

                    player.xp += enemy.xp;
                }

            } // enemies

        }// isInWave





        // update resurrection state
        if (game.didResurrect && game.skeletons.isEmpty())
            game.didResurrect = false;



        for(int skeleton_index = 0; skeleton_index < game.skeletons.size(); skeleton_index++)
        {

            Enemy skeleton = game.skeletons.get(skeleton_index); // temp

            skeleton.x += skeleton.speed; // starts at deadX


            // because you can kill skeletons too.
            for (int i = 0; i < p_arr.size(); i++)
            {
                p_arr.get(i).didHit(ground.height, skeleton, player.damage, player);

                if (p_arr.get(i).toRemove)
                    p_arr.remove(p_arr.get(i));

                if (skeleton.hearts <= 0)
                    game.skeletons.remove(skeleton);

                // play minecraft skeleton death noise
            }
        } //resurrection



        for (int i = 0; i < p_arr.size(); i++) // updating the already-shot projectiles ( and their dots )
        {
            p_arr.get(i).didHit(ground.height, null, player.damage, player);


            if (p_arr.get(i).isThrown)
            {
                p_arr.get(i).physics();


                // limit dot arrays
                p_arr.get(i).dotArrayListX.add(p_arr.get(i).x + p_arr.get(i).width / 2);
                if (p_arr.get(i).dotArrayListX.size() > 30)
                    p_arr.get(i).dotArrayListX.remove(0);

                p_arr.get(i).dotArrayListY.add(p_arr.get(i).y + p_arr.get(i).height / 2);
                if (p_arr.get(i).dotArrayListY.size() > 30)
                    p_arr.get(i).dotArrayListY.remove(0);

            }

            if (p_arr.get(i).toRemove)
                p_arr.remove(p_arr.get(i));
        }


        // guard shooting
        for (int enemy_index = 0; enemy_index < game.waves.get(game.currentWave).size(); enemy_index++)
        {
            if (game.waves.get(game.currentWave).get(enemy_index).type.equals("guard"))
            {
                Enemy enemy = game.waves.get(game.currentWave).get(enemy_index); // temp

                game.guard(enemy, getResources(), screenX, screenY, ground.height, metersInScreen, enemy.x, enemy.y);


                for (int i = 0; i < enemy.guard_projectiles.size(); i++) // updating the already-shot projectiles ( and their dots )
                {
                    enemy.guard_projectiles.get(i).didHit(ground.height, null, player.damage, player);

                    enemy.guard_projectiles.get(i).physics();


                    if (enemy.guard_projectiles.get(i).toRemove)
                        enemy.guard_projectiles.remove(enemy.guard_projectiles.get(i));
                }
            }
        }



        // wave format (update / stop)
        if (game.waves.get(game.currentWave).isEmpty())
            game.isInWave = false;

        else
            game.isInWave = true;



        if (! game.isInWave)
            if (game.didContinue)
            {
                game.currentWave++;
                game.didContinue = false;
            }






    }











    public void draw()
    {
        if (getHolder().getSurface().isValid()) // is the surface valid?
        {
            Canvas screenCanvas = getHolder().lockCanvas(); // create the canvas


            //background
            screenCanvas.drawBitmap(ground.backgroundBitmap, 0, 0, paint1);//background
            // wave number
            screenCanvas.drawText((game.currentWave + 1) + " - " + 10, screenX - (15 * getResources().getDisplayMetrics().scaledDensity) * 7 , player.meter, white_text_paint);


            // show skulls to later resurrect
            for (int i = 0; i < game.deadX.size(); i++)
                screenCanvas.drawBitmap(game.skullBitmap, game.deadX.get(i), screenY - ground.height - screenY/ 40f, paint1);

            // only when resurrected skeletons would be added
            for (int i = 0; i < game.skeletons.size(); i++)
                screenCanvas.drawBitmap(game.skeletons.get(i).enemyBitmap, game.skeletons.get(i).x, screenY - ground.height - game.skeletons.get(i).height, paint1);




            //projectile and dots (path)
            for (int i = 0; i < p_arr.size(); i++)
            {
                if (p_arr.get(i).isThrown)
                {
                    for (int j = 0; j < p_arr.get(i).dotArrayListX.size() - 2; j++) // draw *all* the dots of *all* projectiles | -2 for delay
                    {
                        screenCanvas.drawCircle( p_arr.get(i).dotArrayListX.get(j), p_arr.get(i).dotArrayListY.get(j),
                                p_arr.get(i).width / 40f * (float) Math.sqrt(j) + 1, paint2);
                        // cool idea:  * i * i when damage is boosted


                        p_arr.get(i).dotArrayListY.set(j, p_arr.get(i).dotArrayListY.get(j) + 5);

                        if (Math.ceil(p_arr.get(i).dotArrayListX.get(j)) % 4 == 0)
                            p_arr.get(i).dotArrayListX.set(j, p_arr.get(i).dotArrayListX.get(j) - 2);
                        else
                            p_arr.get(i).dotArrayListX.set(j, p_arr.get(i).dotArrayListX.get(j) + 2);



                    }


                    if (player.damage == 1)
                        screenCanvas.drawBitmap(p_arr.get(i).projectileBitmap, p_arr.get(i).x, p_arr.get(i).y, paint2);
                    else if (player.damage == 2)
                        screenCanvas.drawBitmap(p_arr.get(i).projectile1Bitmap, p_arr.get(i).x, p_arr.get(i).y, paint2);
                    else
                        screenCanvas.drawBitmap(p_arr.get(i).projectile2Bitmap, p_arr.get(i).x, p_arr.get(i).y, paint2);
                }
            }


            //ground
            screenCanvas.drawBitmap(ground.groundBitmap, ground.x, ground.y, paint1);//ground

            //cross-hair
            screenCanvas.drawBitmap(player.crosshairBitmap, player.aimX, player.aimY, paint2);//ball



            //bob & throwing projectile
            if ( player.iteration_of_throw != -1 ) // if (ever thrown), otherwise bob is shown to be throwing in the first iterations.
            {
                if ((player.iteration_of_throw + (100 / sleep_millis) > iteration) && game.isInWave)
                    screenCanvas.drawBitmap(player.bobThrowingBitmap, player.x, player.y, paint1);
                else
                    screenCanvas.drawBitmap(player.bobNormalBitmap, player.x, player.y, paint1);
            }
            else
                screenCanvas.drawBitmap(player.bobNormalBitmap, player.x, player.y, paint1);

            // heart system
            for (int i = 1; i <= player.maxHearts; i++) {
                if (player.hearts <= 0)
                    screenCanvas.drawBitmap(player.emptyHeartBitmap, i * player.crosshairSize, player.crosshairSize, paint1);
                else {
                    if (i > player.hearts)
                        screenCanvas.drawBitmap(player.emptyHeartBitmap, i * player.crosshairSize, player.crosshairSize, paint1);
                    else
                        screenCanvas.drawBitmap(player.heartBitmap, i * player.crosshairSize, player.crosshairSize, paint1);
                }
            }

            // ammo system
            for (int i = 1; i <= player.maxAmmo; i++)
                if (i > player.ammo)
                    screenCanvas.drawBitmap(player.emptyAmmoBitmap, i * player.crosshairSize, player.crosshairSize * 2.5f, paint1);
                else
                    screenCanvas.drawBitmap(player.ammoBitmap, i * player.crosshairSize, player.crosshairSize * 2.5f, paint1);



            // display enemies, hearts, skeletons, resurrect button
            if (game.isInWave)
            {
                for (int enemy_index = 0; enemy_index < game.waves.get(game.currentWave).size(); enemy_index++) // game in game
                {
                    Enemy enemy = game.waves.get(game.currentWave).get(enemy_index);

                    if (enemy.guard_projectiles != null && enemy.guard_projectiles.size() > 0)
                        for (int i = 0; i < enemy.guard_projectiles.size(); i++)
                            screenCanvas.drawBitmap(enemy.guard_projectiles.get(i).guardProjectileBitmap, enemy.guard_projectiles.get(i).x,
                                    enemy.guard_projectiles.get(i).y, paint1);

                    screenCanvas.drawBitmap(enemy.enemyBitmap, enemy.x, enemy.y, paint1);
                    enemy.displayHearts(screenCanvas, white_text_paint);
                }

                screenCanvas.drawCircle(screenX/2f - game.circleRadius/2f, screenY/10f , game.circleRadius ,game.res_paint);

                float resurrectTextSize = game.text_paint.measureText("Resurrect");

                screenCanvas.drawText("Resurrect", screenX/2f - game.circleRadius/2f - resurrectTextSize /2,
                        screenY/10f + paint1.getTextSize()/2, game.text_paint);

            }


            else // -> between waves
            {
                afterWaveScreen(screenCanvas, player.xp);   // show upgrades and stuff

                if (game.currentWave < 9)
                    screenCanvas.drawText("(i)  " + game.explainUpgrade, screenX / 2f, player.y, game.text_paint); // explain upgrades
            }

            if (player.hasMagnet)
                screenCanvas.drawBitmap(player.magnetBitmap, player.x + (player.width-player.crosshairSize)/2,
                        player.y - player.crosshairSize, paint1);


            getHolder().unlockCanvasAndPost(screenCanvas);
        }
    }




    private void sleep() {
        try {
            Thread.sleep(sleep_millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        for (int i = 0; i < p_arr.size(); i++) {
            if (p_arr.get(i).isThrown)
                p_arr.get(i).time += sleep_millis/1000f;
            else
                p_arr.get(i).time = 0;
        }

        for (int enemy_index = 0; enemy_index < game.waves.get(game.currentWave).size(); enemy_index++) {

            if (game.waves.get(game.currentWave).get(enemy_index).type.equals("guard"))
            {
                Enemy guard = game.waves.get(game.currentWave).get(enemy_index);

                for (int i = 0; i < guard.guard_projectiles.size(); i++)
                {
                    if (guard.guard_projectiles.get(i).isThrown)
                        guard.guard_projectiles.get(i).time += sleep_millis / 1000f;
                    else
                        guard.guard_projectiles.get(i).time = 0;
                }
            }
        }
    }










    @Override
    public boolean onTouchEvent (MotionEvent event) // this is a method that helps me detect touch.
    {

        switch (event.getAction()) // down/move/up
        {
            case MotionEvent.ACTION_DOWN:// started touch

                if ( game.isInWave )
                {
                    if (player.ammo >= 1)
                        p_arr.add(new Projectile(getResources(), screenX, screenY, player.aimX, player.aimY, ground.height, metersInScreen,1));
                    // I need to put this ?? because if you spam there's a bug caused by the delay in ACTION_DOWN and ACTION_UP

                    if (!p_arr.isEmpty())
                    {
                        angle_of_touch = p_arr.get(p_arr.size() - 1).findAngle(event.getX(), event.getY(), player.midX, player.midY);

                        quarterOfThrow();


                        px = (float) Math.abs(Math.cos(angle_of_touch) * player.height);
                        py = (float) Math.abs(Math.sin(angle_of_touch) * player.height);
                        //  perpendicular (ניצב), x- adjacent (ליד), y - opposite (מול)


                        switch (quarter) {
                            case 1:
                                player.setCrosshairPosition(player.midX + px, player.midY - py);
                                break;
                            case 2:
                                player.setCrosshairPosition(player.midX - px, player.midY - py);
                                break;
                            case 3:
                                player.setCrosshairPosition(player.midX - px, player.midY + py);
                                break;
                            case 4:
                                player.setCrosshairPosition(player.midX + px, player.midY + py);
                                break;
                        }
                    }
                }


                //explaining system
                else if ( ! game.didContinue )
                    for (int i = 0; i < game.upgrades.get(game.currentWave).size(); i++)
                        if (event.getX() >= game.cx_arr.get(i) - game.circleRadius && event.getX() <= game.cx_arr.get(i) + game.circleRadius
                                && event.getY() >= game.cy_arr.get(i) - game.circleRadius && event.getY() <= game.cy_arr.get(i) + game.circleRadius)
                            explainUpgrades(i);




                break;







            case MotionEvent.ACTION_MOVE: // pressed and moving

                if ( game.isInWave )
                {
                    // calc crosshair position
                    if (!p_arr.isEmpty()) {
                        angle_of_touch = p_arr.get(p_arr.size() - 1).findAngle(event.getX(), event.getY(), player.midX, player.midY);

                        quarterOfThrow();

                        px = (float) Math.abs(Math.cos(angle_of_touch) * player.height);
                        py = (float) Math.abs(Math.sin(angle_of_touch) * player.height);
                        //  perpendicular (ניצב), x- adjacent (ליד), y - opposite (מול)


                        switch (quarter) {
                            case 1:
                                player.setCrosshairPosition(player.midX + px, player.midY - py);
                                break;
                            case 2:
                                player.setCrosshairPosition(player.midX - px, player.midY - py);
                                break;
                            case 3:
                                player.setCrosshairPosition(player.midX - px, player.midY + py);
                                break;
                            case 4:
                                player.setCrosshairPosition(player.midX + px, player.midY + py);
                                break;
                        }
                    }
                }

                //explaining system
                else
                    for (int i = 0; i < game.upgrades.get(game.currentWave).size(); i++)
                        if (event.getX() >= game.cx_arr.get(i) - game.circleRadius && event.getX() <= game.cx_arr.get(i) + game.circleRadius
                                && event.getY() >= game.cy_arr.get(i) - game.circleRadius && event.getY() <= game.cy_arr.get(i) + game.circleRadius)
                            explainUpgrades(i);

                break;





            case MotionEvent.ACTION_UP:

                if ( game.isInWave )
                {
                    // pressed resurrection button
                    if (event.getX() >= screenX / 2f - game.circleRadius * 0.7f && event.getX() <= screenX / 2f + game.circleRadius * 0.7f
                            && event.getY() >= screenY / 10f - game.circleRadius * 0.7f && event.getY() <= screenY / 10f + game.circleRadius * 0.7f) {
                        if (game.canResurrect == 0) {
                            if ( ! game.deadX.isEmpty()) {
                                resurrect();

                                game.canResurrect = 200; // restart counter
                            }
                        }
                    }


                    // shoot !

                    else if (player.ammo >= 1)
                    {
                        p_arr.add(new Projectile(getResources(), screenX, screenY, player.aimX, player.aimY, ground.height, metersInScreen,1));
                        // I need to put this ?? because if you spam there's a bug caused by the delay in ACTION_DOWN and ACTION_UP



                        angle_of_touch = p_arr.get(p_arr.size() - 1).findAngle(event.getX(), event.getY(), player.midX, player.midY);
                        quarterOfThrow();

                        px = (float) Math.abs(Math.cos(angle_of_touch) * player.height);
                        py = (float) Math.abs(Math.sin(angle_of_touch) * player.height);
                        //  perpendicular (ניצב), x- adjacent (ליד), y - opposite (מול)


                        switch (quarter) {
                            case 1:
                                player.setCrosshairPosition(player.midX + px, player.midY - py);
                                break;
                            case 2:
                                player.setCrosshairPosition(player.midX - px, player.midY - py);
                                break;
                            case 3:
                                player.setCrosshairPosition(player.midX - px, player.midY + py);
                                break;
                            case 4:
                                player.setCrosshairPosition(player.midX + px, player.midY + py);
                                break;
                        }


                        player.ammo--;

                        if (p_arr.size() > 0)
                        {
                            p_arr.get(p_arr.size() - 1).isThrown = true;

                            p_arr.get(p_arr.size() - 1).initialX = player.aimX;
                            p_arr.get(p_arr.size() - 1).initialY = player.aimY;


                            player.iteration_of_throw = iteration; // for showing bob throwing


                            p_arr.get(p_arr.size() - 1).vx = (float) (-1 * Math.cos(angle_of_touch) * p_arr.get(p_arr.size() - 1).v);
                            p_arr.get(p_arr.size() - 1).v0y = (float) (-1 * Math.sin(angle_of_touch) * p_arr.get(p_arr.size() - 1).v);
                        }
                    }
                }


                // else -> upgrade choosing mechanism
                else {
                    // continue button pressed
                    if (event.getX() >= game.Ccx - game.circleRadius && event.getX() <= game.Ccx + game.circleRadius
                            && event.getY() >= game.Ccy - game.circleRadius && event.getY() <= game.Ccy + game.circleRadius)
                        game.didContinue = true;


                    else  // check if pressed any upgrade
                        for (int i = 0; i < game.upgrades.get(game.currentWave).size(); i++)
                            if (event.getX() >= game.cx_arr.get(i) - game.circleRadius && event.getX() <= game.cx_arr.get(i) + game.circleRadius
                                    && event.getY() >= game.cy_arr.get(i) - game.circleRadius && event.getY() <= game.cy_arr.get(i) + game.circleRadius)
                                upgrade(game.upgrades.get(game.currentWave).get(i));
                }

                break;
        }
        return true;
    }


    public void resurrect ()
    {

        // for each skull -> create skeleton
        for (int i = 0; i < game.deadX.size(); i++)
            game.skeletons.add(new Enemy(getResources(), screenX, screenY, ground.height,
                    (byte) 25, 1, "skeleton", game.deadX.get(i), player.x));

        // no longer needed
        game.deadX.clear();

        // to remove skulls
        game.didResurrect = true;

        game.res_paint.setColor(Color.argb(200, 180, 100, 100));


    }



    public void afterWaveScreen (Canvas canvas, int playerXP)
    {
        if (game.currentWave == 9)
            canvas.drawText("I'm gonna need to find a new town...",
                    screenX/2f - "I'm gonna need to find a new town...".length() /2f * game.textSize, screenY/2f, game.text_paint );

        else {
            // draw upgrade bubbles of current wave;
            for (int upgrade_index = 0; upgrade_index < game.upgrades.get(game.currentWave).size(); upgrade_index++) {
                if (player.hearts == player.maxHearts && game.upgrades.get(game.currentWave).get(upgrade_index).equals("Heal"))
                    continue;
                // start x is: screenX / 8f | start x of next: a radius away from end of last one
                // check if all the upgrades fit in the screen

                if (screenX / 8f + (game.circleRadius * 3 * upgrade_index) < screenX) {
                    game.cx = screenX / 8f + (game.circleRadius * 3 * upgrade_index);
                    game.cy = screenY / 3f;
                }
                else { // (lower)
                    if (game.whenOutOfScreen == 0)
                        game.whenOutOfScreen = upgrade_index;

                    game.cx = screenX / 8f + (game.circleRadius * 3 * (upgrade_index - game.whenOutOfScreen));
                    game.cy = 2 * screenY / 3f;
                }


                // for every circle there's a (x,y) point of the middle point
                game.cx_arr.add(game.cx);
                game.cy_arr.add(game.cy); // for clicking


                if (game.upgrades_costs.get(game.currentWave).size() > upgrade_index) {
                    // to show the user that if they don't have enough XP they can't upgrade
                    if (player.xp < game.upgrades_costs.get(game.currentWave).get(upgrade_index))
                        game.circle_paint.setColor(Color.argb(100, 160, 60, 60));
                    else
                        game.circle_paint.setColor(Color.argb(100, 40, 160, 110));


                    float xpTextSize = game.text_paint.measureText("xp: " + game.upgrades_costs.get(game.currentWave).get(upgrade_index));

                    canvas.drawText("xp: " + game.upgrades_costs.get(game.currentWave).get(upgrade_index),
                            game.cx - xpTextSize / 2,
                            game.cy - game.circleRadius - game.scaledSizeInPixels / 2, game.text_paint);
                }

                canvas.drawCircle(game.cx, game.cy, game.circleRadius, game.circle_paint); // x,y are the middle points! not up-left!!


                // text in circle would be in the middle
                canvas.drawText(game.upgrades.get(game.currentWave).get(upgrade_index),
                        game.cx - game.circleRadius + (game.circleRadius * 2 - game.upgrades.get(game.currentWave).get(upgrade_index).length() * game.textSize) / 2f,
                        game.cy + game.textSize / 2f, game.text_paint);

                canvas.drawText("xp: " + playerXP, screenX / 2f, 50, game.text_paint);
            }


            // show 'continue' button regardless of how many upgrades are available
            game.Ccx = screenX - game.circleRadius * 2;
            game.Ccy = screenY - game.circleRadius * 2;

            game.circle_paint.setColor(Color.argb(150, 180, 60, 60));
            canvas.drawCircle(game.Ccx, game.Ccy, game.circleRadius, game.circle_paint);
            game.circle_paint.setColor(Color.argb(100, 40, 160, 110)); // upgrades

            canvas.drawText("continue", game.Ccx - game.circleRadius + (game.circleRadius * 2 - "continue".length() * game.textSize) / 2f,
                    game.Ccy + game.textSize / 2f, game.text_paint);
        }
    }


    float distance (float x1, float y1, float x2, float y2)
    { return (float) Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));}

    public void upgrade ( String upgrade_name )
    {
        int indexOfUpgrade = game.upgrades.get(game.currentWave).indexOf(upgrade_name);

        // can't upgrade

        if (game.upgrades_costs.get(game.currentWave).size() > indexOfUpgrade)
        {
            if (game.upgrades_costs.get(game.currentWave).get(indexOfUpgrade) > player.xp)
                explainUpgrades(-1);

                // can upgrade
            else {
                switch (upgrade_name) {
                    case "Health":
                        player.maxHearts++;

                        // if players' full life - adding one to max hearts will add a heart as well
                        if (player.hearts == player.maxHearts - 1)
                            player.hearts = player.maxHearts;

                        player.xp -= game.upgrades_costs.get(game.currentWave).get((indexOfUpgrade));

                        game.upgrades.get(game.currentWave).remove("Health");
                        game.upgrades_costs.get(game.currentWave).remove(game.upgrades_costs.get(game.currentWave).get(indexOfUpgrade));

                        break;

                    case "Heal":
                        player.xp -= game.upgrades_costs.get(game.currentWave).get((indexOfUpgrade));

                        player.hearts++;
                        game.upgrades.get(game.currentWave).remove("Heal");
                        break;

                    case "Recharge":
                        player.maxAmmo++;

                        player.xp -= game.upgrades_costs.get(game.currentWave).get((indexOfUpgrade));

                        game.upgrades.get(game.currentWave).remove("Recharge");
                        game.upgrades_costs.get(game.currentWave).remove(game.upgrades_costs.get(game.currentWave).get(indexOfUpgrade));
                        break;

                    case "Damage":
                        player.damage++;

                        player.xp -= game.upgrades_costs.get(game.currentWave).get((indexOfUpgrade));

                        game.upgrades.get(game.currentWave).remove("Damage");
                        game.upgrades_costs.get(game.currentWave).remove(game.upgrades_costs.get(game.currentWave).get(indexOfUpgrade));

                        if (player.damage == 2)
                            paint2.setARGB(255, 222, 172, 9);

                        if (player.damage == 3)
                            paint2.setARGB(255, 120, 0, 0);
                        break;

                    case "Magnet":
                        player.hasMagnet = true;

                        player.xp -= game.upgrades_costs.get(game.currentWave).get((indexOfUpgrade));

                        game.upgrades.get(game.currentWave).remove("Magnet");
                        game.upgrades_costs.get(game.currentWave).remove(game.upgrades_costs.get(game.currentWave).get(indexOfUpgrade));


                        break;

                }
            }
        }
    }


    public void explainUpgrades (int serialNum)
    {

        if (serialNum == -1)
            game.explainUpgrade = "insufficient XP points";

        else {
            switch (game.upgrades.get(game.currentWave).get(serialNum)) // oracle says this is the same as .equals()
            {

                case "Health":
                    game.explainUpgrade = "+1 max heart";
                    break;

                case "Heal":
                    game.explainUpgrade = "heals one heart";
                    break;

                case "Recharge":
                    game.explainUpgrade = "+1 max ammo";
                    break;

                case "Damage":
                    game.explainUpgrade = "+1 damage";
                    break;

                case "Freeze": // show icicle effect above the head of the entity
                    game.explainUpgrade = "20% chance to freeze enemy when hit";
                    break;

                case "Bleed": // show red effect above the head of the entity
                    game.explainUpgrade = "slowly damage enemies";
                    break;

            }

        }
    }




    public void quarterOfThrow ()
    {
        if (Math.abs(180 / Math.PI * angle_of_touch) > 90) // right side
            if (180 / Math.PI * angle_of_touch >= 0)
                quarter = 1; // top right corner
            else
                quarter = 4; // bottom right corner

        else // left side
            if (180 / Math.PI * angle_of_touch >= 0)
                quarter = 2; // top left corner
            else
                quarter = 3; // bottom left corner
    }




    public void resume() // discussion: "activity lifecycle"
    {
        isPlaying = true;
        thread = new Thread(this); // -> "this" is the run() method above.
        thread.start();
    } // resume the game


    public void pause()  // discussion: "activity lifecycle"
    {
        try {
            isPlaying = false;
            thread.join(); // join = stop
            Thread.sleep(100);

        }
        catch (InterruptedException e) {e.printStackTrace();}
    }



    public void stopGame()  // discussion: "activity lifecycle"
    {
        activity.finish();
    }

}