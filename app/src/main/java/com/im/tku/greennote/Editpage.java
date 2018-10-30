package com.im.tku.greennote;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.constraint.ConstraintLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import yuku.ambilwarna.AmbilWarnaDialog;
import android.content.res.AssetManager;


public class Editpage extends Activity implements Serializable {

    private static final long serialVersionUID = -763618247875550322L;

    private static ImageButton newtext, newpic, newsave, newcolor;
    private static ConstraintLayout background;
    //顏色區塊
    private int currentColor;

    //文字區塊
    private static Context context;
    private static MyImageView imageView;
    private static TextView textView;
    private static ImageViewParams ivParams;
    private TextViewParams tvParams;
    private static android.support.constraint.ConstraintLayout layout;

    public static boolean imgcheck = false;
    private boolean mflag = false;
    private boolean tvOneFinger;

    //紀錄是否為TextView上的單擊事件
    private boolean isClick = true;
    public static final int DEFAULT_TEXTSIZE = 20;

    //保存創建的TextView
    public static List<TextView> list;
    public static List<TextViewParams> listTvParams;
    public static List<MyImageView> listimg;
    public static List<ImageViewParams> listIvParams;


    private static List<Bitmap> list_bitmap;
    public static List<Matrix> listIvMatrix;


    private float oldDist = 0;
    private float textSize = 0;


    //左邊點的偏移量
    float mTv_width;
    float mTv_height;
    float tv_widths;
    float tv_heights;
    float mTv_widths;
    float mTv_heights;


    private byte[] bytes;

    private int width;
    private int height;
    private float startX;
    private float startY;

    private static final int INVALID_POINTER_ID = -1;
    private float fX, fY, sX, sY;
    private float mfX, mfY, msX, msY;
    private int ptrID1, ptrID2;
    private int mptrID1, mptrID2;
    private float mAngle;
    private float scale;
    private static MotionEvent mEvent;


    //记录第一个手指下落时的位置
    private float firstX;
    private float firstY;

    private float defaultAngle;

    //记录当前点击坐标
    private float currentX;
    private float currentY;

    //记录当前设备的缩放倍数
    private double scaleTimes = 1;

    //存讀檔區塊
    private String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/GreenNote/Note/"; //資料夾路徑

    //存讀檔區塊變數
    private String tag;
    private String file_path = "null";
    private String text;
    private static int textView_cnt;
    private static int ImageView_cnt;

    private static List<TextViewParams> list_tv_tmp;
    private static List<MyImageView> list_Image_tmp;


    //圖片控制變數區塊
    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();
    /**
     * 位图对象
     */
    private Bitmap bitmap = null;
    /**
     * 屏幕的分辨率
     */
    private static DisplayMetrics dm;

    /**
     * 最小缩放比例
     */
    float minScaleR = 1.0f;

    /**
     * 最大缩放比例
     */
    static final float MAX_SCALE = 15f;

    /**
     * 初始状态
     */
    static final int NONE = 0;
    /**
     * 拖动
     */
    static final int DRAG = 1;
    /**
     * 缩放
     */
    static final int ZOOM = 2;

    /**
     * 存储float类型的x，y值，就是你点下的坐标的X和Y
     */
    PointF prev = new PointF();
    PointF mid = new PointF();
    float dist = 1f;

    private BitmapDataObject bitmapDataObject;

    private Metrics metrics;
    /**
     * 当前模式
     */
    int mode = NONE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editpage);
        getActionBar().hide(); //隱藏標題


        context = this;

        init();
        //進入讀檔存檔初始化
        Bundle bundle = getIntent().getExtras();
        tag = bundle.getString("TAG");
        switch (tag) {
            case "new":

                break;

            case "load":
                Saving("load");

                for (int i = 0; i < list_bitmap.size(); i++) {
                    addImageView(null,list_bitmap.get(i),(int)listIvParams.get(i).getX(),(int)listIvParams.get(i).getY(),listIvParams.get(i).getWidth(),listIvParams.get(i).getHeight());
                    ImageView_cnt++;
                    Log.e("HHH","圖片讀取一次");
                }
                for (int i = 0; i < listTvParams.size(); i++) {
                    addTextView(null, listTvParams.get(i).getX(), listTvParams.get(i).getY(), listTvParams.get(i).getContent(), listTvParams.get(i).getTextColor(), listTvParams.get(i).getScale());
                    Log.e("HHH","文字讀取一次");
                    Log.e("HHH",listTvParams.get(i).getContent());
                    textView_cnt++;
                }
                setListTvParams(list_tv_tmp);
                setListImageView(list_Image_tmp);
                textView_cnt=0;
                ImageView_cnt=0;

                break;
            case "img":
                Saving("load");
                Log.e("HHH",""+list_bitmap.size());
                for (int i = 0; i < list_bitmap.size(); i++) {
                    addImageView(null,list_bitmap.get(i),(int)listIvParams.get(i).getX(),(int)listIvParams.get(i).getY(),listIvParams.get(i).getWidth(),listIvParams.get(i).getHeight());
                    ImageView_cnt++;
                    Log.e("HHH","圖片讀取一次");
                }
                for (int i = 0; i < listTvParams.size(); i++) {
                    addTextView(null, listTvParams.get(i).getX(), listTvParams.get(i).getY(), listTvParams.get(i).getContent(), listTvParams.get(i).getTextColor(), listTvParams.get(i).getScale());
                    Log.e("HHH","文字讀取一次");
                    Log.e("HHH",listTvParams.get(i).getContent());
                    textView_cnt++;
                }
                setListTvParams(list_tv_tmp);
                setListImageView(list_Image_tmp);
                textView_cnt=0;
                ImageView_cnt=0;

                bytes = bundle.getByteArray("bitmap");

                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                list_bitmap.add(bmp);
                addImageView(null, bmp);
                Log.e("HHH", "測試用");



                break;
        }


        newsave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                saveDialog();
            }
        });
        newtext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                AlertDialog.Builder editDialog = new AlertDialog.Builder(Editpage.this);
                editDialog.setTitle("--- Edit ---");

                final EditText editText = new EditText(Editpage.this);
                editDialog.setView(editText);
                editDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int arg0) {

                        addTextView(null, 0, 0, editText.getText().toString(), Color.BLACK, 20f);


                    }
                });
                editDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        //...
                    }
                });
                editDialog.show();
            }
        });

        newpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Editpage.this, Folder_Image.class);
                Bundle bundle = new Bundle();
                bundle.putString("TAG", "choose");
                intent.putExtras(bundle);
                startActivity(intent);
            }

        });

        newcolor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createColorDialog(false);
            }
        });

    }

    private void init() {
        checkFileName();

        ptrID1 = INVALID_POINTER_ID;
        ptrID2 = INVALID_POINTER_ID;
        mptrID1 = INVALID_POINTER_ID;
        mptrID2 = INVALID_POINTER_ID;

        list = new ArrayList<>();
        listTvParams = new ArrayList<>();
        listimg = new ArrayList<>();
        listIvParams = new ArrayList<>();
        list_bitmap = new ArrayList<>();


        list_tv_tmp = new ArrayList<>();
        list_Image_tmp = new ArrayList<>();

        bitmapDataObject = new BitmapDataObject();

        textView_cnt = 0;
        ImageView_cnt = 0;

        newtext = (ImageButton) findViewById(R.id.wordnote);
        newpic = (ImageButton) findViewById(R.id.picturenote);
        newsave = (ImageButton) findViewById(R.id.savenote);
        newcolor = (ImageButton) findViewById(R.id.bgcolor);
        background = (ConstraintLayout) findViewById(R.id.background);
        layout = (android.support.constraint.ConstraintLayout) findViewById(R.id.note);
    }
    //檢查檔名
    private void checkFileName(){
        try {
            FileInputStream fis = openFileInput("note_path");
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            StringBuilder out = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                out.append(line);
            }

            file_path = out.toString();


            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<TextView> getList() {
        return list;
    }

    private void addTextView(TextView textView, float x, float y, String s, int color, float mtextSize) {
        this.textView = textView;
        if (textView == null) {
            if (mtextSize == 0) {
                mtextSize = DEFAULT_TEXTSIZE;
            }
            textView = new TextView(context);
            // textView.setEms(1);
            textView.setTag(System.currentTimeMillis());
            textView.setText(s);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            textView.setLayoutParams(params);
            textView.setTextSize(mtextSize);
            textView.setTextColor(color);
            textView.setX(x - textView.getWidth());
            textView.setY(y - textView.getHeight());
            updateTextViewParams(textView, scale);
            textView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    TextView textView = (TextView) v;

                    mEvent = event;
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_DOWN:
                            //此时有一个手指头落点
                            tvOneFinger = true;
                            isClick = true;
                            //给第一个手指落点记录落点的位置
                            firstX = event.getX();
                            firstY = event.getY();

                            mptrID1 = event.getPointerId(event.getActionIndex());
                            //计算当前textView的位置和大小
                            width = textView.getWidth();
                            height = textView.getHeight();
                            if (mEvent != null) {
                                mTv_width = mEvent.getX() - textView.getX();
                                mTv_height = mEvent.getY() - textView.getY();
                            }
                            mflag = true;
                            break;
                        case MotionEvent.ACTION_POINTER_DOWN:
                            //第二個手指落點  不是點擊事件
                            tvOneFinger = false;
                            isClick = false;
                            //第二根手指落下
                            mptrID2 = event.getPointerId(event.getActionIndex());
                            msX = mEvent.getX(event.findPointerIndex(mptrID1));
                            msY = mEvent.getY(event.findPointerIndex(mptrID1));
                            mfX = mEvent.getX(event.findPointerIndex(mptrID2));
                            mfY = mEvent.getY(event.findPointerIndex(mptrID2));

                            mflag = false;
//
                            mTv_widths = getMidPiont((int) mfX, (int) mfY, (int) msX, (int) msY).x - textView.getX();
                            mTv_heights = getMidPiont((int) mfX, (int) mfY, (int) msX, (int) msY).y - textView.getY();
//
                            oldDist = spacing(event, mptrID1, mptrID2);

                            break;
                        case MotionEvent.ACTION_MOVE:
                            //平移操作
                            if (mflag && mEvent != null) {
                                textView.setX(mEvent.getX() - mTv_width);
                                textView.setY(mEvent.getY() - mTv_height);
                            }

                            if (spacing(firstX, firstY, event.getX(), event.getY()) > 2) {
                                isClick = false;
                            }

                            //縮放操作
                            if (mptrID1 != INVALID_POINTER_ID && mptrID2 != INVALID_POINTER_ID) {
                                float nfX, nfY, nsX, nsY;
                                nsX = mEvent.getX(event.findPointerIndex(mptrID1));
                                nsY = mEvent.getY(event.findPointerIndex(mptrID1));
                                nfX = mEvent.getX(event.findPointerIndex(mptrID2));
                                nfY = mEvent.getY(event.findPointerIndex(mptrID2));

                                //雙指進行平移移動
                                textView.setX(getMidPiont((int) nfX, (int) nfY, (int) nsX, (int) nsY).x - mTv_widths);
                                textView.setY(getMidPiont((int) nfX, (int) nfY, (int) nsX, (int) nsY).y - mTv_heights);
                                //實現縮放
                                float newDist = spacing(event, mptrID1, mptrID2);
                                scale = newDist / oldDist;

                                if (newDist > oldDist + 1) {
                                    textView.setTextSize(textSize *= scale);
                                    oldDist = newDist;
                                }
                                if (newDist < oldDist - 1) {
                                    textView.setTextSize(textSize *= scale);
                                    oldDist = newDist;
                                }

                            }
                            break;
                        case MotionEvent.ACTION_UP:

                            mptrID1 = INVALID_POINTER_ID;
                            updateTextViewParams((TextView) v, scale);

                            if (tvOneFinger && isClick) {
                                updateTextViewParams((TextView) v, scale);
                                createDialog(context, textView);
                            }

                            break;
                        case MotionEvent.ACTION_POINTER_UP:
                            mptrID2 = INVALID_POINTER_ID;
                            updateTextViewParams((TextView) v, scale);
                            break;
                        case MotionEvent.ACTION_CANCEL:
                            mptrID1 = INVALID_POINTER_ID;
                            mptrID2 = INVALID_POINTER_ID;
                            break;
                    }
                    return true;
                }
            });

            //保存并添加到list中
            if (listTvParams.size() - list_tv_tmp.size() > 0) {

                Log.e("HHH", "讀取1次");
                list.add(textView);
                saveTextViewparams(textView, list_tv_tmp);

            } else {
                Log.e("HHH", "新增1次");
                list.add(textView);
                saveTextViewparams(textView, listTvParams);
            }
            layout.addView(textView);
            Log.e("HHH", "123");
        } else {
            layout.addView(textView);
            textView.setText(s);
            textView.setTextColor(color);
        }
    }

    public void addImageView(MyImageView imageView, Bitmap bmp) {
        this.imageView = imageView;

        imageView = new MyImageView(layout.getContext(),null,null);
        imageView.setImageBitmap(bmp);
        listimg.add(imageView);
        layout.addView(imageView);
    }
    public void addImageView(MyImageView imageView, Bitmap bmp,int x , int y , int width , int heigh) {
        this.imageView = imageView;
        imageView = new MyImageView(layout.getContext(),null,null);
        Log.e("HHH","x="+x+"y="+y+"w="+width+"h="+heigh);
        imageView.setImageBitmap(bmp);
        listimg.add(imageView);

        imageView.layout(x,y,x+width,y+heigh);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.width = width;
        params.height = heigh;
        imageView.setLayoutParams(params);

        layout.addView(imageView);
    }

    public void ImageView_set(MyImageView imageView, Bitmap bmp, ImageViewParams imageViewParams) {
        this.imageView = imageView;
        imageView = new MyImageView(layout.getContext(),null,imageViewParams);
        imageView.setImageBitmap(bmp);
        listimg.add(imageView);
        layout.addView(imageView);
    }

    /**
     * 顯示自訂對話框 *文字選項*
     */
    private void createDialog(final Context context, final TextView textView) {

        AlertDialog.Builder adb = new AlertDialog.Builder(context);
        adb.setTitle("文字選項");
        adb.setIcon(android.R.drawable.ic_dialog_alert);
        adb.setItems(R.array.TextView_Dialog, new DialogInterface.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch (which) {
                    //修改文字
                    case 0:
                        final AlertDialog.Builder editDialog = new AlertDialog.Builder(context);
                        editDialog.setTitle("--- Edit ---");

                        final EditText editText = new EditText(context);
                        editDialog.setView(editText);
                        editText.setText(textView.getText());
                        editDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int arg0) {
                                textView.setText(editText.getText());
                            }
                        });
                        editDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface arg0, int arg1) {

                            }
                        });
                        editDialog.show();

                        break;
                    //修改顏色
                    case 1:
                        createColorDialog(context, false, textView);
                        break;

                    //修改字型
                    case 2:
                        String [] s = {"標楷體","新細明體"};
                        AlertDialog.Builder adb = new AlertDialog.Builder(context);
                        adb.setTitle("文字選項");
                        adb.setIcon(android.R.drawable.ic_dialog_alert);
                        adb.setItems(s, new DialogInterface.OnClickListener() {
                            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        textView.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/bkai00mp.ttf"));
                                        break;
                                    case 1:
                                        textView.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/mingliu.TTF"));
                                        break;


                                }
                            }
                        });
                        AlertDialog alertDialog = adb.create();
                        alertDialog.show();
                        break;
                    //刪除
                    case 3:

                        refreshView(textView);
                        break;
                    //取消
                    case 4:
                        dialog.dismiss();
                        break;

                }
            }
        });
        AlertDialog alertDialog = adb.create();
        alertDialog.show();


    }

    /**
     * 顯示自訂對話框 *色彩選項*
     */
    private void createColorDialog(final Context context, boolean supportsAlpha, final TextView textView) {

        currentColor = textView.getCurrentTextColor();
        AmbilWarnaDialog dialog = new AmbilWarnaDialog(context, currentColor, supportsAlpha, new AmbilWarnaDialog.OnAmbilWarnaListener() {

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {

                Log.e("HHH", "list長度=" + list.size());
                Log.e("HHH", "paras長度=" + listTvParams.size());
                currentColor = color;
                textView.setTextColor(color);
                updateTextViewParams(textView, scale);

            }

            @Override
            public void onCancel(AmbilWarnaDialog dialog) {


            }
        });
        dialog.show();
    }

    /**
     * 顯示自訂對話框 *色彩選項*
     */
    private void createColorDialog(boolean supportsAlpha) {
        currentColor = background.getSolidColor();
        //currentColor =((ColorDrawable)background.getBackground().mutate()).getColor();
        AmbilWarnaDialog dialog = new AmbilWarnaDialog(context, currentColor, supportsAlpha, new AmbilWarnaDialog.OnAmbilWarnaListener() {

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {


                currentColor = color;
                background.setBackgroundColor(color);

            }

            @Override
            public void onCancel(AmbilWarnaDialog dialog) {
                Toast.makeText(Editpage.this, "Action canceled!", Toast.LENGTH_SHORT).show();

            }
        });
        dialog.show();
    }

    //存檔功能選項
    private void saveDialog(){
        new AlertDialog.Builder(context)
                    .setTitle("確認視窗")
                    .setMessage("確定是否進行存檔")
                    .setPositiveButton("確定",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    Saving("save");
                                }
                            })
                    .setNegativeButton("取消",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    // TODO Auto-generated method stub

                                }
                            }).show();
    }

    /**
     * 顯示自訂對話框 *刷新介面*
     */
    private void refreshView(TextView textView) {
        for (int i = 0; i < list.size(); i++) {
            if (textView.equals(list.get(i))) {
                list.remove(i);
                listTvParams.remove(i);
            }
        }
        layout.removeAllViews();
        for (int i = 0; i < list.size(); i++) {
            Log.e("EEE", "TEST");
            layout.addView(list.get(i));
        }
        for(int i = 0 ; i < list_bitmap.size();i++){
            layout.addView(listimg.get(i));
        }
        //refresh();

    }

    //對物件進行參數更新
    private void updateTextViewParams(TextView tv, float scale) {
        for (int i = 0; i < listTvParams.size(); i++) {
            TextViewParams param = new TextViewParams();
            if (tv.getTag().toString().equals(listTvParams.get(i).getTag())) {
                param.setTextSize((float) (tv.getTextSize() / scaleTimes));
                param.setMidPoint(getViewMidPoint(tv));
                param.setScale(scale);
                textSize = tv.getTextSize() / 2;
                param.setWidth(tv.getWidth());
                param.setHeight(tv.getHeight());
                param.setX(tv.getX());
                param.setY(tv.getY());
                param.setTag(listTvParams.get(i).getTag());
                param.setContent(tv.getText().toString());
                param.setTextColor(tv.getCurrentTextColor());
                listTvParams.set(i, param);
                return;
            }
        }
    }

    public static void saveImageViewparams(MyImageView imageView) {
        if (imageView != null) {
            ivParams = new ImageViewParams();
            ivParams.setX((int) imageView.getX());
            ivParams.setY((int) imageView.getY());
            ivParams.setWidth(imageView.getWidth());
            ivParams.setHeight(imageView.getHeight());
            //tvParams.setMidPoint(getViewMidPoint(textView));
            ivParams.setTag(String.valueOf((long) imageView.getTag()));
            listIvParams.add(ivParams);
        }
    }

    public static class ImageViewParams implements Serializable {
        private static final long serialVersionUID = 1L;
        private float scale;
        private int width;
        private int height;
        private String tag;
        private int x;
        private int y;

        @Override
        public String toString() {
            return "TextViewParams{" +
                    ", scale=" + scale +
                    ", width=" + width +
                    ", height=" + height +
                    ", x=" + x +
                    ", y=" + y +
                    '}';
        }
        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        public float getScale() {
            return scale;
        }

        public void setScale(float scale) {
            this.scale = scale;
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

        public void setHeight(int height) {
            this.height = height;
        }

        public float getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public float getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }
    }

    //對物件重新給予數值 防止出現錯誤
    private void setTextViewParams(TextViewParams para) {
        scale = para.getScale();
        textSize = para.getTextSize();
        defaultAngle = mAngle;
        Log.d("HHH", "defaultAngle " + defaultAngle);
    }

    //根据TextView获取到该TextView的配置文件
    private TextViewParams getTextViewParams(TextView tv) {
        for (int i = 0; i < listTvParams.size(); i++) {
            if (listTvParams.get(i).getTag().equals(String.valueOf((long) tv.getTag()))) {
                return listTvParams.get(i);
            }
        }
        return null;
    }

    //返回所有的TextView的参数
    private List<TextViewParams> getListTvParams() {
        List<TextViewParams> newImageList = new ArrayList<>();
        newImageList.addAll(listTvParams);
        return newImageList;
    }

    private void setListTvParams(List<TextViewParams> listTvParams) {

        List<TextViewParams> tempList = new ArrayList<>();
        tempList.addAll(listTvParams);
        this.listTvParams = tempList;

    }

    private void setListImageView(List<MyImageView> listImageView) {

        List<MyImageView> tempList = new ArrayList<>();
        tempList.addAll(listImageView);
        this.listimg = tempList;

    }

    //縮放實現
    private void zoom(float f) {
        textView.setTextSize(textSize *= f);
    }


    /**
     * 求两个一直点的距离
     *
     * @param p1
     * @param p2
     * @return
     */
    private double spacing(Point p1, Point p2) {
        double x = p1.x - p2.x;
        double y = p1.y - p2.y;
        return Math.sqrt((x * x + y * y));
    }

    /**
     * 返回两个点之间的距离
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    private double spacing(float x1, float y1, float x2, float y2) {
        double x = x1 - x2;
        double y = y1 - y2;
        return Math.sqrt((x * x + y * y));
    }

    /**
     * 计算两点之间的距离
     *
     * @param event
     * @return 两点之间的距离
     */
    private float spacing(MotionEvent event, int ID1, int ID2) {
        float x = event.getX(ID1) - event.getX(ID2);
        float y = event.getY(ID1) - event.getY(ID2);
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * 两点的距离
     */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }


    //回View的中點坐標
    //如果該View不存在則返回(0,0)
    private Point getViewMidPoint(View view) {
        Point point = new Point();
        if (view != null) {
            float xx = view.getX();
            float yy = view.getY();
            int center_x = (int) (xx + view.getWidth() / 2);
            int center_y = (int) (yy + view.getHeight() / 2);
            point.set(center_x, center_y);
        } else {
            point.set(0, 0);
        }
        return point;
    }

    private Point getMidPiont(Point p1, Point p2) {
        return new Point((p1.x + p2.x) / 2, (p1.y + p2.y) / 2);
    }

    /**
     * 获取中间点
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    private Point getMidPiont(int x1, int y1, int x2, int y2) {
        return new Point((x1 + x2) / 2, (y1 + y2) / 2);
    }

    /**
     * 两点的中点
     */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    private void saveTextViewparams(TextView textView, List<TextViewParams> listTvParams) {
        if (textView != null) {
            tvParams = new TextViewParams();
            tvParams.setTextSize((float) (textView.getTextSize() / scaleTimes));
            tvParams.setX(textView.getX());
            tvParams.setY(textView.getY());
            tvParams.setWidth(textView.getWidth());
            tvParams.setHeight(textView.getHeight());
            tvParams.setContent(textView.getText().toString());
            //tvParams.setMidPoint(getViewMidPoint(textView));
            tvParams.setScale(1);
            tvParams.setTag(String.valueOf((long) textView.getTag()));
            tvParams.setTextColor(textView.getCurrentTextColor());
            listTvParams.add(tvParams);
        }
    }

    public static void updateImageViewParams(MyImageView iv, float scale) {
        for (int i = 0; i < listIvParams.size(); i++) {
            ImageViewParams param = new ImageViewParams();
            if (iv.getTag().toString().equals(listIvParams.get(i).getTag())) {
                param.setScale(scale);
                param.setWidth(iv.getWidth());
                param.setHeight(iv.getHeight());
                param.setX((int) iv.getX());
                param.setY((int) iv.getY());
                param.setTag(listIvParams.get(i).getTag());
                listIvParams.set(i, param);
                Log.e("HHH","iv修改位置");
                return;
            }
        }
    }


    //存取功能
    public void Saving(String s) {
        BitmapDataObject bitmapDataObject = new BitmapDataObject();
        if (s.equals("load")) {
            File f = new File(file_path);

            if (f.exists()) {
                Log.e("HHH","確定一下");
                try {
                    Log.e("HHHL", "listTvparar=" + listTvParams.size());
                    Log.e("HHH", "listimg=" + listimg.size());

                    Log.e("HHH", f.getPath());
                    ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));


                    int j = ois.readInt();
                    listTvParams.clear();
                    list_bitmap.clear();
                    listIvParams.clear();
                    for(int i = 0 ; i < j ; i++){
                        Bitmap bmap = bitmapDataObject.readObject(ois);
                        list_bitmap.add(bmap);
                        listIvParams.add((ImageViewParams) ois.readObject());
                    }
                    listTvParams = (List<TextViewParams>) ois.readObject();
                    Log.e("HHHL", "listTvparar=" + listTvParams.size());
                    Log.e("HHHL", "listMr=" + listIvParams.size());
                    Log.e("HHH", "listimg=" + list_bitmap.size());



                    ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }

        } else if (s.equals("save")) {
            File f = new File(file_path);
            try {
                if (f.exists()) {
                    f.delete();
                }
                ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f));
                Log.e("HHH", "listTvparar=" + listTvParams.size());
                Log.e("HHH", "listbmp=" + list_bitmap.size());
                Log.e("HHH", "listi,g=" + listimg.size());

                oos.writeInt(listimg.size());

                for(int i = 0 ; i < listimg.size() ; i++){

                    BitmapDrawable drawable = (BitmapDrawable) listimg.get(i).getDrawable();
                    Bitmap bmap = drawable.getBitmap();
                    bitmapDataObject.writeObject(oos,bmap);
                    oos.writeObject(listIvParams.get(i));
                }

                oos.writeObject(listTvParams);
                oos.close();

                Toast.makeText(Editpage.this, "存檔位置為"+file_path, Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    protected void center(boolean horizontal, boolean vertical) {
        Matrix m = new Matrix();
        m.set(matrix);
        RectF rect = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
        m.mapRect(rect);

        float height = rect.height();
        float width = rect.width();

        float deltaX = 0, deltaY = 0;

        if (vertical) {
            // 图片小于屏幕大小，则居中显示。大于屏幕，上方留空则往上移，下方留空则往下移
            int screenHeight = dm.heightPixels;
            if (height < screenHeight) {
                deltaY = (screenHeight - height) / 2 - rect.top;
            } else if (rect.top > 0) {
                deltaY = -rect.top;
            } else if (rect.bottom < screenHeight) {
                deltaY = layout.getHeight() - rect.bottom;
            }
        }

        if (horizontal) {
            int screenWidth = dm.widthPixels;
            if (width < screenWidth) {
                deltaX = (screenWidth - width) / 2 - rect.left;
            } else if (rect.left > 0) {
                deltaX = -rect.left;
            } else if (rect.right < screenWidth) {
                deltaX = screenWidth - rect.right;
            }
        }
        matrix.postTranslate(deltaX, deltaY);
    }
    //實現序列化

    public class TextView extends android.support.v7.widget.AppCompatTextView implements Serializable {
        private static final long serialVersionUID = 1L;

        public TextView(Context context) {
            super(context);
        }
    }

    public class Point extends android.graphics.Point implements Serializable {
        private static final long serialVersionUID = 1L;

        public int x;
        public int y;

        Point() {

        }

        Point(Point point) {
            this.x = point.x;
            this.y = point.y;
        }

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public void set(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public static class TextViewParams implements Serializable {
        private static final long serialVersionUID = 1L;
        private String tag;
        private float textSize;
        private Point midPoint;
        private float scale;
        private String content;
        private int width;
        private int height;
        private float x;
        private float y;
        private int textColor;

        public int getTextColor() {
            return textColor;
        }

        public void setTextColor(int textColor) {
            this.textColor = textColor;
        }

        @Override
        public String toString() {
            return "TextViewParams{" +
                    "tag='" + tag + '\'' +
                    ", textSize=" + textSize +
                    ", midPoint=" + midPoint +
                    ", scale=" + scale +
                    ", content='" + content + '\'' +
                    ", width=" + width +
                    ", height=" + height +
                    ", x=" + x +
                    ", y=" + y +
                    ", textColor=" + textColor +
                    '}';
        }

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        public float getTextSize() {
            return textSize;
        }

        public void setTextSize(float textSize) {
            this.textSize = textSize;
        }

        public Point getMidPoint() {
            return midPoint;
        }

        public void setMidPoint(Point midPoint) {
            this.midPoint = midPoint;
        }

        public float getScale() {
            return scale;
        }

        public void setScale(float scale) {
            this.scale = scale;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
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

        public void setHeight(int height) {
            this.height = height;
        }

        public float getX() {
            return x;
        }

        public void setX(float x) {
            this.x = x;
        }

        public float getY() {
            return y;
        }

        public void setY(float y) {
            this.y = y;
        }

    }

    public static class Matrix extends android.graphics.Matrix implements Serializable {
        private static final long serialVersionUID = 1L;
    }

    public static class PointF extends android.graphics.PointF implements Serializable {
        private static final long serialVersionUID = 1L;
    }

    public static class Metrics extends android.util.DisplayMetrics implements Serializable {
        private static final long serialVersionUID = 1L;
    }

    protected class BitmapDataObject implements Serializable {
        private static final long serialVersionUID = 111696345129311948L;
        public byte[] imageByteArray;



        /**
         * Included for serialization - write this layer to the output stream.
         */
        private void writeObject(ObjectOutputStream out,Bitmap b) throws IOException {

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            b.compress(Bitmap.CompressFormat.PNG, 100, stream);
            BitmapDataObject bitmapDataObject = new BitmapDataObject();
            bitmapDataObject.imageByteArray = stream.toByteArray();
            out.writeObject(bitmapDataObject);

        }

        /**
         * Included for serialization - read this object from the supplied input stream.
         */
        private Bitmap readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {

            BitmapDataObject bitmapDataObject = (BitmapDataObject) in.readObject();
            Bitmap image = BitmapFactory.decodeByteArray(bitmapDataObject.imageByteArray, 0, bitmapDataObject.imageByteArray.length);
            return image;

        }
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub

        if (keyCode == KeyEvent.KEYCODE_BACK) { // 攔截返回鍵
            new AlertDialog.Builder(context)
                    .setTitle("確認視窗")
                    .setMessage("確定是否進行存檔並退出")
                    .setPositiveButton("確定",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    Saving("save");
                                    Intent intent = new Intent(Editpage.this, HomePage.class);
                                    startActivity(intent);
                                }
                            })
                    .setNegativeButton("取消",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    // TODO Auto-generated method stub

                                }
                            }).show();

        }
        return true;
    }
}





// 自訂textView 介面

//系統初始化設定
