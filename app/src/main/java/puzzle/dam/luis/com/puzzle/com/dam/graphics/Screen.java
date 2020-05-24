package puzzle.dam.luis.com.puzzle.com.dam.graphics;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

/**
 * Created by Luis on 30/04/2017.
 */

public abstract class Screen extends SurfaceView implements SurfaceHolder.Callback {

    private int screenWidth;
    private int screenHeight;
    private int worldWidth;
    private int worldHeight;

    private float scaleX;
    private float scaleY;

    private SurfaceHolder surfaceHolder;
    private Bitmap buffer;
    private Canvas bufferCanvas;
    private DisplayMetrics displayMetrics;

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB_MR2)
    public Screen(Activity context, int worldWidth, int worldHeight) {
        super(context);
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        this.buffer = Bitmap.createBitmap(worldWidth, worldHeight, Bitmap.Config.RGB_565);
        this.bufferCanvas = new Canvas(buffer);

        this.hideSystemUI(context);

        this.screenWidth = this.displayMetrics.widthPixels;
        this.screenHeight = this.displayMetrics.heightPixels;

        this.scaleX = (float)worldWidth  / (float)this.screenWidth;
        this.scaleY = (float)worldHeight / (float)this.screenHeight;

        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        setFocusable(true);
        setFocusableInTouchMode(true);

        setLayoutParams(new ActionBar.LayoutParams(screenWidth, screenHeight));
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        this.surfaceHolder = holder;
        try{
            this.surfaceHolder.setFixedSize(this.screenWidth, this.screenHeight);
            //Mejora la calidad de los colores. Probarlo con cuidado.
            this.surfaceHolder.setFormat(PixelFormat.RGBA_8888);
        }
        catch (IllegalArgumentException e) {}
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {}

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

        if(surfaceHolder != null && surfaceHolder.getSurface().isValid()){
            try {
                //gestureEvent.cancel();
                Canvas canvas = surfaceHolder.lockCanvas();
                //Construye un rect del tamanyo del surfaceView
                Rect dstRect = new Rect();
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

    private void hideSystemUI(Activity activity){
        //Hide systemUI
        int androidApiVersion = android.os.Build.VERSION.SDK_INT;
        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        // This work only for android 4.4+
        if (androidApiVersion >= Build.VERSION_CODES.KITKAT) {

            activity.getWindow().getDecorView().setSystemUiVisibility(flags);

            // Code below is to handle presses of Volume up or Volume down.
            // Without this, after pressing volume buttons, the navigation bar will
            // show up and won't hide
            final View decorView = activity.getWindow().getDecorView();
            decorView
                    .setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                        @Override
                        public void onSystemUiVisibilityChange(int visibility) {
                            if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                                decorView.setSystemUiVisibility(flags);
                            }
                        }
                    });
        }

        this.displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(this.displayMetrics);


        //A partir de KITKAT es posible remover la barra de menu. Uso reflexion para saber si la clase contiene el metodo 'getRealMetrics'
        if (androidApiVersion < Build.VERSION_CODES.O) {
            try {
                android.view.Display.class.getMethod("getRealMetrics", DisplayMetrics.class).invoke(activity.getWindowManager().getDefaultDisplay(), this.displayMetrics);
            } catch (Exception e) {
                activity.getWindowManager().getDefaultDisplay().getMetrics(this.displayMetrics);
            }
        }
    }

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


    private float x;
    private float y;

    private boolean isTouching;
    private boolean isTouchDown;
    private boolean isTouchUp;
    private boolean isTouchDrag;

    public int getTouchX() {
        return (int)(x * getScaleX());
    }

    public int getTouchY() {
        return (int)(y * getScaleY());
    }

    public boolean isTouching() {
        return isTouching;
    }

    public boolean isTouchDown() {
        return isTouchDown;
    }

    public boolean isTouchUp() {
        return isTouchUp;
    }

    public boolean isTouchDrag() {
        return isTouchDrag;
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isTouchDown = true;
                isTouchUp = false;
                isTouching = true;
                break;
            case MotionEvent.ACTION_MOVE:
                isTouchUp = false;
                isTouchDown = false;
                isTouchDrag = true;
                isTouching = true;
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                isTouchUp = true;
                isTouchDown = false;
                isTouchDrag = false;
                isTouching = false;
                break;
            default:
                break;
        }
        x = motionEvent.getX();
        y = motionEvent.getY();
        return true;
    }
}
