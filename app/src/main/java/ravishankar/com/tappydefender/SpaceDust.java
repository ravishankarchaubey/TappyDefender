package ravishankar.com.tappydefender;

import java.util.Random;

/**
 * Created by RAVISHANKAR CHAUBEY on 17-06-2016.
 */
public class SpaceDust {
    public SpaceDust(int screenX, int screenY){
        maxX=screenX;
        maxY=screenY;
        minX=0;
        minY=0;
        Random generator=new Random();
        speed=generator.nextInt(10);
        x=generator.nextInt(maxX);
        y=generator.nextInt(maxY);

    }
    private int x,y;
    private int speed;
    private int minX;
    private int maxX;
    private int minY;
    private int maxY;

    public void update(int playerspeed){
        x-=playerspeed;
        x-=speed;

        if(x<0){
            x=maxX;
            Random generator=new Random();
            y=generator.nextInt(maxY);
            speed=generator.nextInt(15);
        }
    }

    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
}
