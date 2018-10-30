package com.im.tku.greennote;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;


public class EditImage extends Activity {

    private String image_path;
    private int w=360,h=438;
    static Bitmap newbm;
    private float mlum;
    static final int FLAG_LUM = 0x1;
    //亮度參數設置
    private SeekBar mSeekbarLum;
    private static int MAX_VALUE = 255;
    private static int MID_VALUE = 127;
    private float  mLum;
    public static ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_image);
        ImageButton cut_bg=(ImageButton) findViewById(R.id.cut_bg);
        ImageButton light=(ImageButton) findViewById(R.id.light);
        ImageButton save=(ImageButton)findViewById(R.id.Save);
        final ImageButton color=(ImageButton) findViewById(R.id.color);

        final String[] back={"白板","黑板"},words={"黑色","白色","紅色","藍色","綠色"};


        final SeekBar slb=(SeekBar) findViewById(R.id.sb);
        slb.setVisibility(SeekBar.INVISIBLE);
        slb.setMax(MAX_VALUE);
        slb.setProgress(MID_VALUE);




        getActionBar().hide(); //隱藏標題

        Bundle b =this.getIntent().getExtras();
        image_path= b.getString("image_path");

        Toast.makeText(EditImage.this, image_path, Toast.LENGTH_SHORT).show();

        imageView = (ImageView) findViewById(R.id.editimgview);
        imageView.setImageDrawable(null);


        final Bitmap bm = BitmapFactory.decodeFile(image_path);
        final int color1 = bm.getPixel(4100, 2300),color2=bm.getPixel(400,300);

        if(bm.getWidth()<=imageView.getWidth()){
            imageView.setImageBitmap(bm);
            newbm=bm;
        }else{
            float scalewidth=((float)w)/bm.getWidth(),scaleheight=((float)h)/bm.getHeight();
            Matrix matrix = new Matrix();
            matrix.postScale(scalewidth, scaleheight);
            newbm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),bm.getHeight(), matrix,true);
            imageView.setImageBitmap(newbm);
        }

        imageView.setVisibility( imageView.VISIBLE);
        cut_bg.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {                                       //去背
                AlertDialog.Builder dialog_list = new AlertDialog.Builder(EditImage.this);
                dialog_list.setTitle("請選擇背景顏色");
                dialog_list.setItems(back, new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which) {
                        if(which==0){
                            newbm=makeTransparent1(newbm, color1,1);
                            imageView.setImageBitmap(newbm);
                        }else{
                            newbm=makeTransparent1(newbm, color2,2);
                            imageView.setImageBitmap(newbm);
                        }
                    }
                });
                dialog_list.show();
            }
        });



        light.setOnClickListener(new View.OnClickListener(){                    //亮度

            @Override
            public void onClick(View v) {                   //亮度
                slb.setVisibility(SeekBar.VISIBLE);
                slb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        mLum = progress*1.0F / MID_VALUE;
                        imageView.setImageBitmap(handleImageEffect(newbm, mLum));
                    }
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {}
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {}
                });}});

        color.setOnClickListener(new View.OnClickListener(){                    //顏色
            @Override
            public void onClick(View v) {                   //顏色
                AlertDialog.Builder dialog_list = new AlertDialog.Builder(EditImage.this);
                dialog_list.setTitle("請選擇字體顏色");
                dialog_list.setItems(words,new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which) {
                        switch(which){
                            case 0:
                                newbm=ChangeColor(newbm,Color.BLACK);
                                imageView.setImageBitmap(newbm);
                                break;
                            case 1:
                                newbm=ChangeColor(newbm,Color.WHITE);
                                imageView.setImageBitmap(newbm);
                                break;
                            case 2:
                                newbm=ChangeColor(newbm,Color.RED);
                                imageView.setImageBitmap(newbm);
                                break;
                            case 3:
                                newbm=ChangeColor(newbm,Color.BLUE);
                                imageView.setImageBitmap(newbm);
                                break;
                            case 4:
                                newbm=ChangeColor(newbm,Color.GREEN);
                                imageView.setImageBitmap(newbm);
                                break;
                        }
                    }
                });dialog_list.show();}});

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(EditImage.this);
                dialog.setTitle("是否確定儲存?");
                dialog.setPositiveButton("是",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Bitmap bm = newbm;
                        bm = zoomBitmap(bm,bm.getWidth(), bm.getHeight());
                        byte[] bytes=Bitmap2Bytes(bm);

                        Bundle bundle = new Bundle();
                        bundle.putByteArray("bitmap", bytes);
                        bundle.putString("TAG","img");
                        bundle.putString("NOTENAME","");

                        Editpage.imgcheck =true;
                        Intent intent = new Intent(EditImage.this,Editpage.class);
                        intent.putExtras(bundle);
                        startActivity(intent);

                    }
                });
                dialog.setNegativeButton("否",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });
                dialog.show();
            }
        });

        imageView.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder alt=new AlertDialog.Builder(EditImage.this);
                alt.setTitle("是否轉向?");
                alt.setPositiveButton("左",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Matrix m=new Matrix();
                        int w=newbm.getWidth(),h=newbm.getHeight();
                        m.setRotate(-90);
                        newbm=Bitmap.createBitmap(newbm,0,0,w,h,m,true);
                        imageView.setImageBitmap(newbm);
                    }
                });
                alt.setNegativeButton("右",new  DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Matrix m=new Matrix();
                        int w=newbm.getWidth(),h=newbm.getHeight();
                        m.setRotate(90);
                        newbm=Bitmap.createBitmap(newbm,0,0,w,h,m,true);
                        imageView.setImageBitmap(newbm);
                    }
                });alt.show();
                return true;
            }});

    }
    public static Bitmap makeTransparent1(Bitmap bit, int transparentColor,int ch) {//去背
        int width =  bit.getWidth();
        int height = bit.getHeight();

        Bitmap myBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        int [] allpixels = new int [ myBitmap.getHeight()*myBitmap.getWidth()];
        bit.getPixels(allpixels, 0, myBitmap.getWidth(), 0, 0, myBitmap.getWidth(),myBitmap.getHeight());
        myBitmap.setPixels(allpixels, 0, width, 0, 0, width, height);

        switch(ch){
            case 1:
                for(int i =0; i<myBitmap.getHeight()*myBitmap.getWidth();i++) {
                    if ( allpixels[i] > transparentColor-3000000 && allpixels[i] <transparentColor+3000000) {
                        allpixels[i] = Color.alpha(Color.TRANSPARENT);
                    }
                }
                break;
            
            case 2:
                for(int i =0; i<myBitmap.getHeight()*myBitmap.getWidth();i++) {
                    if (allpixels[i]>Color.WHITE-5000000&&allpixels[i]<Color.WHITE+5000000) {
                        allpixels[i]=Color.BLACK;
                    }else{
                        allpixels[i] = Color.alpha(Color.TRANSPARENT);
                    }
                }break;}
        myBitmap.setPixels(allpixels, 0, myBitmap.getWidth(), 0, 0, myBitmap.getWidth(), myBitmap.getHeight());
        return myBitmap;
    }
    public static Bitmap handleImageEffect(Bitmap bm,float lum){//亮度調整
        Bitmap bmp = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        ColorMatrix lumMatrix = new ColorMatrix();
        lumMatrix.setScale(lum, lum, lum, 1);

        ColorMatrix imageMatrix = new ColorMatrix();
        imageMatrix.postConcat(lumMatrix);

        paint.setColorFilter(new ColorMatrixColorFilter(imageMatrix));
        canvas.drawBitmap(bm, 0, 0, paint);

        return bmp;
    }
    public static Bitmap ChangeColor(Bitmap bit, int transparentColor) {//變更顏色
        int width =  bit.getWidth();
        int height = bit.getHeight();

        Bitmap myBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        int [] allpixels = new int [ myBitmap.getHeight()*myBitmap.getWidth()];
        bit.getPixels(allpixels, 0, myBitmap.getWidth(), 0, 0, myBitmap.getWidth(),myBitmap.getHeight());
        myBitmap.setPixels(allpixels, 0, width, 0, 0, width, height);


        for(int i =0; i<myBitmap.getHeight()*myBitmap.getWidth();i++) {
            if(allpixels[i]!=Color.TRANSPARENT)allpixels[i] = transparentColor;

        }
        myBitmap.setPixels(allpixels, 0, myBitmap.getWidth(), 0, 0, myBitmap.getWidth(), myBitmap.getHeight());
        return myBitmap;
    }

    public static Bitmap getBitmapFromView(View view) {
        //Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable =view.getBackground();
        if (bgDrawable!=null)
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        else
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        return returnedBitmap;
    }

    private byte[] Bitmap2Bytes(Bitmap bm){

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);

        return baos.toByteArray();

    }
    public static Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) width / w);
        float scaleHeight = ((float) height / h);
        matrix.postScale(scaleWidth, scaleHeight); // 不改變原來圖像大小
        Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
        return newbmp;
    }
}

