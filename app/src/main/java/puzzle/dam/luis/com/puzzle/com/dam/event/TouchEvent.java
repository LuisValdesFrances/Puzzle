package puzzle.dam.luis.com.puzzle.com.dam.event;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import puzzle.dam.luis.com.puzzle.com.dam.graphics.Screen;

/**
 * Created by Luis on 01/05/2017.
 */

public class TouchEvent implements View.OnTouchListener {

    private float x;
    private float y;

    private boolean isTouching;
    private boolean isTouchDown;
    private boolean isTouchUp;
    private boolean isTouchDrag;
    private Screen screen;

    public TouchEvent(Screen screen){
        this.screen = screen;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

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

    public int getX() {
        return (int)(x/screen.getScaleX());
    }

    public int getY() {
        return (int)(y/screen.getScaleY());
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

    public void cancel(){
        isTouchUp = false;
        isTouchDown = false;
    }
}
