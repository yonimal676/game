package com.example.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceView;


public class GameView extends SurfaceView implements Runnable
{

    private final Paint paint;
    private final Paint precursor_paint;            // trajectory of the ball.
    private final Paint path_paint;            // hitting point.

    private Background background;
    private Projectile p;
    private final Ground ground;
    private Player player;
    //    private Mob mob;

    // Objects


    private final short screenX, screenY;
    private float game_time;
    private int iterations;
    private boolean isPlaying;
    private Thread thread;
    private final GameActivity activity;
    private final Context gameActivityContext;
    private byte quarterOfLaunch;
    float SLEEP_MILLIS;
    private boolean needsShoot;
    private float angle_of_touch;
    // Technical stuff

    private Bitmap showAxis;   // screen axis in comparison to initial ball place #12
    private byte showAxisBool; // 0 -> false, 1 -> true



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
        p = new Projectile(getResources(), screenX, screenY, player.height, player.x, player.y);

        p.maxBallPull = (short) (screenY - ground.height - p.initialY - p.height); // the radius of max dist of the ball from the initial position

        game_time = 0;

        quarterOfLaunch = 0;
        p.thrown = false;
        needsShoot = false;

        paint = new Paint();
        paint.setColor(Color.rgb(189, 146, 81));
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(1f);

        path_paint = new Paint();
        path_paint.setColor(Color.rgb(78, 166, 135));
        path_paint.setStyle(Paint.Style.FILL);
        path_paint.setPathEffect(new DashPathEffect(new float[]{20, 20}, 0)); // array of ON and OFF distances,
        path_paint.setStrokeWidth(4f);

        precursor_paint = new Paint();
        precursor_paint.setColor(Color.rgb(189, 146, 81));
        precursor_paint.setStyle(Paint.Style.FILL);
        precursor_paint.setStrokeWidth(4f);

        showAxis = BitmapFactory.decodeResource(getResources(), R.drawable.play_btn);
        showAxis = Bitmap.createScaledBitmap(showAxis, p.width * 3, p.height * 3, false);
        showAxisBool = 0;
        iterations = 0;
    }



    @Override
    public void run()
    {
        while (isPlaying) // run only if playing.
        {
            sleep();//To render motion (FPS).
            update();//The components.
            draw();//Components on screen.

            iterations++;
        }
    }



    public void update () // issue: physics #25
    {
        if (p.thrown)
        {
            p.prevX = p.x;
            p.prevY = p.y; // for collision physics.



            physics();  // -> if collided will call physicsUpdate()

            p.dotArrayListX.add(p.x + fixX()); // → ↓
            if (p.dotArrayListX.size() > 15)
                p.dotArrayListX.remove(0);

            p.dotArrayListY.add(p.y + fixY()); // show path of the ball
            if (p.dotArrayListY.size() > 15)
                p.dotArrayListY.remove(0);
        }

        else if (p.isTouched) // get quarter right before the ball is thrown.
            quarterOfLaunch = p.quarter; // discussion: From where has the ball been thrown? #24




    }/* UPDATE */


    public void physics () // issue: physics #25
    {
/*

        if (quarterOfLaunch == 2)
        {
//            p.vx = -1 * abs(p.vx);
//            p.vy = abs(p.vy);
        }
        else {
            p.vy = quarterOfLaunch == 1  ?  abs(p.vy) : -1 * abs(p.vy);
//            p.vx = quarterOfLaunch == 3  ?  -1 * abs(p.vx) : abs(p.vx);
        } // =4 -> -1 * abs(ball.vy) && -1 * abs(ball.vx)

*/


        p.vy = p.v0y + p.GRAVITY * p.time;

        p.x = calcX();
        p.y = calcY();


        /* Explanation: In previous attempts, I didn't change the velocities, but rather the way the ball moves,
         for example: ball.x = ball.initialX - ball.vx * time;
         Now this did work but I had to make 4 different cases for | -- | -+ | +- | ++ |
         which is unreadable. Instead, I'll change the velocities according to where they should move towards.
         Discussion: physics #25*/
    }



    public void draw ()
    {
        if (getHolder().getSurface().isValid()) // is the surface valid?
        {
            Canvas screenCanvas = getHolder().lockCanvas(); // create the canvas

            // KEEP IN MIND THAT THE ORDER MATTERS! ↓

            //dev-mode
            if (showAxisBool == 0) screenCanvas.drawBitmap(background.backgroundBitmap, 0, 0, paint);//background
            else screenCanvas.drawBitmap(background.devBackgroundBitmap, 0, 0, paint);//background

            //Dots (path)
            for (short i = 0; i < p.dotArrayListX.size() - 2; i++)
                try{screenCanvas.drawCircle(p.dotArrayListX.get(i), p.dotArrayListY.get(i), p.width/50f * i, path_paint);} // cool idea
                catch (Exception ignored) {}


            //projectile
            if (p.thrown)
                screenCanvas.drawBitmap(p.projectileBitmap, p.x, p.y, paint);//ball

            //ground
            screenCanvas.drawBitmap(ground.groundBitmap, ground.x, ground.y, paint);//ground

            //cross-hair
            screenCanvas.drawBitmap(p.crosshairBitmap, p.aimX, p.aimY, paint);//ball

            //bob throwing projectile
            if (p.thrown && (player.iteration_of_throw + 20 >= iterations))
                screenCanvas.drawBitmap(player.bobThrowingBitmap, player.x, player.y, paint);
            else
                screenCanvas.drawBitmap(player.bobNormalBitmap, player.x, player.y, paint);


            showStats(screenCanvas);



            getHolder().unlockCanvasAndPost(screenCanvas);
        }
    } /* DRAW */




    private void sleep() // discussion: Time updating #33 | byte is like int | refresh rate is (1000 / SLEEP_MILLIS = 62.5 FPS)
    {
        SLEEP_MILLIS = 1000/120f;//

        try { Thread.sleep((long) (SLEEP_MILLIS / 2)); }
        catch (InterruptedException e) {e.printStackTrace();}


        //count time from throw:
//        ball.time = ball.thrown ? ball.time + SLEEP_MILLIS/1000f : 0;
        if (p.thrown)
            p.time += SLEEP_MILLIS / 1000;  // = 0.022 -> cuz it looks good
        else
            p.time = 0;


        game_time += SLEEP_MILLIS / 1000;  // = 0.01
    }




    float calcX () // d0(x0) + Vx * t
    {return (p.initialX + p.vx * p.time - p.crosshairSize/2f); }

    float calcY () // h(y0) + Vy * t - g * t² / 2
    {return (p.initialY + p.vy * p.time - p.GRAVITY * p.time * p.time / 2);}

    // ooh fancy 👌




    @Override
    public boolean onTouchEvent (MotionEvent event) // this is a method that helps me detect touch.
    {




        switch (event.getAction()) // down/move/up
        {


            case MotionEvent.ACTION_DOWN:// started touch


                p.reset();


                angle_of_touch = p.findAngle(event.getX(), event.getY(),
                        player.x + player.width / 2f, player.y + player.height / 2f); // also sets ball.angle


                float py = abs(Math.sin(angle_of_touch) * player.height); // for y of maxBallPull
                float px = abs(Math.cos(angle_of_touch) * player.height); // for x
                //  perpendicular (ניצב), x- adjacent (ליד), y - opposite (מול)



                quarterOfThrow();


                switch (p.quarter) {
                    case 1:
                        p.setCrosshairPosition(player.x+player.width / 2f + px, player.y + player.height / 2f - py);
                        break;
                    case 2:
                        p.setCrosshairPosition(player.x + player.width / 2f - px, player.y + player.height / 2f - py);
                        break;
                    case 3:
                        p.setCrosshairPosition(player.x + player.width / 2f - px, player.y + player.height / 2f + py);
                        break;
                    case 4:
                        p.setCrosshairPosition(player.x + player.width / 2f + px, player.y + player.height / 2f + py);
                        break;
                } // discussion: screen axis in comparison to initial ball place #12


                // RESET
                if (p.thrown && p.calcDistanceFromI(event.getX(), event.getY()) <= p.maxBallPull)
                    p.reset(); // reset when touch origin.


                // DEV MODE
                if (event.getRawX() >= screenX / 2f - p.width * 3 && event.getRawX() <= screenX / 2f + p.width * 3
                        && event.getRawY() >= 0 && event.getRawY() <= p.height * 3)
                    showAxisBool = (byte) ((showAxisBool == 0) ? 1 : 0);


                break;


// TODO : cross-hair and shoot

            case MotionEvent.ACTION_MOVE: // pressed and moving
            {


                angle_of_touch = p.findAngle(event.getX(), event.getY(),
                        player.x + player.width / 2f, player.y + player.height / 2f); // also sets ball.angle


                py = abs(Math.sin(angle_of_touch) * player.height); // for y of maxBallPull
                px = abs(Math.cos(angle_of_touch) * player.height); // for x
                //  perpendicular (ניצב), x- adjacent (ליד), y - opposite (מול)


                quarterOfThrow();


                switch (p.quarter) {
                    case 1:
                        p.setCrosshairPosition(player.x + player.width / 2f + px, player.y + player.height / 2f - py);
                        break;
                    case 2:
                        p.setCrosshairPosition(player.x + player.width / 2f - px, player.y + player.height / 2f - py);
                        break;
                    case 3:
                        p.setCrosshairPosition(player.x + player.width / 2f - px, player.y + player.height / 2f + py);
                        break;
                    case 4:
                        p.setCrosshairPosition(player.x + player.width / 2f + px, player.y + player.height / 2f + py);
                        break;
                } // discussion: screen axis in comparison to initial ball place #12


            }//ACTION_MOVE

            break;


            case MotionEvent.ACTION_UP:

                p.thrown = true;

                player.iteration_of_throw = iterations; // for showing bob throwing


                p.v = p.MAX_VELOCITY;

                p.vx = (float) (-1 * Math.cos(angle_of_touch) * p.v);
                p.v0y = (float) ( -1 * Math.sin(angle_of_touch) * p.v);


                p.initialX = p.aimX + p.crosshairSize/2f;
                p.initialY = p.aimY + p.crosshairSize/2f;


                break;
        }
        return true;
    }




    /////////////////////////////////////////////////////////////////////////
    // general functions



    public void showStats (Canvas screenCanvas)
    {


        if (showAxisBool == 1)
        {

            if (p.thrown) {
                screenCanvas.drawLine(p.prevX, p.prevY + fixY(), p.x, p.y + fixY(), paint);
//              SHOW BALL AXIS:
                screenCanvas.drawLine(0, p.y + fixY(), screenX, p.y + fixY(), paint);
                screenCanvas.drawLine(p.x + fixX(), 0, p.x + fixX(), screenY, paint);

                // SHOW i AXIS:
                screenCanvas.drawLine(0, p.initialY + fixY(), screenX, p.initialY + fixX(), paint);
                screenCanvas.drawLine(p.initialX + fixX(), 0, p.initialX + fixX(), screenY, paint);

            }
//              SHOW org AXIS:
            screenCanvas.drawLine(0, p.orgIY, screenX, p.orgIY, paint);
            screenCanvas.drawLine(p.orgIX, 0, p.orgIX, screenY, paint);





            ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


            screenCanvas.drawText("X: " + p.x + fixX(), 75, 50, paint);
            screenCanvas.drawText("Y: " + p.y + fixY(), 75, 75, paint);

            screenCanvas.drawText("Angle: ∠ " + (float) (180 / Math.PI * p.angle) + "°", 75, 110, paint);
            screenCanvas.drawText("velocity (m/s): " + p.v / p.ratioMtoPX, 75, 130, paint);
            screenCanvas.drawText("velocityX (m/s): " + p.vx / p.ratioMtoPX, 75, 150, paint);
            screenCanvas.drawText("velocityY (m/s): " + p.vy / p.ratioMtoPX, 75, 170, paint);
            screenCanvas.drawText("v0y: " + p.v0y / p.ratioMtoPX, 75, 210, paint);

            screenCanvas.drawText("collided: " + p.collision, screenX / 2f - p.width * 3, p.height * 3 + 50, paint);

            screenCanvas.drawText("quarterOfLaunch: " + quarterOfLaunch, screenX / 2f - p.width * 3, p.height * 3 + 110, paint);
            screenCanvas.drawText("range: " + p.range, screenX / 2f - p.width * 3, p.height * 3 + 130, paint);
            screenCanvas.drawText("HEIGHT: " + p.HEIGHT, screenX / 2f - p.width * 3, p.height * 3 + 150, paint);

            screenCanvas.drawText("Time: " + (int) game_time +"s", screenX / 2f + p.width * 3, 50, paint);




        }

        screenCanvas.drawBitmap(showAxis, screenX / 2f - p.width * 3, 0, paint);//button to show initial axis

    } // TODO: THIS BLOCK IS TEMP



    public void quarterOfThrow ()
    {
        if (abs(180 / Math.PI * angle_of_touch) > 90) // right side
            if (180 / Math.PI * angle_of_touch >= 0)
                p.quarter = 1; // top right corner
            else
                p.quarter = 4; // bottom right corner

        else // left side
            if (180 / Math.PI * angle_of_touch >= 0)
                p.quarter = 2; // top left corner
            else
                p.quarter = 3; // bottom left corner
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




    public Context getGameActivityContext() {
        return gameActivityContext;
    }


    public float fixX() {return p.width/2f;}// for comfortable coding.
    public float fixY() {return p.height/2f;}// for comfortable coding.
    public float abs(double num) {return (float) Math.abs(num);} // saves space -> '(float)' and 'Math.' are unnecessary.
}