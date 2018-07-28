package puzzle.dam.luis.com.puzzle.com.dam.graphics;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;

/**
 * Created by Luis on 07/05/2017.
 */

public class Font {

    private Typeface typeFace;
    private float size;
    private float stroke;
    private int color;
    private int strokeColor;

    public Font(Activity context, String path, int color, int strokeColor, float size, float stroke){
        typeFace = Typeface.createFromAsset(context.getAssets(), path);
        this.color = color;
        this.strokeColor = strokeColor;
        this.size = size;
        this.stroke = stroke;
    }

    public void draw(Canvas canvas, Paint paint, String text, int x, int y){
        paint.setTypeface(typeFace);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        paint.setTextSize(size);
        if(stroke>0){
            paint.setColor(strokeColor);
            canvas.drawText(text, x, y, paint);
            paint.setColor(color);
            canvas.drawText(text, x+stroke, y+stroke, paint);
        }else{
            paint.setColor(color);
            canvas.drawText(text, x, y, paint);
        }


    }

    public int getTextWidth(Paint paint, String text){
        paint.setTypeface(typeFace);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        paint.setTextSize(size);
        return (int)paint.measureText(text);
    }

    public int getTextHeight(Paint paint){
        paint.setTypeface(typeFace);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        paint.setTextSize(size);
        return (int)paint.measureText("o");
    }
}
