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

    private final Paint paint1;            // precursor of throw trajectory.
    private final Paint paint2;            // axis in respect to initial point of the ball.
    private final Paint paint3;            // ball hit-box.
    private final Paint paint4;            // axis in respect to the ball.
    private final Paint paint5;            // trajectory of the ball.
    private final Paint path_paint;            // hitting point.

    private Background background;
    private Projectile p;
    //    private Mob mob;
    private final Ground ground;
    private Player player;
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
        p = new Projectile(getResources(), screenX, screenY);
        ground = new Ground(getResources(), screenX, screenY);
        player = new Player(getResources(), screenX, screenY, ground.height);

        p.maxBallPull = (short) (screenY - ground.height - p.initialY - p.height); // the radius of max dist of the ball from the initial position

        game_time = 0;

        quarterOfLaunch = 0;
        p.thrown = false;


        paint1 = new Paint();
        paint1.setColor(Color.rgb(224, 65, 11));
        paint1.setStyle(Paint.Style.FILL_AND_STROKE);
        paint1.setPathEffect(new DashPathEffect(new float[]{20, 20}, 0)); // array of ON and OFF distances,
        paint1.setStrokeWidth(3f);


        paint2 = new Paint();
        paint2.setColor(Color.GREEN);
        paint2.setStyle(Paint.Style.FILL);
        paint2.setStrokeWidth(5f);


        paint3 = new Paint();
        paint3.setColor(Color.RED);
        paint3.setStyle(Paint.Style.FILL);
        paint3.setStrokeWidth(1f);


        paint4 = new Paint();
        paint4.setColor(Color.WHITE);
        paint4.setStyle(Paint.Style.FILL);
        paint4.setStrokeWidth(3f);

        paint5 = new Paint();
        paint5.setColor(Color.BLUE);
        paint5.setStyle(Paint.Style.FILL);
        paint5.setStrokeWidth(4f);

        path_paint = new Paint();
        path_paint.setColor(Color.rgb(189, 146, 81));
        path_paint.setStyle(Paint.Style.FILL);
        path_paint.setStrokeWidth(4f);


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
    {//discussion (bug fix): physics don't work when angle is -90 or 90 #29


        if (p.thrown)
        {
            p.prevX = p.x;
            p.prevY = p.y; // for collision physics.
            // discussion: Changing Direction #34 || to prevent a bug (of dotArrayListX/Y) -> discussion: The Dots look disgusting #28

            physicsUpdateNoCol();  // -> if collided will call physicsUpdate()
        }

        else if (p.isTouched) // get quarter right before the ball is thrown.
            quarterOfLaunch = p.quarter; // discussion: From where has the ball been thrown? #24


        p.dotArrayListX.add(p.prevX + fixX()); // → ↓
        p.dotArrayListY.add(p.prevY + fixY()); // show path of the ball

    }/* UPDATE */


    public void physicsUpdateNoCol () // issue: physics #25
    {


        if (quarterOfLaunch == 2)
        {
            p.vx = abs(p.vx);
            p.vy = abs(p.vy);
        }
        else {
            p.vy = quarterOfLaunch == 1  ?  abs(p.vy) : -1 * abs(p.vy);
            p.vx = quarterOfLaunch == 3  ?  abs(p.vx) : -1 * abs(p.vx);
        } // =4 -> -1 * abs(ball.vy) && -1 * abs(ball.vx)

        p.vy = p.v0y + p.GRAVITY * p.time;



        p.x = calcX();
        p.y = calcY();


        // Explanation: In previous attempts, I didn't change the velocities, but rather the way the ball moves,
        // for example: ball.x = ball.initialX - ball.vx * time;
        // Now this did work but I had to make 4 different cases for | -- | -+ | +- | ++ |
        // which is unreadable. Instead, I'll change the velocities according to where they should move towards.
        // Discussion: physics #25


    }



    public void draw ()
    {
        if (getHolder().getSurface().isValid()) // is the surface valid?
        {
            Canvas screenCanvas = getHolder().lockCanvas(); // create the canvas

            // KEEP IN MIND THAT THE ORDER MATTERS! ↓

            if (showAxisBool == 0)
                screenCanvas.drawBitmap(background.backgroundBitmap, 0, 0, paint1);//background
            else
                screenCanvas.drawBitmap(background.devBackgroundBitmap, 0, 0, paint1);//background





            if (p.time > 0.032)
                for (short i = 0; i < p.dotArrayListX.size() - 1; i++)
                    try{
//                        if (!(ball.y + ball.height + (ball.y - ball.prevY) >= screenY - ground.height)) // TODO
                        screenCanvas.drawCircle(p.dotArrayListX.get(i), p.dotArrayListY.get(i), p.width/20f, paint3/*path_paint*/);
                    }
                    catch (Exception ignored) {}
            // discussion: The Dots look disgusting #28




            if (p.thrown)
            {
                // see in onTouchEvent -> case MotionEvent.ACTION_UP
                if (player.iteration_of_throw + 20 >= iterations)
                    screenCanvas.drawBitmap(player.bobThrowingBitmap, player.x, player.y, paint1);//ball
                else
                    screenCanvas.drawBitmap(player.bobNormalBitmap, player.x,player.y , paint1);//ball
            }
            else
                screenCanvas.drawBitmap(player.bobNormalBitmap, player.x,player.y , paint1);//ball


            screenCanvas.drawBitmap(p.projectileBitmap, p.x,p.y , paint1);//ball
            screenCanvas.drawBitmap(ground.groundBitmap, ground.x, ground.y, paint1);//ground

            showStats(screenCanvas);




            if ( ! p.thrown) // draw this line only before the ball is thrown.
            {
                p.v = 0;
                p.vx = 0;
                p.vy = 0;


                //discussion: X and Y of stop screenCanvas.DrawLine #15 | issue: correcting the line with the ball #13
                if (p.calcDistanceFromI(p.x + fixX(), p.y + fixY()) > p.width / 2f) // don't draw inside the ball
                {

                    float lineStopX = abs((Math.cos(p.ballAngle()) * fixX())); // similar to perpAdj
                    float lineStopY = abs((Math.sin(p.ballAngle()) * fixX())); // similar to perpOpp | fixX() = radius of the ball so..


                    switch (p.quarter) // draw a line to the opposite corner
                    {
                        case 1:
                            screenCanvas.drawLine(p.x + fixX() - lineStopX, p.y + fixY() + lineStopY,
                                    p.initialX - (p.x - p.initialX) - fixX(),
                                    p.initialY + (p.initialY - p.y) - fixY(), paint1);
                            break;
                        case 2:
                            screenCanvas.drawLine(p.x + fixX() + lineStopX, p.y + fixY() + lineStopY,
                                    p.initialX + (p.initialX - p.x) - fixX(),
                                    p.initialY + (p.initialY - p.y) - fixY(), paint1);
                            break;
                        case 3:
                            screenCanvas.drawLine(p.x + fixX() + lineStopX, p.y + fixY() - lineStopY,
                                    p.initialX + (p.initialX - p.x) - fixX(),
                                    p.initialY - (p.y - p.initialY) - fixY(), paint1);
                            break;
                        case 4:
                            screenCanvas.drawLine(p.x + fixX() - lineStopX, p.y + fixY() - lineStopY,
                                    p.initialX - (p.x - p.initialX) - fixX(),
                                    p.initialY - (p.y - p.initialY) - fixY(), paint1);
                            break;
                    }
                }
            }
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
    {return (p.initialX + p.vx * p.time); }

    float calcY () // h(y0) + Vy * t - g * t² / 2
    {return (p.initialY + p.vy * p.time - p.GRAVITY * p.time * p.time / 2);}

    // ooh fancy 👌




    @Override
    public boolean onTouchEvent (MotionEvent event) // this is a method that helps me detect touch.
    {
        switch (event.getAction()) // down/move/up
        {

            case MotionEvent.ACTION_DOWN:// started touch

                if (p.isTouching(event.getX(),event.getY()) && ! p.thrown)
                    p.isTouched = true; // 'ball' has a boolean method that indicates whether the object is touched.


                if (p.thrown && p.calcDistanceFromI(event.getX(), event.getY()) <= p.maxBallPull)
                    p.reset(); // reset when touch origin.


                if (event.getRawX() >= screenX /2f - p.width * 3 && event.getRawX() <= screenX /2f + p.width * 3
                        && event.getRawY() >= 0 && event.getRawY() <= p.height * 3)

                    showAxisBool = (byte) ((showAxisBool == 0) ? 1 : 0);


                // turn dev mode on or off.

                break;




            case MotionEvent.ACTION_MOVE: // pressed and moving
            {
                if (p.isTouched) // if touched the ball
                {

                    float angle_of_touch = p.findAngleWhenOutside(event.getX(), event.getY()); // also sets ball.angle


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

                    // TODO: DON'T EVER CHANGE THIS !!!



                    if (p.calcDistanceFromI(event.getX(), event.getY()) < p.maxBallPull) // if in drag-able circle
                        p.setPosition(event.getX(), event.getY());                         // than drag normally.



                    else // issue: finger drag outside of radius #4
                    {
                        float perpOpp = abs(Math.sin(angle_of_touch) * p.maxBallPull); // for y of maxBallPull
                        float perpAdj = abs(Math.cos(angle_of_touch) * p.maxBallPull); // for x
                        //  perp- perpendicular (ניצב), adj- adjacent (ליד), opp- opposite (מול)


                        switch (p.quarter)
                        {
                            case 1: p.setPosition(p.initialX + perpAdj, p.initialY - perpOpp); break;
                            case 2: p.setPosition(p.initialX - perpAdj, p.initialY - perpOpp); break;
                            case 3: p.setPosition(p.initialX - perpAdj, p.initialY + perpOpp); break;
                            case 4: p.setPosition(p.initialX + perpAdj, p.initialY + perpOpp); break;
                        } // discussion: screen axis in comparison to initial ball place #12

                    }// outside drag-able circle
                }// if touched the ball
            }//ACTION_MOVE

            break;



            case MotionEvent.ACTION_UP: // ended touch || discussion: Throw from stretched point #40

                if (p.isTouched) // if touched the ball in the first place.
                {
                    if (p.calcDistanceFromI(p.x + fixX(), p.y + fixY()) < p.width)
                    {
                        p.x = p.orgIX - fixX();
                        p.y = p.orgIY - fixY();
                    } // discussion: Disable ball movement when only touched briefly #31


                    else { // shot

                        p.thrown = true;

                        player.iteration_of_throw = iterations;

                        p.percentOfPull = p.calcDistanceFromI(p.x + fixX(), p.y + fixY()) / p.maxBallPull;

                        p.v = p.percentOfPull * p.MAX_VELOCITY;
                        // Percent of pull * max velocity = percent of max velocity

                        p.vx = abs(Math.cos(p.angle) * p.v);

                        if (quarterOfLaunch == 1 || quarterOfLaunch == 2)
                            p.v0y = abs(Math.sin(p.angle) * p.v);
                        else
                            p.v0y = -1 * abs(Math.sin(p.angle) * p.v);
                        // Both of these values never change after the ball is thrown.

                        p.orgIX = p.initialX;
                        p.orgIY = p.initialY;

                        p.initialX = p.x;
                        p.initialY = p.y;
                    }

                }
                p.isTouched = false;

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
                screenCanvas.drawLine(p.prevX, p.prevY + fixY(), p.x, p.y + fixY(), paint5);
//              SHOW BALL AXIS:
                screenCanvas.drawLine(0, p.y + fixY(), screenX, p.y + fixY(), paint4);
                screenCanvas.drawLine(p.x + fixX(), 0, p.x + fixX(), screenY, paint4);

                // SHOW i AXIS:
                screenCanvas.drawLine(0, p.initialY + fixY(), screenX, p.initialY + fixX(), paint3);
                screenCanvas.drawLine(p.initialX + fixX(), 0, p.initialX + fixX(), screenY, paint3);

            }
//              SHOW org AXIS:
            screenCanvas.drawLine(0, p.orgIY, screenX, p.orgIY, paint2);
            screenCanvas.drawLine(p.orgIX, 0, p.orgIX, screenY, paint2);





            ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


            screenCanvas.drawText("X: " + p.x + fixX(), 75, 50, paint2);
            screenCanvas.drawText("Y: " + p.y + fixY(), 75, 75, paint2);

            screenCanvas.drawText("Angle: ∠ " + (float) (180 / Math.PI * p.angle) + "°", 75, 110, paint2);
            screenCanvas.drawText("velocity (m/s): " + p.v / p.ratioMtoPX, 75, 130, paint2);
            screenCanvas.drawText("velocityX (m/s): " + p.vx / p.ratioMtoPX, 75, 150, paint2);
            screenCanvas.drawText("velocityY (m/s): " + p.vy / p.ratioMtoPX, 75, 170, paint2);
            screenCanvas.drawText("v0y: " + p.v0y / p.ratioMtoPX, 75, 210, paint2);

            screenCanvas.drawText("collided: " + p.collision, screenX / 2f - p.width * 3, p.height * 3 + 50, paint2);

            screenCanvas.drawText("quarterOfLaunch: " + quarterOfLaunch, screenX / 2f - p.width * 3, p.height * 3 + 110, paint2);
            screenCanvas.drawText("range: " + p.range, screenX / 2f - p.width * 3, p.height * 3 + 130, paint2);
            screenCanvas.drawText("HEIGHT: " + p.HEIGHT, screenX / 2f - p.width * 3, p.height * 3 + 150, paint2);

            screenCanvas.drawText("Time: " + (int) game_time +"s", screenX / 2f + p.width * 3, 50, paint2);




        }

        screenCanvas.drawBitmap(showAxis, screenX / 2f - p.width * 3, 0, paint1);//button to show initial axis

    } // TODO: THIS BLOCK IS TEMP




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