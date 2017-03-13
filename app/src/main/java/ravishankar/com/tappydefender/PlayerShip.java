package ravishankar.com.tappydefender;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

/**
 * Created by RAVISHANKAR CHAUBEY on 16-06-2016.
 */
public class PlayerShip {
    public PlayerShip(Context context,int screenX,int screenY)
    {
        x=50;
        y=50;
        speed=1;
        bitmap= BitmapFactory.decodeResource(context.getResources(),R.drawable.ship);
        boosting=false;
        maxY=screenY-bitmap.getHeight();
        minY=0;
        hitBox=new Rect(x,y,bitmap.getWidth(),bitmap.getHeight());
        shieldStrength=2;
    }
    private boolean boosting;
    private Bitmap bitmap;
    private int x, y;
    private int speed = 0;
    private final int GRAVITY=-12;
    private int maxY;
    private int minY;
    private final int MIN_SPEED=1;
    private final int MAX_SPEED=20;
    private Rect hitBox;
    private int shieldStrength;

    public Rect getHitBox(){
        return hitBox;
    }

    public int getShieldStrength() {
        return shieldStrength;
    }
    public void reduceStrength(){
        shieldStrength--;
    }

    public void update()
    {
       if(boosting) {
           speed += 2;
       }
        else {
           speed=-5;
       }

        if(speed>MAX_SPEED){
            speed=MAX_SPEED;
        }

        if(speed<MIN_SPEED){
            speed=MIN_SPEED;
        }

        y-=speed+GRAVITY;
        if(y<minY){
            y=minY;
        }
        if(y>maxY){
            y=maxY;
        }

        hitBox.left=x;
        hitBox.top=y;
        hitBox.right=x+bitmap.getWidth();
        hitBox.bottom=y+bitmap.getHeight();
    }

    public Bitmap getBitmap()
    {
        return bitmap;
    }
    public int getSpeed()
    {
        return speed;
    }
    public int getX()
    {
        return x;
    }
    public int getY()
    {
        return y;
    }

    public void setBoosting(){
        boosting=true;
    }
    public void stopBoosting(){
        boosting=false;
    }
}
