package puzzle.dam.luis.com.puzzle.com.dam.graphics;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import puzzle.dam.luis.com.puzzle.com.dam.event.TouchEvent;

/**
 * Created by Luis on 30/04/2017.
 */

public abstract class Screen extends SurfaceView {

    private float screenWidth;
    private float screenHeight;
    private int worldWidth;
    private int worldHeight;

    private float scaleX;
    private float scaleY;

    private SurfaceHolder surfaceHolder;
    private Rect dstRect;
    private Bitmap buffer;
    private Canvas bufferCanvas;

    private TouchEvent touchEvent;

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB_MR2)
    public Screen(Activity context, int worldWidth, int worldHeight) {
        super(context);
        this.surfaceHolder = getHolder();
        this.buffer = Bitmap.createBitmap(worldWidth, worldHeight, Bitmap.Config.RGB_565);
        this.bufferCanvas = new Canvas(buffer);
        this.dstRect = new Rect();
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;

        DisplayMetrics dm = new DisplayMetrics();
        //A partir de KITKAT es posible remover la barra de menu. Uso reflexión para saber si la clase contiene el metodo 'getRealMetrics'
        try {
            android.view.Display.class.getMethod("getRealMetrics", DisplayMetrics.class).invoke(context.getWindowManager().getDefaultDisplay(), dm);

        } catch (Exception e) {
            context.getWindowManager().getDefaultDisplay().getMetrics(dm);
        }

        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;

        scaleX = screenWidth / worldWidth;
        scaleY = screenHeight / worldHeight;

        setLayoutParams(new ActionBar.LayoutParams((int)screenWidth, (int)screenHeight));

        touchEvent = new TouchEvent(this);
    }

    /*
    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        gestureEvent.cancel();
        renderGame(canvas);
        invalidate();
    }
    */
    ///*
    protected void draw(){
        if(surfaceHolder.getSurface().isValid()){
            try {
                //gestureEvent.cancel();
                Canvas canvas = surfaceHolder.lockCanvas();
                //Construye un rect del tamaño del surfaceView
                canvas.getClipBounds(dstRect);
                //Todas las llamadas a paint pintan sobre el bufferCanvas asociado a la bufferImagen
                renderGame(bufferCanvas);
                canvas.drawBitmap(buffer, null, dstRect, null);
                surfaceHolder.unlockCanvasAndPost(canvas);
            }catch(Exception e){}
        }
    }

    /**
     * Pinta en el canvas a resolucion nativa
     * Mas rapido que 'draw' puesto que pinta directamente sobre el canvas del surface
     * y no sobre el bitamp, el cual luego tiene que ser rescalado a las dimensiones de la
     * pantalla.
     */
    protected void nativeDraw(){
        if(surfaceHolder.getSurface().isValid()){
            Canvas canvas = surfaceHolder.lockCanvas();
            renderGame(canvas);
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    protected abstract void renderGame(Canvas canvas);

    public float getScreenWidth() {
        return screenWidth;
    }

    public float getScreenHeight() {
        return screenHeight;
    }

    public float getScaleX() {
        return scaleX;
    }

    public float getScaleY() {
        return scaleY;
    }

    public int getWorldMidle(){
        return (worldWidth+worldHeight)/2;
    }

    public int getWorldWidth() {
        return worldWidth;
    }

    public void setWorldWidth(int worldWidth) {
        this.worldWidth = worldWidth;
    }

    public int getWorldHeight() {
        return worldHeight;
    }

    public void setWorldHeight(int worldHeight) {
        this.worldHeight = worldHeight;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        touchEvent.onTouch(this, event);
        return true;
    }

    public TouchEvent getTouchEvent() {
        return touchEvent;
    }
}
