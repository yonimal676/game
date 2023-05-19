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

    private final Paint paint;
    private final Paint path_paint;

    private Background background;
    private final Ground ground;
    private Player player;
    //private Mob mob;

    // Objects


    private final short screenX, screenY;
    private float game_time;
    private int iterations;
    private boolean isPlaying;
    private Thread thread;
    private final GameActivity activity;
    private final Context gameActivityContext;
    private int sleep_millis;

    private float angle_of_touch;
    byte quarter;
    private ArrayList<Projectile> p_arr;
    float px, py;


    public GameView(GameActivity activity, short screenX, short screenY)
    {
        super(activity);

        this.activity = activity;
        gameActivityContext = this.activity;

        this.screenX = screenX;
        this.screenY = screenY;

        isPlaying = true;

        background = new Background(getResources(), screenX, screenY);
        ground = new Ground(getResources(), screenX, screenY);
        player = new Player(getResources(), screenX, screenY, ground.height);


        game_time = 0;
        p_arr = new ArrayList<>();

        paint = new Paint();
        paint.setColor(Color.rgb(189, 146, 81));
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(1f);

        path_paint = new Paint();
        path_paint.setColor(Color.rgb(78, 166, 135));
        path_paint.setStyle(Paint.Style.FILL);
        path_paint.setPathEffect(new DashPathEffect(new float[]{20, 20}, 0)); // array of ON and OFF distances,
        path_paint.setStrokeWidth(4f);


        iterations = 0;
        sleep_millis = 10;
    }


    @Override
    public void run() {
        while (isPlaying) // run only if playing.
        {
            sleep();//To render motion (FPS).
            update();//The components.
            draw();//Components on screen.

            iterations++;
        }
    }





    public void update() // issue: physics #25
    {

        if (player.ammo <= player.maxAmmo) {
            if (player.iterationForAmmoRegeneration == 0)
                player.ammo++;
            else
                player.iterationForAmmoRegeneration--;
        }
        else
            player.iterationForAmmoRegeneration = player.AmmoRegenerationPace;


        for (int i = 0; i < p_arr.size(); i++)
        {

            //supposed removal of projectile
            if (toRemove(p_arr.get(i).x, p_arr.get(i).y, p_arr.get(i).height))
            {
                p_arr.get(i).damage = 0;
                p_arr.get(i).projectileBitmap = p_arr.get(i).transparentBitmap; // "remove" the projectile

            }


        }





        for (int i = 0; i < p_arr.size(); i++) // shoot (empty the array)
        {

            if (p_arr.get(i).isThrown)
            {

                p_arr.get(i).prevX = p_arr.get(i).x ;
                p_arr.get(i).prevY = p_arr.get(i).y ; // prevX=x and then update x.


                physics(p_arr.get(i));  // -> if collided will call physicsUpdate()



                // limit dot arrays
                p_arr.get(i).dotArrayListX.add(p_arr.get(i).x + fixX(p_arr.get(i))); // → ↓
                if (p_arr.get(i).dotArrayListX.size() > 15)
                    p_arr.get(i).dotArrayListX.remove(0);

                p_arr.get(i).dotArrayListY.add(p_arr.get(i).y + fixY(p_arr.get(i))); // show path of the ball
                if (p_arr.get(i).dotArrayListY.size() > 15)
                    p_arr.get(i).dotArrayListY.remove(0);


                if (toRemove(p_arr.get(i).dotArrayListX.get(0),   p_arr.get(i).dotArrayListY.get(0), p_arr.get(i).height ))
                    p_arr.remove(p_arr.get(i));

            }
        }

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


            for (int i = 0; i < p_arr.size(); i++)
            {

                //projectile
                if (p_arr.get(i).isThrown)
                    screenCanvas.drawBitmap(p_arr.get(i).projectileBitmap, p_arr.get(i).x, p_arr.get(i).y, paint);//ball


                //Dots (path)
                for (short j = 0; j < p_arr.get(i).dotArrayListX.size() - 2; j++) // draw *all* the dots of *all* projectiles | -2 for delay
                {
                    try {
                        screenCanvas.drawCircle(p_arr.get(i).dotArrayListX.get(j), p_arr.get(i).dotArrayListY.get(j), p_arr.get(i).width / 40f * j+1, path_paint);
                    }
                    catch(Exception ignored){}
                }
                // cool idea:  * i * i when boosted

            }

            //ground
            screenCanvas.drawBitmap(ground.groundBitmap, ground.x, ground.y, paint);//ground

            //cross-hair
            screenCanvas.drawBitmap(player.crosshairBitmap, player.aimX, player.aimY, paint);//ball



            //bob throwing projectile
            if ( (player.iteration_of_throw + (100/sleep_millis) >= iterations))
                screenCanvas.drawBitmap(player.bobThrowingBitmap, player.x, player.y, paint);
            else
                screenCanvas.drawBitmap(player.bobNormalBitmap, player.x, player.y, paint);





            for (int i = 1; i <= player.maxHearts; i++)
            {
                if (i > player.hearts)
                    screenCanvas.drawBitmap(player.emptyHeartBitmap, i * player.crosshairSize, player.crosshairSize, paint);
                else
                    screenCanvas.drawBitmap(player.heartBitmap, i * player.crosshairSize, player.crosshairSize, paint);
            }




            for (int i = 1; i <= player.maxAmmo; i++)
            {
                if (i > player.ammo)
                    screenCanvas.drawBitmap(player.emptyAmmoBitmap, i * player.crosshairSize, player.crosshairSize * 2.5f, paint);
                else
                    screenCanvas.drawBitmap(player.ammoBitmap, i * player.crosshairSize, player.crosshairSize * 2.5f, paint);

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
    public boolean onTouchEvent(MotionEvent event) // this is a method that helps me detect touch.
    {

        switch (event.getAction()) // down/move/up
        {



            case MotionEvent.ACTION_DOWN:// started touch


                p_arr.add(new Projectile(getResources(), screenX, screenY, player.aimX, player.aimY, ground.height));



                if ( ! p_arr.isEmpty())
                    angle_of_touch = p_arr.get(p_arr.size()-1).findAngle(event.getX(), event.getY(), player.midX, player.midY);


                quarterOfThrow();



                px = (float) Math.abs(Math.cos(angle_of_touch) * player.height);
                py = (float) Math.abs(Math.sin(angle_of_touch) * player.height);
                //  perpendicular (ניצב), x- adjacent (ליד), y - opposite (מול)


                switch (quarter)
                {
                    case 1: player.setCrosshairPosition(player.midX + px, player.midY - py);
                        break;
                    case 2: player.setCrosshairPosition(player.midX - px, player.midY - py);
                        break;
                    case 3: player.setCrosshairPosition(player.midX - px, player.midY + py);
                        break;
                    case 4: player.setCrosshairPosition(player.midX + px, player.midY + py);
                        break;
                }




                break;







            case MotionEvent.ACTION_MOVE: // pressed and moving


                if ( ! p_arr.isEmpty())
                    angle_of_touch = p_arr.get(p_arr.size()-1).findAngle(event.getX(), event.getY(), player.midX, player.midY);

                quarterOfThrow();

                px = (float) Math.abs(Math.cos(angle_of_touch) * player.height);
                py = (float) Math.abs(Math.sin(angle_of_touch) * player.height);
                //  perpendicular (ניצב), x- adjacent (ליד), y - opposite (מול)


                switch (quarter)
                {
                    case 1: player.setCrosshairPosition(player.midX + px, player.midY - py);
                        break;
                    case 2: player.setCrosshairPosition(player.midX - px, player.midY - py);
                        break;
                    case 3: player.setCrosshairPosition(player.midX - px, player.midY + py);
                        break;
                    case 4: player.setCrosshairPosition(player.midX + px, player.midY + py);
                        break;
                }

                break;




            case MotionEvent.ACTION_UP:

                if ( ! p_arr.isEmpty())
                {
                    if (p_arr.size() <= player.ammo)
                        player.ammo--;




                    p_arr.get(p_arr.size() - 1).isThrown = true;


                    p_arr.get(p_arr.size() - 1).initialX = player.aimX;
                    p_arr.get(p_arr.size() - 1).initialY = player.aimY;


                    player.iteration_of_throw = iterations; // for showing bob throwing

                    p_arr.get(p_arr.size() - 1).vx = (float) (-1 * Math.cos(angle_of_touch) * p_arr.get(p_arr.size() - 1).v);
                    p_arr.get(p_arr.size() - 1).v0y = (float) (-1 * Math.sin(angle_of_touch) * p_arr.get(p_arr.size() - 1).v);
                    // TODO: why -1 * ?
                }

                break;

        }
        return true;
    }







    public boolean toRemove (float x, float y, float height) // needed because then it could also be used for dotArray
    {
/*        if (x + width > screenX/2)
            return true;*/


        return y + height >= screenY - ground.height;


        //or if hit mob
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




    public Context getGameActivityContext() {return gameActivityContext;}
    public float fixX(Projectile p) {return p.width/2f;}// for comfortable coding.
    public float fixY(Projectile p) {return p.height/2f;}// for comfortable coding.
}