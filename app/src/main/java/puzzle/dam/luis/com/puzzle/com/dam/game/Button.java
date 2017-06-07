package puzzle.dam.luis.com.puzzle.com.dam.game;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import puzzle.dam.luis.com.puzzle.com.dam.graphics.Image;
import puzzle.dam.luis.com.puzzle.com.dam.graphics.Screen;

/**
 * Created by Luis on 07/05/2017.
 */

public class Button {

    private int x;
    private int y;
    private int width;
    private int height;

    private Image imgIdle;
    private Image imgHover;

    private int state;
    private static final int STATE_IDLE = 0;
    private static final int STATE_HOVER = 1;

    public Button(int x, int y, Image imgIdle, Image imgHover) {
        this.x = x;
        this.y = y;
        this.imgIdle = imgIdle;
        this.imgHover = imgHover;
        width = imgIdle.getWidth();
        height = imgIdle.getHeight();
    }

    public void update(Screen screen){
        switch(state){
            case STATE_IDLE:
                if(screen.isTouchDown()){
                    if(isToching(screen.getTouchX(), screen.getTouchY())){
                        state = STATE_HOVER;
                    }
                }
                break;
            case STATE_HOVER:
                if(isToching(screen.getTouchX(), screen.getTouchY())) {
                    if (screen.isTouchUp()) {
                        state = STATE_IDLE;
                        callback();
                        return;
                    }
                }else{
                    state = STATE_IDLE;
                }
                break;
        }
    }

    public void callback(){

    }

    private boolean isToching(int tX, int tY){
        return (tX > x && tX < x + width && tY > y && tY < y + height);
    }

    public void draw(Canvas canvas, Paint paint) {
        switch(state){
            case STATE_IDLE:
                canvas.drawBitmap(imgIdle.getBitmap(), x, y, paint);
                break;
            case STATE_HOVER:
                if(imgHover != null) {
                    canvas.drawBitmap(imgHover.getBitmap(), x, y, paint);
                }else {
                    Matrix matrix = new Matrix();
                    matrix.setTranslate(x+(getWidth()*0.1f), y+(getHeight()*0.1f));
                    matrix.preScale(1.2f, 1.2f, getWidth(), getHeight());
                    canvas.drawBitmap(imgIdle.getBitmap(), matrix, paint);
                }
                break;
        }
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setH(int height) {
        this.height = height;
    }
}
