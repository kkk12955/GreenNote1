package com.im.tku.greennote;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class HomePage extends Activity {

    private static final String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/GreenNote"; //資料夾位置
    private static final int TAKE_PHOTO = 1;
    private Uri imageUri; //圖片路徑
    private String filename; //圖片名稱
    private ArrayList<Curse> list = new ArrayList<Curse>();//建立Arraylist
    private static ArrayList<String> cat_lsit; //分類選擇


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        getActionBar().hide(); //隱藏標題
        //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN); //隱藏狀態



        final LinearLayout liner_middle = (LinearLayout) super.findViewById(R.id.liner_middle);
        final LinearLayout.LayoutParams liner_middle_LayoutParams = (LinearLayout.LayoutParams) liner_middle.getLayoutParams();
        final LinearLayout liner_top = (LinearLayout) super.findViewById(R.id.liner_top);
        final LinearLayout.LayoutParams liner_top_LayoutParams = (LinearLayout.LayoutParams) liner_top.getLayoutParams();
        final ImageButton imgBtn_note = (ImageButton) findViewById(R.id.imageButton_note);
        final ImageButton imgBtn_photo = (ImageButton) findViewById(R.id.imageButton_photo);
        final ImageButton imgBtn_book = (ImageButton) findViewById(R.id.imageButton_book);
        final ImageButton imgBtn_folder = (ImageButton) findViewById(R.id.imageButton_folder);

        cat_lsit = new ArrayList<>();
        cat_lsit.add("未分類");
        //初始化建構
        initConstruction();
        //讀取課程資料
        loadCurse();
        //***介面調整***
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        //取得手機解析度
        int vWidth = dm.widthPixels; //寬
        int vHeight = dm.heightPixels; //長
        //針對手機進行畫面調整
        liner_middle_LayoutParams.leftMargin = (int) (vWidth *0.15);
        liner_top_LayoutParams.height = (int) (vHeight*0.15);



        //Onclick動作實作
        //筆記按鈕
        imgBtn_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("cat_lsit",cat_lsit);
                Intent intent = new Intent(HomePage.this, Note_create.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        //相機按鈕
        imgBtn_photo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Save_Photo();
            }
        });

        //課程按鈕
        imgBtn_book.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePage.this, Curse_page.class);
                startActivity(intent);
            }
        });

        //資料庫按鈕
        imgBtn_folder.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePage.this, Folder.class);
                startActivity(intent);
            }
        });

    }

    private void loadCurse() { //讀取課程

        File file = new File(path,"saveBook.txt");


        FileInputStream in = null;
        StringBuffer data = new StringBuffer();
        try {
            //開啟 getFilesDir() 目錄底下名稱為 test.txt 檔案
            in = new FileInputStream(file);
            //讀取該檔案的內容
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(in, "utf-8"));
            //讀取字串
            String line;

            while ((line = reader.readLine()) != null) {

                String [] tmp = line.split(",");
                String [] tmp2 = tmp[4].split("~");
                String [] tmp_1 = tmp2[0].split("："); //Start 時間
                String [] tmp_2 = tmp2[1].split("："); //End時間


                // lisst 名稱 地點 日期 開始時間 結束時間
                cat_lsit.add(tmp[0]);
                list.add(new Curse(tmp[0],tmp[1],tmp[2],Integer.parseInt(tmp[3]),new Time(Integer.parseInt(tmp_1[0]),Integer.parseInt(tmp_1[1])), new Time(Integer.parseInt(tmp_2[0]),Integer.parseInt(tmp_2[1]))));


            }
        }
        catch (Exception e) { }
        finally {
            try {
                in.close();
            } catch (Exception e) {}
        }

    }

    private class DateUtil{
        private int hour,min; private String week;
        public  Calendar calendar = Calendar.getInstance();
        public  String getChineseDayOfWeek(
                Calendar rightNow) {
            String chineseDayOfWeek = null;

            switch(rightNow.get(Calendar.DAY_OF_WEEK)) {
                case Calendar.SUNDAY:
                    chineseDayOfWeek = "7";
                    break;
                case Calendar.MONDAY:
                    chineseDayOfWeek = "1";
                    break;
                case Calendar.TUESDAY:
                    chineseDayOfWeek = "2";
                    break;
                case Calendar.WEDNESDAY:
                    chineseDayOfWeek = "3";
                    break;
                case Calendar.THURSDAY:
                    chineseDayOfWeek = "4";
                    break;
                case Calendar.FRIDAY:
                    chineseDayOfWeek = "5";
                    break;
                case Calendar.SATURDAY:
                    chineseDayOfWeek = "6";
                    break;
            }

            return chineseDayOfWeek;
        }
    }

    protected void initConstruction(){
        File file = new File(path); //課程檔案資料夾路徑
        if(!file.exists()){ //若資料夾不存在
            file.mkdir(); //建立資料夾
        }
        file = new File(path+"/Note");
        if(!file.exists()){ //若資料夾不存在
            file.mkdir(); //建立資料夾
        }
        file = new File(path+"/Note/未分類");
        if(!file.exists()){ //若資料夾不存在
            file.mkdir(); //建立資料夾
        }
        file = new File(path+"/Image");
        if(!file.exists()){ //若資料夾不存在
            file.mkdir(); //建立資料夾
        }
        file = new File(path+"/Image/未分類");
        if(!file.exists()){ //若資料夾不存在
            file.mkdir(); //建立資料夾
        }
    }

    protected void Save_Photo(){
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date(System.currentTimeMillis());
        filename = format.format(date);
        // 存放照片位置字串
        String filepath;
        //==取得目前時間
        DateUtil nowdate=new DateUtil();
        nowdate.calendar.setTime(date);
        nowdate.hour=nowdate.calendar.get(Calendar.HOUR_OF_DAY); //24小時制時
        nowdate.min=nowdate.calendar.get(Calendar.MINUTE);         //分
        nowdate.week=nowdate.getChineseDayOfWeek(nowdate.calendar);  //星期

        //判斷時間因應儲存位置
        filepath = path+"/Image/未分類";
        for(int i=0;i<list.size();i++){
            if(list.get(i).get_Curse_Date() == Integer.parseInt(nowdate.week)){
                if(list.get(i).get_Curse_StartTime().time_compare(nowdate.hour,nowdate.min) && !list.get(i).get_Curse_EndTime().time_compare(nowdate.hour,nowdate.min)){ //判斷現在時間是否符合課程時間
                    filepath = path + "/Image/" + list.get(i).get_Curse_Name();
                }
            }
        }

        Toast.makeText(HomePage.this,  filepath  , Toast.LENGTH_SHORT).show();

        File outputImage = new File(filepath,filename+".jpg"); //指定輸出檔案
        imageUri = Uri.fromFile(outputImage);
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE"); //照相
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri); //指定照片輸出位址
        startActivityForResult(intent,TAKE_PHOTO); //啟動相機
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub

        if (keyCode == KeyEvent.KEYCODE_BACK) { // 攔截返回鍵
            new AlertDialog.Builder(HomePage.this)
                    .setTitle("確認視窗")
                    .setMessage("確定要結束應用程式嗎?")
                    .setPositiveButton("確定",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    finish();
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
