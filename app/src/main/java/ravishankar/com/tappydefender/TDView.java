package ravishankar.com.tappydefender;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by RAVISHANKAR CHAUBEY on 16-06-2016.
 */
public class TDView extends SurfaceView implements Runnable {
    public TDView(Context context,int x,int y) {
        super(context);
        this.context=context;

        soundPool=new SoundPool(10, AudioManager.STREAM_MUSIC,0);
        try{
            AssetManager assetManager=context.getAssets();
            AssetFileDescriptor descriptor;
            descriptor=assetManager.openFd("start.ogg");
            start=soundPool.load(descriptor, 0);
            descriptor=assetManager.openFd("win.ogg");
            win=soundPool.load(descriptor, 0);
            descriptor=assetManager.openFd("bump.ogg");
            bump=soundPool.load(descriptor, 0);
            descriptor=assetManager.openFd("destroyed.ogg");
            destroyed=soundPool.load(descriptor,0);
        }
        catch (IOException e){
            Log.e("error", "failed to load sound files");
        }
        prefs=context.getSharedPreferences("HiScores",Context.MODE_PRIVATE);
        editor=prefs.edit();
        fastestTime=prefs.getLong("fastestTime",1000000);
        ourholder=getHolder();
        paint=new Paint();
        screenX=x;
        screenY=y;
        /*player=new PlayerShip(context,x,y);
        enemy1=new EnemyShip(context,x,y);
        enemy2=new EnemyShip(context,x,y);
        enemy3=new EnemyShip(context,x,y);
        int numSpecs=50;
        for(int i=0;i<numSpecs;i++){
            SpaceDust spec=new SpaceDust(x,y);
            dustList.add(spec);
        }*/
        startGame();
    }
    private SoundPool soundPool;
    int start=-1;
    int bump=-1;
    int destroyed=-1;
    int win=-1;

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    private Context context;
    private int screenX;
    private int screenY;
    private PlayerShip player;
    public EnemyShip enemy1;
    public EnemyShip enemy2;
    public EnemyShip enemy3;
    //public EnemyShip enemy4;
    //public EnemyShip enemy5;
    public ArrayList<SpaceDust> dustList=new ArrayList<SpaceDust>();
    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder ourholder;
    volatile boolean playing;
    private float distanceRemaining;
    private long timeTaken;
    private long timeStarted;
    private long fastestTime;
    private boolean gameEnded;


    Thread gameThread=null;
    @Override
    public void run() {
        while (playing){
            update();
            draw();
            control();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent){
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK){
            case MotionEvent.ACTION_UP:
                player.stopBoosting();
                break;
            case MotionEvent.ACTION_DOWN:
                player.setBoosting();
                if(gameEnded){
                    startGame();
                }
                break;
        }
        return true;
    }
    private void draw() {
        if(ourholder.getSurface().isValid()){
            canvas=ourholder.lockCanvas();
            canvas.drawColor(Color.argb(255,0,0,0));
            paint.setColor(Color.argb(255, 255, 255, 255));
            /*Draw HitBoxes ------ showing white boxes around ships*/
            //canvas.drawRect(player.getHitBox().left,player.getHitBox().top,player.getHitBox().right,player.getHitBox().bottom,paint);
            //canvas.drawRect(enemy1.getHitBox().left,enemy1.getHitBox().top,enemy1.getHitBox().right,enemy1.getHitBox().bottom,paint);
            //canvas.drawRect(enemy2.getHitBox().left,enemy2.getHitBox().top,enemy2.getHitBox().right,enemy2.getHitBox().bottom,paint);
            //canvas.drawRect(enemy3.getHitBox().left,enemy3.getHitBox().top,enemy3.getHitBox().right,enemy3.getHitBox().bottom,paint);
            for(SpaceDust sd:dustList){
                canvas.drawPoint(sd.getX(),sd.getY(),paint);
            }
            canvas.drawBitmap(player.getBitmap(), player.getX(), player.getY(), paint);
            canvas.drawBitmap(enemy1.getBitmap(), enemy1.getX(), enemy1.getY(), paint);
            canvas.drawBitmap(enemy2.getBitmap(), enemy2.getX(), enemy2.getY(), paint);
            canvas.drawBitmap(enemy3.getBitmap(), enemy3.getX(), enemy3.getY(), paint);

            /*if(screenX > 1000){
                canvas.drawBitmap(enemy4.getBitmap(),enemy4.getX(),enemy4.getY(),paint);
            }
            if(screenX > 1200){
                canvas.drawBitmap(enemy5.getBitmap(),enemy5.getX(),enemy5.getY(),paint);
            }*/

            if(!gameEnded) {
                paint.setTextAlign(Paint.Align.LEFT);
                paint.setColor(Color.argb(255, 255, 255, 255));
                paint.setTextSize(25);
                //canvas.drawText("Fastest:" +fastestTime + " s", 10, 20, paint);
                canvas.drawText("Fastest:"+formatTime(fastestTime)+" s",10,20,paint);
                //canvas.drawText("Time:"+timeTaken+" s",screenX/2,20,paint);
                canvas.drawText("Time:"+formatTime(timeTaken)+" s",screenX/2,20,paint);
                canvas.drawText("Distance:"+distanceRemaining/1000+" KM",screenX/3,screenY-20,paint);
                canvas.drawText("Shield:"+player.getShieldStrength(),10,screenY-20,paint);
                canvas.drawText("Speed:"+player.getSpeed()*60+" MPS",(screenX/3)*2,screenY-20,paint);
            }
            else {
                paint.setTextSize(80);
                paint.setTextAlign(Paint.Align.CENTER);
                canvas.drawText("Game Over!!!", screenX / 2, 100, paint);
                paint.setTextSize(25);
                //canvas.drawText("Fastest:"+fastestTime+" s",screenX/2,160,paint);
                canvas.drawText("Fastest:"+formatTime(fastestTime)+" s",screenX/2,160,paint);
                //canvas.drawText("Time:" +timeTaken+" s",screenX/2,200,paint);
                canvas.drawText("Time:" +formatTime(timeTaken)+" s",screenX/2,200,paint);
                canvas.drawText("Distance remaining:" + distanceRemaining / 1000 + " KM", screenX / 2, 240, paint);
                paint.setTextSize(80);
                canvas.drawText("Tap to Replay!", screenX / 2, 350, paint);
            }

            ourholder.unlockCanvasAndPost(canvas);
        }
    }

    private void control() {
        try {
            gameThread.sleep(17);
        }
        catch (InterruptedException e){
        }
    }

    private void update() {
        boolean hitDetected=false;
        if(Rect.intersects(player.getHitBox(),enemy1.getHitBox())){
            hitDetected=true;
            enemy1.setX(-100);
        }
        if(Rect.intersects(player.getHitBox(),enemy2.getHitBox())){
            hitDetected=true;
            enemy2.setX(-100);
        }
        if(Rect.intersects(player.getHitBox(),enemy3.getHitBox())){
            hitDetected=true;
            enemy3.setX(-100);
        }

        /*if(screenX > 1000){
            if(Rect.intersects(player.getHitBox(), enemy4.getHitBox())){
                hitDetected = true;
                enemy4.setX(-100);
            }
        }
        if(screenX > 1200){
            if(Rect.intersects(player.getHitBox(), enemy5.getHitBox())){
                hitDetected = true;
                enemy5.setX(-100);
            }
        }*/


        if(hitDetected){
            soundPool.play(bump,1,1,0,0,1);
            player.reduceStrength();
            if(player.getShieldStrength()<0){
                //Game Over
                soundPool.play(destroyed,1,1,0,0,1);
                gameEnded=true;
            }
        }

        player.update();
        enemy1.update(player.getSpeed());
        enemy2.update(player.getSpeed());
        enemy3.update(player.getSpeed());

        /*if(screenX > 1000) {
            enemy4.update(player.getSpeed());
        }
        if(screenX > 1200) {
            enemy5.update(player.getSpeed());
        }*/

        for(SpaceDust sd:dustList){
            sd.update(player.getSpeed());
        }

        if(!gameEnded){
            distanceRemaining-=player.getSpeed();
            timeTaken=System.currentTimeMillis()-timeStarted;
        }

        if(distanceRemaining<0){
            soundPool.play(win,1,1,0,0,1);
            if(timeTaken<fastestTime){
                editor.putLong("fastestTime",timeTaken);
                editor.commit();
                fastestTime=timeTaken;
            }
            distanceRemaining=0;
            gameEnded=true;
        }
    }

    public void pause() {
        playing = false;
        try {
            gameThread.join();
        }
        catch (InterruptedException e) {
        }
    }

    public void resume(){
        playing=true;
        gameThread=new Thread(this);
        gameThread.start();
    }

    private void startGame(){
        soundPool.play(start,1,1,0,0,1);
        player=new PlayerShip(context,screenX,screenY);
        enemy1=new EnemyShip(context,screenX,screenY);
        enemy2=new EnemyShip(context,screenX,screenY);
        enemy3=new EnemyShip(context,screenX,screenY);

        /*if(screenX>1000){
            enemy4=new EnemyShip(context,screenX,screenY);
        }
        if(screenX>1200){
            enemy5=new EnemyShip(context,screenX,screenY);
        }*/
        int numSpecs=50;
        for(int i=0;i<numSpecs;i++){
            SpaceDust spec=new SpaceDust(screenX,screenY);
            dustList.add(spec);
        }
        distanceRemaining=10000;
        timeTaken=0;

        timeStarted=System.currentTimeMillis();
        gameEnded=false;
    }

    private String formatTime(long time){
        long seconds=(time)/1000;
        long thousandths=(time)-(seconds*1000);
        String strThousandths=""+thousandths;
        if(thousandths<100){
            strThousandths="0"+thousandths;
        }
        if(thousandths<10){
            strThousandths="0"+strThousandths;
        }
        String stringTime=""+seconds+"."+strThousandths;
        return stringTime;
    }

}
