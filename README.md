# MainActivity - Entrence page

> 1. Determine the size of the screen (screenX, screenY)
> 2. shows button that if pressed will start GameActivity to start the game 

----------------------------------------------------------------------------------------------------------------------------------------------------

# GameActivity - "Run" GameView

> 1. onCreate
> 2. create new GameView
> 3. setContentView(gameView);

----------------------------------------------------------------------------------------------------------------------------------------------------

# GameView - The game ( Runnable SurfaceView )

> ## Constructor 
> 
> > 1. Create the objects (Player, Projectile ... )
> > 2. Detemine size of screen in meters
> > 3. Create paint that will be used to darw on canvas
> > 4. Detemine sleep duration between frames
> 
> 
>
> ## Run
> > ### playing:
> >> #### 1. sleep()
> >>> Thread.sleep(sleep_millis)
> >>> Count Projectile time for physics
> 
> >> #### 2. update()
> >>> Projectile throw (physics, path)
> >>> regenerateAmmo();
> >>> Enemy movements
> 
> >> #### 3. draw()
> >>> Draw all game components on Canvas
> 
> >> #### 4. Count iteration
>
> 
>
> ## onTouchEvent
>> ### MotionEvent.ACTION_DOWN (started touch):
>>> projectile_ArrayList.add( new Projectile(...) )
>>> Calculate angle of the projectile
>>> Detemine crosshair placement
>
> ### MotionEvent.ACTION_MOVE (dragging):
>>> projectile_ArrayList.add( new Projectile(...) )
>>> Calculate angle of the projectile
>>> Detemine crosshair placement

> ### MotionEvent.ACTION_UP (stopped touch):
>>> Update ammo
>>> projectile.isThrown = true;
>>> Calculate Vx, V0y of the projectile
