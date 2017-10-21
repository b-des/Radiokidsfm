package fm.radiokids.models;

import android.graphics.Bitmap;

import com.bumptech.glide.load.engine.Resource;

public class SmileyItem {
    private Bitmap image;
    private String code;
    private int i;

    public SmileyItem(Bitmap image, String code/*, Integer i*/) {
        super();
        this.image = image;
        this.code = code;
        //this.i = i;
    }

    public Bitmap getImage() {
        return image;
    }

    public int getRes() {
        return i;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}