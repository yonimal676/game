
/* TODO: Bugs:

1. spam when empty ammo
2. path missing a dot
3. projectile occasionally teleports
4. when shooting fast and to very different directions, projectile is seen once at the previous aim(x,y)
5. touching slightly at the beginning of the game doen't shoot

*/

package com.example.game;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceView;


import java.util.ArrayList;


public class GameView extends SurfaceView implements Runnable
{


    private Background background;
    private final Ground ground;
    private Player player;
    private Game game;

    // Objects


    private final int screenX, screenY;
    private byte metersInScreen;
    private final Paint paint;
    private final Paint paint1;

    private float game_time;
    private int iteration;
    private boolean isPlaying;
    private Thread thread;
    private final GameActivity activity;
    private final Context gameActivityContext;
    private final int sleep_millis;


    private float angle_of_touch;
    private byte quarter;
    private ArrayList<Projectile> p_arr;
    private float px, py;




    public GameView(GameActivity activity, short screenX, short screenY)
    {
        super(activity);

        this.activity = activity;
        gameActivityContext = this.activity;

        this.screenX = screenX;
        this.screenY = screenY;

        metersInScreen = 25; // screenX / 25 will be one meter

        isPlaying = true;

        background = new Background(getResources(), screenX, screenY, metersInScreen);
        ground = new Ground(getResources(), screenX, screenY, metersInScreen);
        player = new Player(getResources(), screenX, screenY, ground.height, metersInScreen);
        game = new Game(getResources(), screenX, screenY, ground.height, metersInScreen);

        game_time = 0;
        p_arr = new ArrayList<>();


        paint = new Paint();
        paint.setColor(Color.rgb(78, 166, 135));
        paint.setTextSize(15 * getResources().getDisplayMetrics().scaledDensity);



        paint1 = new Paint();
        paint1.setColor(Color.BLACK);
        paint1.setTextSize(10 * getResources().getDisplayMetrics().scaledDensity);



        iteration = 0;
        sleep_millis = 10;




    }


    @Override
    public void run() {
        while (isPlaying) // run only if playing.
        {
            sleep();//To render motion (FPS).
            update();//The components.
            draw();//Components on screen.

            iteration++;
        }
    }





    public void update() // issue: physics #25
    {

        player.regenerateAmmo();


        // wave is running
        if (game.isInWave)
        {
            for (int enemy_index = 0; enemy_index < game.waves.get(game.currentWave).size(); enemy_index++) // enemies within wave
            {
                Enemy enemy = game.waves.get(game.currentWave).get(enemy_index); // temp

                enemy.x -= enemy.speed;


                if (enemy.x <= player.x + player.width)
                {
                    player.hearts -= enemy.hearts;
                    game.waves.get(game.currentWave).remove(enemy);
                }


                for (int i = 0; i < p_arr.size(); i++ )
                    p_arr.get(i).didHit(ground.height, enemy); // check if any projectile hit any enemy




                for (int skeleton_index = 0; skeleton_index < game.skeletons.size(); skeleton_index++)
                {

                    Enemy skeleton = game.skeletons.get(skeleton_index); // temp


                    if (skeleton.x + skeleton.width >= enemy.x && skeleton.x + skeleton.width <= enemy.x + enemy.width)
                    {
                        if (enemy.hearts > skeleton.hearts) {
                            enemy.hearts -= skeleton.hearts;
                            skeleton.hearts = 0;
                        }

                        else if (enemy.hearts < skeleton.hearts) {
                            skeleton.hearts -= enemy.hearts;
                            enemy.hearts = 0;
                        }

                        else {
                            skeleton.hearts = 0;
                            enemy.hearts = 0;
                        }
                    }



                    if (skeleton.hearts == 0)
                    {
                        game.skeletons.remove(skeleton);

                    }

                }

                //check life for removal
                if (enemy.hearts == 0) {
                    game.deadX.add(enemy.x);
                    game.waves.get(game.currentWave).remove(enemy);
                }

            } // enemies





        }// isInWave






        // update resurrection state
        if (game.didResurrect && game.skeletons.isEmpty())
            game.didResurrect = false;


//        if ( game.didResurrect )
        {
            for(int skeleton_index = 0; skeleton_index < game.skeletons.size(); skeleton_index++)
            {

                Enemy skeleton = game.skeletons.get(skeleton_index); // temp

                skeleton.x += skeleton.speed; // starts at deadX


                // because you can kill skeletons too.
                for (int i = 0; i < p_arr.size(); i++)
                {
                    p_arr.get(i).didHit(ground.height, skeleton);

                    if (p_arr.get(i).toRemove)
                        p_arr.remove(p_arr.get(i));

                    if (skeleton.hearts <= 0)
                        game.skeletons.remove(skeleton_index);
                    // play minecraft skeleton death noise
                }
            }
        } //resurrection



        for (int i = 0; i < p_arr.size(); i++) // updating the already-shot projectiles ( and their dots )
        {
            p_arr.get(i).didHit(ground.height, null);


            if (p_arr.get(i).isThrown)
            {
                physics(p_arr.get(i));


                // limit dot arrays
                p_arr.get(i).dotArrayListX.add(p_arr.get(i).x + p_arr.get(i).width / 2);
                if (p_arr.get(i).dotArrayListX.size() > 15)
                    p_arr.get(i).dotArrayListX.remove(0);

                p_arr.get(i).dotArrayListY.add(p_arr.get(i).y + p_arr.get(i).height / 2);
                if (p_arr.get(i).dotArrayListY.size() > 15)
                    p_arr.get(i).dotArrayListY.remove(0);
/*                    // if all the dots of a projectile are gone, remove the projectile completely.
                if (toRemove(p_arr.get(i).dotArrayListX.get(0),   p_arr.get(i).dotArrayListY.get(0), p_arr.get(i).height, null))
                    p_arr.remove(p_arr.get(i));
                boolean flag = false;
                for (int j = 0; j < p_arr.get(i).dotArrayListX.size(); j++)
                {
                    if (p_arr.get(i).dotArrayListX.get())
                }*/
            }

            if (p_arr.get(i).toRemove)
                p_arr.remove(p_arr.get(i));
        }




        // wave format (update / stop)
        if (game.waves.get(game.currentWave).isEmpty())
        {
            game.isInWave = false;

            checkUpgrades(); // don't show irrelevant upgrades

            if (game.didContinue)
            {
                game.currentWave++;
                game.didContinue = false;
            }

//            p_arr.clear(); <- unnecessary removal of projectiles at the end of a wave.
        }
        else
            game.isInWave = true;






        if ( player.hearts == 0 )
            isPlaying = false;



    }




    public void physics(Projectile p) // issue: physics #25
    {

        p.vy = p.v0y + p.GRAVITY * p.time;

        p.x = p.initialX + p.vx * p.time; // x0 + Vx * t
        p.y = p.initialY + p.vy * p.time - p.GRAVITY * p.time * p.time / 2; // y0 + Vy * t - g * t² / 2
    }






    public void draw()
    {
        if (getHolder().getSurface().isValid()) // is the surface valid?
        {
            Canvas screenCanvas = getHolder().lockCanvas(); // create the canvas


            //background
            screenCanvas.drawBitmap(background.backgroundBitmap, 0, 0, paint);//background

            screenCanvas.drawText((game.currentWave + 1) + " - " + 10, screenX - (15 * getResources().getDisplayMetrics().scaledDensity) * 7 , player.meter, paint);



            if ( ! game.didResurrect)
                for (int i = 0; i < game.deadX.size(); i++)
                    screenCanvas.drawBitmap(game.skullBitmap, game.deadX.get(i), screenY - ground.height - screenY/ 40f, paint);


            else
                for (int i = 0; i < game.skeletons.size(); i++)
                    screenCanvas.drawBitmap(game.skeletons.get(i).enemyBitmap, game.skeletons.get(i).x, screenY - ground.height - game.skeletons.get(i).height, paint);




            //projectile and dots (path)
            for (int i = 0; i < p_arr.size(); i++)
            {
                if (p_arr.get(i).isThrown)
                {
                    for (short j = 0; j < p_arr.get(i).dotArrayListX.size() - 2; j++) // draw *all* the dots of *all* projectiles | -2 for delay
                        screenCanvas.drawCircle(p_arr.get(i).dotArrayListX.get(j), p_arr.get(i).dotArrayListY.get(j),
                                p_arr.get(i).width / 40f * j + 1, paint);
                    // cool idea:  * i * i when boosted


                    screenCanvas.drawBitmap(p_arr.get(i).projectileBitmap, p_arr.get(i).x, p_arr.get(i).y, paint);//ball
                }
            }


            //ground
            screenCanvas.drawBitmap(ground.groundBitmap, ground.x, ground.y, paint);//ground

            //cross-hair
            screenCanvas.drawBitmap(player.crosshairBitmap, player.aimX, player.aimY, paint);//ball




            //bob & throwing projectile
            if ( player.iteration_of_throw != -1 ) // if (ever thrown), otherwise bob is shown to be throwing in the first iterations.
            {
                if ((player.iteration_of_throw + (100 / sleep_millis) > iteration) && game.isInWave)
                    screenCanvas.drawBitmap(player.bobThrowingBitmap, player.x, player.y, paint);
                else
                    screenCanvas.drawBitmap(player.bobNormalBitmap, player.x, player.y, paint);
            }
            else
                screenCanvas.drawBitmap(player.bobNormalBitmap, player.x, player.y, paint);



            // heart counter
            for (int i = 1; i <= player.maxHearts; i++)
            {
                if (i > player.hearts)
                    screenCanvas.drawBitmap(player.emptyHeartBitmap, i * player.crosshairSize, player.crosshairSize, paint);
                else
                    screenCanvas.drawBitmap(player.heartBitmap, i * player.crosshairSize, player.crosshairSize, paint);
            }


            // ammo counter
            for (int i = 1; i <= player.maxAmmo; i++)
            {
                if (i > player.ammo)
                    screenCanvas.drawBitmap(player.emptyAmmoBitmap, i * player.crosshairSize, player.crosshairSize * 2.5f, paint);
                else
                    screenCanvas.drawBitmap(player.ammoBitmap, i * player.crosshairSize, player.crosshairSize * 2.5f, paint);

            }




            if (game.isInWave)
            {
                for (int enemy_index = 0; enemy_index < game.waves.get(game.currentWave).size(); enemy_index++) // game in game
                {

                    Enemy enemy = game.waves.get(game.currentWave).get(enemy_index);

                    screenCanvas.drawBitmap(enemy.enemyBitmap, enemy.x, enemy.y, paint);

                    enemy.displayHearts(screenCanvas, paint);
                }


                screenCanvas.drawCircle(screenX/2f - game.circleRadius*0.7f/2, screenY/10f , game.circleRadius * 0.7f ,paint);

                screenCanvas.drawText("Resurrect", screenX/2f - game.circleRadius*0.7f/2 +
                                ((game.circleRadius*0.7f*2 - ("Resurrect".length() * paint1.getTextSize())))/2,
                        screenY/10f + paint1.getTextSize()/2, paint1);


            }

            else // -> between waves
            {
                game.afterWaveScreen(screenX, screenY, screenCanvas, game.currentWave);
                screenCanvas.drawText("(i)  " + game.explainUpgrade, screenX/2f, player.y, paint);
            }


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


        game_time += sleep_millis/1000f;
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
                        p_arr.add(new Projectile(getResources(), screenX, screenY, player.aimX, player.aimY, ground.height, metersInScreen));
                    // I need to put this ?? because if you spam there's a bug caused by the delay in ACTION_DOWN and ACTION_UP


                    if (!p_arr.isEmpty())
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


                //explaining system
                else
                    for (int i = 0; i < game.upgrades.get(game.currentWave).size(); i++)
                        if (event.getX() >= game.cx_arr.get(i) - game.circleRadius && event.getX() <= game.cx_arr.get(i) + game.circleRadius
                                && event.getY() >= game.cy_arr.get(i) - game.circleRadius && event.getY() <= game.cy_arr.get(i) + game.circleRadius)
                            explainUpgrades(i);






                break;







            case MotionEvent.ACTION_MOVE: // pressed and moving

                if ( game.isInWave )
                {

                    if (!p_arr.isEmpty())
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


                    if (event.getX() >= screenX/2f - game.circleRadius*0.7f/3 && event.getX() <= screenX/2f - game.circleRadius*0.7f
                            && event.getY() >= screenY/10f && event.getY() <= screenY/10f + game.circleRadius * 0.7f * 2)
                        resurrect();


                    else if (player.ammo >= 1)
                    {
                        player.ammo--;


                        if (p_arr.size() > 0)
                        {
                            p_arr.get(p_arr.size() - 1).isThrown = true;

                            p_arr.get(p_arr.size() - 1).initialX = player.aimX;
                            p_arr.get(p_arr.size() - 1).initialY = player.aimY;


                            player.iteration_of_throw = iteration; // for showing bob throwing


                            p_arr.get(p_arr.size() - 1).vx = (float) (-1 * Math.cos(angle_of_touch) * p_arr.get(p_arr.size() - 1).v);
                            p_arr.get(p_arr.size() - 1).v0y = (float) (-1 * Math.sin(angle_of_touch) * p_arr.get(p_arr.size() - 1).v);
                            // TODO: why -1 * ?
                        }
                    }
                }


                // else -> choosing mechanism
                else
                {
                    if (event.getX() >= game.Ccx - game.circleRadius && event.getX() <= game.Ccx + game.circleRadius
                            && event.getY() >= game.Ccy - game.circleRadius && event.getY() <= game.Ccy + game.circleRadius) {
                        game.didContinue = true;

                        resurrect();
                    }




                    else {
                        for (int i = 0; i < game.upgrades.get(game.currentWave).size(); i++)
                        {
                            if (event.getX() >= game.cx_arr.get(i) - game.circleRadius && event.getX() <= game.cx_arr.get(i) + game.circleRadius
                                    && event.getY() >= game.cy_arr.get(i) - game.circleRadius && event.getY() <= game.cy_arr.get(i) + game.circleRadius)
                                upgrade(game.upgrades.get(game.currentWave).get(i));


                            // game.upgrades.get(game.currentWave).get(i) -> is the string of upgrade
                            // double click to upgrade???

                        }
                    }
                }



                break;

        }
        return true;
    }

    public void resurrect()
    {
        for (int i = 0; i < game.deadX.size(); i++)
            game.skeletons.add(new Enemy(getResources(), screenX, screenY, ground.height, (byte) 25, 1, "skeleton", game.deadX.get(i)));

        game.deadX.clear();


        game.didResurrect = true; // to remove skeletons

    }


    // don't show irrelevant upgrades
    public void checkUpgrades ()
    {
        if (player.hearts == player.maxHearts)
            game.upgrades.get(game.currentWave).remove("Health");

    }

    public void upgrade ( String upgrade )
    {


        if ( upgrade.equals("Health") )
        {
            player.maxHearts++;
            game.upgrades.get(game.currentWave).remove("Health");
        }


        if ( upgrade.equals("Heal") )
        {
            player.hearts++;
            game.upgrades.get(game.currentWave).remove("Health");
        }

        if ( upgrade.equals("Recharge") )
        {
            player.maxAmmo++;
            game.upgrades.get(game.currentWave).remove("Recharge");
        }
    }


    public void explainUpgrades (int serialNum)
    {
        switch (game.upgrades.get(game.currentWave).get(serialNum)) // orcale says this is the same as .equals()
        {

            case "Health":
                game.explainUpgrade = "+1 max heart";
                break;

            case "Heal":
                game.explainUpgrade = "heals one heart";
                break;

            case "Recharge":
                game.explainUpgrade = "+1 max ammo";

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

//            activity.PauseMenu();
        }
        catch (InterruptedException e) {e.printStackTrace();}
    }


    void gameOver()
    {
        pause();
    }


    public Context getGameActivityContext() {return gameActivityContext;}
}