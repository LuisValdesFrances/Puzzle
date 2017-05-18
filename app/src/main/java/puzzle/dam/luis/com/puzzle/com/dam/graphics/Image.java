package puzzle.dam.luis.com.puzzle.com.dam.graphics;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.io.InputStream;

/**
 * Created by Luis on 02/05/2017.
 */

public class Image {

    private Bitmap bitmap;

    public Image(Bitmap bitmap){
        this.bitmap = bitmap;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public Image(Activity context, String path){
        try {
            InputStream ins = context.getAssets().open(path);
            bitmap = BitmapFactory.decodeStream(ins);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public int getWidth(){
        return (int)(bitmap.getWidth());
    }
    public int getHeight(){
        return (int)(bitmap.getHeight());
    }









}
