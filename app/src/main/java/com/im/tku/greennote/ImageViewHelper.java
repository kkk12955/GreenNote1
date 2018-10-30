package com.im.tku.greennote;

import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.widget.ImageButton;
import android.widget.ImageView;

/**
 * Created by B310 on 2017/8/28.
 */

public class ImageViewHelper {
    private final DisplayMetrics dm;
    private final ImageView imageView;
    private final ImageButton zoomInButton;
    private final ImageButton zoomOutButton;
    private final Bitmap bitmap;

    public ImageViewHelper(DisplayMetrics dm, ImageView imageView, Bitmap bitmap, ImageButton zoomInButton, ImageButton zoomOutButton){
        this.dm = dm;
        this.imageView = imageView;
        this.zoomInButton = zoomInButton;
        this.zoomOutButton = zoomOutButton;
        this.bitmap = bitmap;
    }
    
}
