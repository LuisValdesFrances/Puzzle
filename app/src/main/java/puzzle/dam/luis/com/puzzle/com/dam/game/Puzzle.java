package puzzle.dam.luis.com.puzzle.com.dam.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextPaint;

import puzzle.dam.luis.com.puzzle.MainActivity;
import puzzle.dam.luis.com.puzzle.R;
import puzzle.dam.luis.com.puzzle.com.dam.graphics.Font;
import puzzle.dam.luis.com.puzzle.com.dam.graphics.Image;
import puzzle.dam.luis.com.puzzle.com.dam.graphics.Screen;
import puzzle.dam.luis.com.puzzle.com.dam.sound.SoundManager;

/**
 * Created by Luis on 30/04/2017.
 */

public class Puzzle extends Screen implements Runnable{

    //Configuracion del juego
    public static final int WORLD_WIDTH = 480;
    public static final int WORLD_HEIGHT = 800;

    private MainActivity mainActivity;
    public MainActivity getMainActivity(){
        return  mainActivity;
    }
    private SoundManager soundManager;

    private boolean debug=false;
    private boolean sound=true;
    private int tick;
    private float acumDelta;
    private int seconds;

    private int gameState;
    private int lastGameState;
    private static final int STATE_MAIN = 0;
    private static final int STATE_OPTIONS = 1;
    private static final int STATE_SELECT_PICTURE = 2;
    private static final int STATE_SELECT_DIF = 3;
    private static final int STATE_PLAY = 4;
    private static final int STATE_END = 5;

    //Imagenes
    private Image bgMenu;

    //Objetos del juego
    private Font font;
    private Font fontSmall;
    private Button playButton;
    private Button optionsButton;
    private String pictureSelect;
    private int pieces;
    private float distAnimation;

    private Button onSoundButton;
    private Button offSoundButton;
    private Button onDebugButton;
    private Button offDebugButton;
    private Button plusButton;
    private Button restButton;
    private Button backButton;
    private Button nextButton;
    private Button img1Button;
    private Button img2Button;
    private Button img3Button;
    private PuzzleImage puzzleImage;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public Puzzle(MainActivity activity) {
        super(activity, WORLD_WIDTH, WORLD_HEIGHT);
        this.mainActivity = activity;
        soundManager = new SoundManager(activity);
        soundManager.loadFX();
        //Cargo la fuente
        font = new Font(activity, "drawable/UncialAntiqua-Regular.ttf", Color.RED, Color.WHITE, 56f, getWorldMidle()*0.004f);
        fontSmall = new Font(activity, "drawable/UncialAntiqua-Regular.ttf", Color.RED, Color.WHITE, 28f, getWorldMidle()*0.002f);
        lastGameState = -1;
        changeState(STATE_MAIN);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void run(){
        while(true) {
            long initTime = System.currentTimeMillis();
            update();
            draw();
            setFrameRate(initTime);
            tick++;
        delta = (System.currentTimeMillis() - initTime) / 1000f;//Quiero en delta en segundos
            calculateFPS(delta);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected void initState(){
        switch(gameState) {
            case STATE_MAIN:
                mainActivity.loadInterstitial();
                //Carga de imagenes
                Image imgBack = new Image(mainActivity, "drawable/button_back.png");
                Image imgPlayIdle = new Image(mainActivity, "drawable/button_play.png");
                Image imgPlayHover = new Image(mainActivity, "drawable/button_play_h.png");
                Image imgOptIdle = new Image(mainActivity, "drawable/button_options.png");

                //instacia de objetos persistentes en todo el juego
                bgMenu = new Image(mainActivity, "drawable/bg.png");
                backButton = new Button(
                        getWorldMidle()/64,
                        WORLD_HEIGHT - imgBack.getHeight() - getWorldMidle()/64,
                        imgBack, null){
                    @Override
                    public void callback(){
                        changeState(lastGameState);
                    }
                };
                optionsButton = new Button(
                        WORLD_WIDTH - imgOptIdle.getWidth() - (getWorldMidle() / 64), (getWorldMidle() / 64),
                        imgOptIdle, null) {
                    @Override
                    public void callback() {
                        changeState(STATE_OPTIONS);
                    }
                };
                playButton = new Button(
                        WORLD_WIDTH/2-imgPlayIdle.getWidth()/2,
                        WORLD_HEIGHT/2-imgPlayIdle.getHeight()/2,
                        imgPlayIdle, imgPlayHover){
                    @Override
                    public void callback(){
                        changeState(STATE_SELECT_PICTURE);
                    }
                };

                soundManager.playMusic(SoundManager.MUSIC_MENU, true, 0);
                break;
            case STATE_OPTIONS:

                Image imgOn = new Image(mainActivity, "drawable/button_on.png");
                Image imgOnHover = new Image(mainActivity, "drawable/button_on_h.png");
                Image imgOff = new Image(mainActivity, "drawable/button_off.png");
                Image imgOffHover = new Image(mainActivity, "drawable/button_off_h.png");
                Image imgPlus = new Image(mainActivity, "drawable/button_plus.png");
                Image imgRest = new Image(mainActivity, "drawable/button_rest.png");

                int optHeight = imgOn.getHeight();

                onSoundButton = new Button(
                        WORLD_WIDTH/2 + WORLD_WIDTH/8,
                        WORLD_HEIGHT/4 + optHeight,
                        imgOn, imgOnHover){
                    @Override
                    public void callback(){
                        sound = !sound;
                        soundManager.setMusic(sound);
                        soundManager.setFX(sound);
                        soundManager.playMusic(
                                lastGameState == STATE_PLAY ? SoundManager.MUSIC_PLAY : SoundManager.MUSIC_MENU, true, 0);
                    }
                };
                offSoundButton = new Button(
                        WORLD_WIDTH/2 + WORLD_WIDTH/8,
                        WORLD_HEIGHT/4 + optHeight,
                        imgOff, imgOffHover){
                    @Override
                    public void callback(){
                        soundManager.playFX(SoundManager.FX_BUTTON);
                        sound = !sound;
                        soundManager.setMusic(sound);
                        soundManager.setFX(sound);
                        soundManager.stopMusic();
                    }
                };
                onDebugButton = new Button(
                        WORLD_WIDTH/2 + WORLD_WIDTH/8,
                        WORLD_HEIGHT/4 + optHeight*2 + (int)(optHeight*0.5f),
                        imgOn, imgOnHover){
                    @Override
                    public void callback(){
                        soundManager.playFX(SoundManager.FX_BUTTON);
                        debug = !debug;
                    }
                };
                offDebugButton = new Button(
                        WORLD_WIDTH/2 + WORLD_WIDTH/8,
                        WORLD_HEIGHT/4 + optHeight*2 + (int)(optHeight*0.5f),
                        imgOff, imgOffHover){
                    @Override
                    public void callback(){
                        soundManager.playFX(SoundManager.FX_BUTTON);
                        debug = !debug;
                    }
                };
                restButton = new Button(
                        WORLD_WIDTH/2 + WORLD_WIDTH/8 - WORLD_WIDTH/6,
                        WORLD_HEIGHT/4 + optHeight*4 + (int)(optHeight*0.5f),
                        imgRest, null){
                    @Override
                    public void callback(){
                        framesXSecond = framesXSecond /2;
                        if(framesXSecond < 15) framesXSecond = 15;
                        soundManager.playFX(SoundManager.FX_BUTTON);
                    }
                };
                plusButton = new Button(
                        WORLD_WIDTH/2 + WORLD_WIDTH/8 + WORLD_WIDTH/6 + (imgOn.getWidth()-imgPlus.getWidth()),
                        WORLD_HEIGHT/4 + optHeight*4 + (int)(optHeight*0.5f),
                        imgPlus, null){
                    @Override
                    public void callback(){
                        framesXSecond = framesXSecond *2;
                        if(framesXSecond > 60) framesXSecond = 60;
                        soundManager.playFX(SoundManager.FX_BUTTON);
                    }
                };
                break;
            case STATE_SELECT_PICTURE:
                Image img1 = new Image(mainActivity, "drawable/min1.png");
                Image img2 = new Image(mainActivity, "drawable/min2.png");
                Image img3 = new Image(mainActivity, "drawable/min3.png");
                img1Button = new Button(
                        WORLD_WIDTH/2 - img1.getWidth()/2,
                        (int)(WORLD_HEIGHT * 0.25f) - img1.getHeight()/2,
                        img1, null){
                    @Override
                    public void callback(){
                        pictureSelect = "il1.png";
                        changeState(STATE_SELECT_DIF);
                    }
                };
                img2Button = new Button(
                        WORLD_WIDTH/2 - img1.getWidth()/2,
                        (int)(WORLD_HEIGHT * 0.5f) - img1.getHeight()/2,
                        img2, null){
                    @Override
                    public void callback(){
                        pictureSelect = "il2.png";
                        changeState(STATE_SELECT_DIF);
                    }
                };
                img3Button = new Button(
                        WORLD_WIDTH/2 - img1.getWidth()/2,
                        (int)(WORLD_HEIGHT * 0.75f) - img1.getHeight()/2,
                        img3, null){
                    @Override
                    public void callback(){
                        pictureSelect = "il3.png";
                        changeState(STATE_SELECT_DIF);
                    }
                };
                break;
            case STATE_SELECT_DIF:
                imgPlus = new Image(mainActivity, "drawable/button_plus.png");
                imgRest = new Image(mainActivity, "drawable/button_rest.png");
                Image imgNext = new Image(mainActivity, "drawable/button_next.png");
                pieces = 3;
                restButton = new Button(
                        WORLD_WIDTH/3 - imgRest.getWidth()/2,
                        WORLD_HEIGHT/2 + imgRest.getHeight()/2,
                        imgRest, null){
                    @Override
                    public void callback(){
                        pieces = Math.max(pieces-1, 3);
                        soundManager.playFX(SoundManager.FX_BUTTON);
                    }
                };
                plusButton = new Button(
                        WORLD_WIDTH - WORLD_WIDTH/3 - imgRest.getWidth()/2,
                        WORLD_HEIGHT/2 + imgRest.getHeight()/2,
                        imgPlus, null) {
                    @Override
                    public void callback() {
                        pieces = Math.min(pieces+1, 5);
                        soundManager.playFX(SoundManager.FX_BUTTON);
                    }
                };
                nextButton = new Button(
                        WORLD_WIDTH - imgNext.getWidth() - getWorldMidle()/64,
                        WORLD_HEIGHT - imgNext.getHeight() - getWorldMidle()/64,
                        imgNext, null){
                    @Override
                    public void callback(){
                        changeState(STATE_PLAY);
                    }
                };
                break;
            case STATE_PLAY:

                mainActivity.requestInterstitial();

                //Solo instancio los objetos del juego en caso de que no se venga del menú
                if(lastGameState != STATE_OPTIONS) {
                    puzzleImage = new PuzzleImage(this, new Image(mainActivity, "drawable/"+ pictureSelect), this, soundManager, pieces);
                    seconds = 0;
                    acumDelta = 0;
                }
                soundManager.playMusic(SoundManager.MUSIC_PLAY, true, 0);
                break;
            case STATE_END:
                bgMenu = new Image(mainActivity, "drawable/"+ pictureSelect);
                imgNext = new Image(mainActivity, "drawable/button_next.png");
                nextButton = new Button(
                        WORLD_WIDTH - imgNext.getWidth() - getWorldMidle()/64,
                        WORLD_HEIGHT - imgNext.getHeight() - getWorldMidle()/64,
                        imgNext, null){
                    @Override
                    public void callback(){
                        changeState(STATE_MAIN);
                    }
                };
                distAnimation = getWorldMidle()*2;
                soundManager.playMusic(SoundManager.MUSIC_END, false, 0);
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected  void update(){
        switch(gameState){
            case STATE_MAIN:
                playButton.update(this);
                optionsButton.update(this);
                break;
            case STATE_OPTIONS:
                backButton.update(this);
                if(!sound){
                    onSoundButton.update(this);
                }else{
                    offSoundButton.update(this);
                }
                if(debug){
                    onDebugButton.update(this);
                }else{
                    offDebugButton.update(this);
                }
                plusButton.update(this);
                restButton.update(this);
                break;
            case STATE_SELECT_PICTURE:
                backButton.update(this);
                img1Button.update(this);
                img2Button.update(this);
                img3Button.update(this);
                break;
            case STATE_SELECT_DIF:
                backButton.update(this);
                nextButton.update(this);
                plusButton.update(this);
                restButton.update(this);
                break;
            case STATE_PLAY:
                if (!puzzleImage.checkVictory()) {
                    optionsButton.update(this);
                    acumDelta += delta;
                    if (acumDelta >= 1f) {
                        acumDelta = acumDelta - 1f;
                        seconds++;
                    }
                    puzzleImage.update(this, delta);
                } else {
                    changeState(STATE_END);
                }
                break;
            case STATE_END:
                nextButton.update(this);
                if(distAnimation > 0){
                    distAnimation = distAnimation -((distAnimation / 10)*(delta*32))-(1*getFrameModificator());
                }else{
                    distAnimation = 0;
                }
                break;
        }
    }

    @Override
    protected void renderGame(Canvas canvas) {
        Paint paint = new Paint();
        switch(gameState) {
            case STATE_MAIN:
                canvas.drawBitmap(bgMenu.getBitmap(), 0, 0, paint);
                playButton.draw(canvas, paint);
                optionsButton.draw(canvas, paint);
                String title = mainActivity.getString(R.string.app_name);
                int m = font.getTextWidth(paint, title);
                font.draw(canvas, paint, title,
                        0,
                        Puzzle.WORLD_HEIGHT - font.getTextHeight(paint)/2);
                break;
            case STATE_OPTIONS:
                canvas.drawBitmap(bgMenu.getBitmap(), 0, 0, paint);
                if(lastGameState == STATE_PLAY && (tick%(50*getFrameModificator()) < (25*getFrameModificator()))){
                    String text = mainActivity.getString(R.string.pause);
                    font.draw(canvas,paint, text, (getWorldMidle()>>6), font.getTextHeight(paint) + (getWorldMidle()>>6));
                }
                backButton.draw(canvas, paint);
                if(sound){
                    onSoundButton.draw(canvas, paint);
                }else{
                    offSoundButton.draw(canvas, paint);
                }
                if(debug){
                    onDebugButton.draw(canvas, paint);
                }else{
                    offDebugButton.draw(canvas, paint);
                }
                plusButton.draw(canvas, paint);
                restButton.draw(canvas, paint);

                String text = mainActivity.getString(R.string.sound);
                fontSmall.draw(canvas, paint, text,
                        WORLD_WIDTH/16,
                        onSoundButton.getY() + onSoundButton.getHeight()/2 + fontSmall.getTextHeight(paint)/2);

                text = mainActivity.getString(R.string.debug);
                fontSmall.draw(canvas, paint, text,
                        WORLD_WIDTH/16,
                        onDebugButton.getY() + onDebugButton.getHeight()/2 + fontSmall.getTextHeight(paint)/2);

                text = mainActivity.getString(R.string.fps);
                fontSmall.draw(canvas, paint, text,
                        WORLD_WIDTH/16,
                        plusButton.getY() + plusButton.getHeight()/2 + fontSmall.getTextHeight(paint)/2);

                text = ""+ framesXSecond;
                fontSmall.draw(canvas, paint, text,
                        WORLD_WIDTH/2 + WORLD_WIDTH/8+onSoundButton.getWidth()/2 -fontSmall.getTextWidth(paint, text)/2,
                        plusButton.getY() + plusButton.getHeight()/2 + fontSmall.getTextHeight(paint)/2);
                break;
            case STATE_SELECT_PICTURE:
                canvas.drawBitmap(bgMenu.getBitmap(), 0, 0, paint);
                text = mainActivity.getString(R.string.select_picture);
                fontSmall.draw(canvas,paint, text, (getWorldMidle()>>6), font.getTextHeight(paint) + (getWorldMidle()>>6));
                backButton.draw(canvas, paint);
                img1Button.draw(canvas, paint);
                img2Button.draw(canvas, paint);
                img3Button.draw(canvas, paint);
                break;
            case STATE_SELECT_DIF:
                canvas.drawBitmap(bgMenu.getBitmap(), 0, 0, paint);
                text = mainActivity.getString(R.string.select_difficult);
                fontSmall.draw(canvas,paint, text, (getWorldMidle()>>6), font.getTextHeight(paint) + (getWorldMidle()>>6));
                backButton.draw(canvas, paint);
                nextButton.draw(canvas, paint);
                plusButton.draw(canvas, paint);
                restButton.draw(canvas, paint);
                text = pieces + "x" + pieces;
                fontSmall.draw(canvas, paint, text,
                        WORLD_WIDTH/2 -fontSmall.getTextWidth(paint, text)/2,
                        plusButton.getY() + fontSmall.getTextHeight(paint)/2);
                break;
            case STATE_PLAY:
                paint.setColor(0xff000000);
                canvas.drawRect(new Rect(0, 0, Puzzle.WORLD_WIDTH, Puzzle.WORLD_HEIGHT), paint);
                optionsButton.draw(canvas, paint);
                puzzleImage.draw(this, canvas, paint);
                text = ""+puzzleImage.movements;
                font.draw(canvas, paint, text,
                        getWorldMidle()>>8,
                        font.getTextHeight(paint) + (getWorldMidle()>>8));
                break;
            case STATE_END:
                paint.setColor(0xff000000);
                canvas.drawRect(new Rect(0, 0, Puzzle.WORLD_WIDTH, Puzzle.WORLD_HEIGHT), paint);
                canvas.drawBitmap(bgMenu.getBitmap(),
                        WORLD_WIDTH/2 - bgMenu.getWidth()/2, WORLD_HEIGHT/ 2- bgMenu.getHeight()/2, paint);
                nextButton.draw(canvas, paint);
                text = mainActivity.getString(R.string.puzzle);
                font.draw(canvas, paint, text,
                        WORLD_WIDTH/2 - font.getTextWidth(paint, text)/2 - (int)distAnimation,
                        (int)(font.getTextHeight(paint)*1.5) + (getWorldMidle()>>8));
                text = mainActivity.getString(R.string.completed);
                font.draw(canvas, paint, text,
                        WORLD_WIDTH/2 - font.getTextWidth(paint, text)/2 + (int)distAnimation,
                        (int)(font.getTextHeight(paint)*3.5) + (getWorldMidle()>>8));

                fontSmall.draw(canvas, paint, mainActivity.getString(R.string.movements),
                        WORLD_WIDTH/6,
                        WORLD_HEIGHT/2);
                text = "" + puzzleImage.movements;
                fontSmall.draw(canvas, paint, text,
                        WORLD_WIDTH - WORLD_WIDTH/6 - fontSmall.getTextWidth(paint, text),
                        WORLD_HEIGHT/2);
                fontSmall.draw(canvas, paint, mainActivity.getString(R.string.time),
                        WORLD_WIDTH/6,
                        WORLD_HEIGHT/2 + (int)(fontSmall.getTextHeight(paint)*2f));
                text = "" + seconds;
                fontSmall.draw(canvas, paint, text,
                        WORLD_WIDTH - WORLD_WIDTH/6 - fontSmall.getTextWidth(paint, text),
                        WORLD_HEIGHT/2 + (int)(fontSmall.getTextHeight(paint)*2f));

                break;
        }

        if(debug) {
            TextPaint textPaint = new TextPaint();
            textPaint.setAntiAlias(true);
            textPaint.setTextSize(24);
            textPaint.setColor(Color.RED);

            //Tamaño de pantalla y tamaño de mundo
            canvas.drawText("Screen: " + (int) getScreenWidth() + "x" + (int) getScreenHeight(), 0, textPaint.getTextSize(), textPaint);
            canvas.drawText("World: " + WORLD_WIDTH + "x" + WORLD_HEIGHT, WORLD_WIDTH * 0.5f, textPaint.getTextSize(), textPaint);
            //Velocidad de refresco
            canvas.drawText("PFS: " + framesXSecond + "/" + fps, 0, textPaint.getTextSize() * 2, textPaint);
            canvas.drawText("Delta: " + delta, WORLD_WIDTH * 0.5f, textPaint.getTextSize() * 2, textPaint);
            //Cordenadas de inputs
            canvas.drawText("sX: " + (int) (getTouchX() * getScaleX()), 0, textPaint.getTextSize() * 3, textPaint);
            canvas.drawText("sY: " + (int) (getTouchY() * getScaleY()), WORLD_WIDTH * 0.2f, textPaint.getTextSize() * 3, textPaint);
            canvas.drawText("wX: " + (int) getTouchX(), WORLD_WIDTH * 0.5f, textPaint.getTextSize() * 3, textPaint);
            canvas.drawText("wY: " + (int) getTouchY(), WORLD_WIDTH * 0.7f, textPaint.getTextSize() * 3, textPaint);
            //Estados de inputs
            canvas.drawText("Touching: " + isTouching(), 0, textPaint.getTextSize() * 4, textPaint);
            canvas.drawText("EventDown: " + isTouchDown(), 0, textPaint.getTextSize() * 5, textPaint);
            canvas.drawText("EventDrag: " + isTouchDrag(), 0, textPaint.getTextSize() * 6, textPaint);
            canvas.drawText("EventUp: " + isTouchUp(), 0, textPaint.getTextSize() * 7, textPaint);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private boolean changeState(int newState){
        if(gameState != STATE_END && lastGameState != -1){
            soundManager.playFX(SoundManager.FX_BUTTON);
        }
        lastGameState = gameState;
        gameState = newState;
        initState();

        return true;
    }

    /*
    Controla que el framerate del juego sea estable, en este caso, lo defino a 30 fps por segundo.
     */
    public static int framesXSecond = 60;
    public static int fps;
    public static float delta;
    private int frame;
    private long acTime;
    private void calculateFPS(float delta){
        acTime += delta*1000f;
        if(acTime >= 1000){
            acTime = acTime-1000;
            fps = frame;
            frame = 0;
        }
        frame++;
    }

    private void setFrameRate(long initTime){
        long delta = System.currentTimeMillis()-initTime;
        long targetDelta = (long)(1000f / framesXSecond);

        if(targetDelta > delta){
            long dif = targetDelta-delta;
            try {
                Thread.sleep(dif);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        /*
        while (System.currentTimeMillis() - initTime < targetDelta)
            Thread.yield();
        */
    }

    public float getFrameModificator(){
        return (framesXSecond / 60f);
    }


}
