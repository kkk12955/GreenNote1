package com.im.tku.greennote;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class    Curse_page extends Activity  {

    public String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/GreenNote"; //資料夾路徑
    private String NAME_ITEM = "name";
    private String TEACHER_ITEM = "teacher";
    private String LOCATION_ITEM = "location";
    private String DATE_ITEM = "date";
    private String TIME_ITEM = "time";
    private List<Map<String, Object>> filesList;
    private SimpleAdapter simpleAdapter;
    private ListView listView;
    private Map<String, Object> filesMap;
    private ImageButton imgBtn_add;
    private File file ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_curse_page);

        //隱藏標題
        getActionBar().hide();

        //繼承
        imgBtn_add = (ImageButton) findViewById(R.id.Btn_add_book);
        listView = (ListView) findViewById(R.id.list_curse);

        //建立課表介面
        Create_Curse_View();

        //新增課表按鈕實作
        imgBtn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Curse_page.this, Curse_create.class);
                startActivity(intent);
            }
        });
    }
    protected void createDialog(View view, final int position){

        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        //adb.setView(Main.this);
        adb.setTitle("課程選項");
        adb.setIcon(android.R.drawable.ic_dialog_alert);
        adb.setItems(R.array.Curse_Dialog, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    //編輯
                    case 0:

                        //new一個intent物件，並指定Activity切換的class
                        Intent intent = new Intent();
                        intent.setClass(Curse_page.this, Curse_create.class);

                        //處理資料
                        String name = (String) filesList.get(position).get(NAME_ITEM).toString().substring(5);
                        String teacher = (String) filesList.get(position).get(TEACHER_ITEM).toString().substring(5);
                        String location = (String) filesList.get(position).get(LOCATION_ITEM).toString().substring(3);
                        String str_date = (String) (filesList.get(position).get(DATE_ITEM).toString().substring(3));
                        String time = (String) filesList.get(position).get(TIME_ITEM).toString().substring(3);
                        int date = 0;

                        if(str_date.equalsIgnoreCase("一")){
                            date = 1;
                        }
                        if(str_date.equalsIgnoreCase("二")){
                            date = 2;
                        }
                        if(str_date.equalsIgnoreCase("三")){
                            date = 3;
                        }
                        if(str_date.equalsIgnoreCase("四")){
                            date = 4;
                        }
                        if(str_date.equalsIgnoreCase("五")){
                            date = 5;
                        }
                        if(str_date.equalsIgnoreCase("六")){
                            date = 6;
                        }
                        if(str_date.equalsIgnoreCase("日")) {
                            date = 7;
                        }

                        //new一個Bundle物件，並將要傳遞的資料傳入
                        Bundle bundle = new Bundle();
                        bundle.putString("name",name);
                        bundle.putString("teacher",teacher);
                        bundle.putString("location",location);
                        bundle.putInt("date",date);
                        bundle.putString("time",time);
                        bundle.putInt("TAG",position);


                        //將Bundle物件assign給intent
                        intent.putExtras(bundle);

                        //切換Activity
                        startActivity(intent);
                        break;
                    //刪除
                    case 1:
                        Remove_Curse(position);
                        break;
                    //取消
                    case 2:
                        break;

                }
            }
        });
        AlertDialog alertDialog = adb.create();
        alertDialog.show();

        Dialog dialog=null;
        AlertDialog.Builder builder=new AlertDialog.Builder(this);

    }
    protected void Create_Curse_View(){
        filesList = new ArrayList<>();
        FileInputStream in = null;
        StringBuffer data = new StringBuffer();
        File file = new File(path,"saveBook.txt");

        if(!file.exists())
            return;
        try {
            //開啟 getFilesDir() 目錄底下名稱為 test.txt 檔案
            in = new FileInputStream (file);

            String date_tmp = null;
            //讀取該檔案的內容
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(in, "utf-8"));
            String line;

            while ((line = reader.readLine()) != null) {
                //  進行文字檔處理
                String [] tmp = line.split(",");

                if(tmp[3].equals("1")){
                    date_tmp = "一";
                }
                if(tmp[3].equals("2")){
                    date_tmp = "二";
                }
                if(tmp[3].equals("3")){
                    date_tmp = "三";
                }
                if(tmp[3].equals("4")){
                    date_tmp = "四";
                }
                if(tmp[3].equals("5")){
                    date_tmp = "五";
                }
                if(tmp[3].equals("6")){
                    date_tmp = "六";
                }
                if(tmp[3].equals("7")){
                    date_tmp = "日";
                }

                filesMap = new HashMap<>();
                filesMap.put(NAME_ITEM,  "課程名稱："+ tmp[0]);
                filesMap.put(TEACHER_ITEM, "授課老師：" + tmp[1]);
                filesMap.put(LOCATION_ITEM , "地點：" + tmp[2]);
                filesMap.put(DATE_ITEM , "星期：" +date_tmp);
                filesMap.put(TIME_ITEM , "時間：" + tmp[4]);
                filesList.add(filesMap);
            }


            simpleAdapter = new SimpleAdapter(this, filesList , R.layout.simple_adapter_curse,
                    new String[]{NAME_ITEM,TEACHER_ITEM,LOCATION_ITEM,DATE_ITEM,TIME_ITEM},
                    new int[]{R.id.textName, R.id.textTeacher,R.id.textLocation , R.id.textDate , R.id.textTime});
            listView.setAdapter(simpleAdapter);
            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){

                @Override
                public boolean onItemLongClick(AdapterView parent, View view, int position, long id) {
                    createDialog(view,position);
                    return true;
                }
            });


        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),"錯誤發生",Toast.LENGTH_LONG).show();
        } finally {
            try {
                in.close();
            } catch (Exception e) {

            }
        }
    }
    protected void Remove_Curse(int index){
        filesList.remove(index);
        file = new File(path,"saveBook.txt");

        String name;
        int list_size = filesList.size();
        //刪除檔案
        file.delete();
        for(int i = 0 ; i < list_size ; i++){

            name = (String) filesList.get(i).get(NAME_ITEM).toString().substring(5);
            String teacher = (String) filesList.get(i).get(TEACHER_ITEM).toString().substring(5);
            int date = Integer.parseInt(filesList.get(i).get(DATE_ITEM).toString().substring(3));
            String location = (String) filesList.get(i).get(LOCATION_ITEM).toString().substring(3);
            String time = (String) filesList.get(i).get(TIME_ITEM).toString().substring(3);


            String [] saveText = {name+",",teacher+",",location+",",date+",",time+"\n"} ;
            if(i==0){
                Curse_create.Save(file,saveText,false);
            }else{
                Curse_create.Save(file,saveText,true);
            }
        }

        listView.removeAllViewsInLayout();
        Create_Curse_View();
        listView.refreshDrawableState();

    }
    //控制返回鍵
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub

        if (keyCode == KeyEvent.KEYCODE_BACK) { // 攔截返回鍵
            Intent intent = new Intent(Curse_page.this, HomePage.class);
            startActivity(intent);
        }
        return true;
    }
}





