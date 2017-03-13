package ravishankar.com.tappydefender;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import java.util.Random;

/**
 * Created by RAVISHANKAR CHAUBEY on 17-06-2016.
 */
public class EnemyShip {
    public EnemyShip(Context context,int screenX,int screenY){
        Random generator=new Random();
        int whichbitmap=generator.nextInt(3);
        switch (whichbitmap){
            case 0: bitmap= BitmapFactory.decodeResource(context.getResources(),R.drawable.enemy3);
                    break;
            case 1: bitmap= BitmapFactory.decodeResource(context.getResources(),R.drawable.enemy2);
                    break;
            case 2: bitmap= BitmapFactory.decodeResource(context.getResources(),R.drawable.enemy);
                    break;
        }
        //scaleBitmap(screenX);
        //bitmap= BitmapFactory.decodeResource(context.getResources(),R.drawable.enemy);
        maxX=screenX;
        maxY=screenY;
        minX=0;
        minY=0;
        speed=generator.nextInt(6)+10;
        x=screenX;
        y=generator.nextInt(maxY)-bitmap.getHeight();
        hitBox=new Rect(x,y,bitmap.getWidth(),bitmap.getHeight());
    }

    /*public void scaleBitmap(int x) {
        if(x<1000){
            bitmap=Bitmap.createScaledBitmap(bitmap,bitmap.getWidth()/3,bitmap.getHeight()/3,false);
        }
        else if(x<1200){
            bitmap=Bitmap.createScaledBitmap(bitmap,bitmap.getWidth()/2,bitmap.getHeight()/2,false);
        }
    }*/

    private Bitmap bitmap;
    private int x,y;
    private int speed=1;
    private int maxX;
    private int minX;
    private int maxY;
    private int minY;
    private Rect hitBox;

    public Bitmap getBitmap(){
        return bitmap;
    }
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public Rect getHitBox(){
        return hitBox;
    }

    public void update(int playerspeed){
        x-=playerspeed;
        x-=speed;
        if(x<minX-bitmap.getWidth()){
            Random generator=new Random();
            speed=generator.nextInt(10)+10;
            x=maxX;
            y=generator.nextInt(maxY)-bitmap.getHeight();
        }

        hitBox.left=x;
        hitBox.top=y;
        hitBox.right=x+bitmap.getWidth();
        hitBox.bottom=y+bitmap.getHeight();
    }

    public void setX(int x) {
        this.x = x;
    }
}
