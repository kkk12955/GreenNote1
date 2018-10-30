package com.im.tku.greennote;

/**
 * Created by kk on 2017/7/26.
 */
import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Curse_create extends Activity {

    private String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/GreenNote" ; //資料夾路徑
    private int s_hour;
    private int s_minute;
    private EditText ed_name,ed_location,ed_teacher,ed_curse_start,ed_curse_end;
    private TextView tvName,tvPlace,tvStart,tvEnd;
    private Spinner Spin_date;
    private ArrayAdapter<CharSequence> date_list;
    private Spinner ed_date;
    private Button add;
    private static List<Curse> list;
    private static int TAG = -1;
    private String [] saveText;
    //繼承介面物件
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_curse_create);


        getActionBar().hide(); //隱藏標題
        initConstruction();


        final Bundle bundle =this.getIntent().getExtras();
        if(bundle!=null){

            this.ed_name.setText(bundle.getString("name"));
            this.ed_location.setText(bundle.getString("location"));
            this.ed_teacher.setText(bundle.getString("teacher"));
            this.Spin_date.setSelection(bundle.getInt("date")-1);
            String time = bundle.getString("time");
            String [] t = time.split("~");
            this.ed_curse_start.setText(t[0]);
            this.ed_curse_end.setText(t[1]);
            TAG = bundle.getInt("TAG");

        }






        //動作實作
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //新增按鈕動作

            try{


                //擷取使用者資料
                String name = ed_name.getText().toString();
                String teacher = ed_teacher.getText().toString();
                String location = ed_location.getText().toString();
                String date = Spin_date.getSelectedItem().toString();
                String start = ed_curse_start.getText().toString();
                String end = ed_curse_end.getText().toString();
                int date_week = 0;

                //判斷資料是否正確
                if(name.equals("")){
                    tvName.setTextColor(Color.RED);
                    throw new MyException();
                }else{
                    tvName.setTextColor(Color.BLACK);
                }
                if(start.equals("點選時間")){
                    tvStart.setTextColor(Color.RED);
                    throw new MyException();
                }else{
                    tvStart.setTextColor(Color.BLACK);
                }
                if(end.equals("點選時間")){
                    tvEnd.setTextColor(Color.RED);
                    throw new MyException();
                }else{
                    tvEnd.setTextColor(Color.BLACK);
                }
                //日期 星期一 = 1
                date_week = Spin_date.getSelectedItemPosition()+1;

                // 地點
                if(location.equals("選填")){
                    location = "無";
                }
                //寫入課程資料進檔案
                File file = new File(path); //課程檔案資料夾路徑
                if(!file.exists()){ //若資料夾不存在
                    file.mkdir(); //建立資料夾
                }

                saveText = new String[]{name + ",", teacher + ",", location + ",", date_week + ",", start + "~" + end + "\n"};

                file = new File(path,"saveBook.txt"); //課程資料

                //判斷是否是編輯更改資料
                if(TAG== -1){
                    Save(file,saveText,true); //儲存資料
                    reload_sort(file);//整理課程資料
                }else{ //修正資料
                    String [] s = {name + ",", teacher + ",", location + ",", date_week + ",", start + "~" + end + "\n"};
                    Log.e("HHH", String.valueOf(TAG));
                    reload_sort(file,TAG,s);//整理課程資料
                }


                file = new File(path+"/Note/"+ed_name.getText()); //建立筆記資料夾
                file.mkdir();
                file = new File(path+"/Image/"+ed_name.getText()); //建立照片資料夾
                file.mkdir();


                Intent intent = new Intent(Curse_create.this, Curse_page.class);
                startActivity(intent);


            }catch(MyException e){

            }

            }
        });
    }

    //初始化建構
    protected void initConstruction(){
        //繼承EditText
        ed_name = (EditText) findViewById(R.id.ed_name);
        ed_teacher = (EditText) findViewById(R.id.ed_teacher);
        ed_location = (EditText) findViewById(R.id.ed_location);
        ed_curse_start = (EditText) findViewById(R.id.ed_curse_start);
        ed_curse_end = (EditText) findViewById(R.id.ed_curse_End);

        //繼承TextView
        tvName = (TextView) findViewById(R.id.tvName);
        tvPlace = (TextView) findViewById(R.id.tvLocation);
        tvStart = (TextView) findViewById(R.id.textStart);
        tvEnd = (TextView) findViewById(R.id.textEnd);

        //下拉視窗生成
        Spin_date = (Spinner) super.findViewById(R.id.Spn_date);
        date_list = ArrayAdapter.createFromResource(this,R.array.date_list,android.R.layout.simple_spinner_dropdown_item);
        Spin_date.setAdapter(date_list);

        //繼承按鈕及下拉
        ed_date = (Spinner) findViewById(R.id.Spn_date);
        add = (Button) findViewById(R.id.Btn_add);

        //阻止鍵盤彈出

        ed_curse_start.setOnTouchListener(new View.OnTouchListener(){
            public boolean onTouch(View v, MotionEvent event){
                ed_curse_start.setInputType(InputType.TYPE_NULL);
                return false;
            }
        });

        ed_curse_end.setOnTouchListener(new View.OnTouchListener(){
            public boolean onTouch(View v, MotionEvent event){
                ed_curse_end.setInputType(InputType.TYPE_NULL);
                return false;
            }
        });

        //設定時間選擇器
        ed_curse_start.setOnClickListener(new EditText.OnClickListener(){
            public void onClick(View v){
                showTimePickDiolog(ed_curse_start);
            }
        });

        ed_curse_end.setOnClickListener(new EditText.OnClickListener(){
            public void onClick(View v){
                showTimePickDiolog(ed_curse_end);
            }
        });
    }

    //寫入檔案 檔案 字串 是否複寫
    //true = 複寫
    //false =重寫
    public static void Save(File file,String[] data,boolean b){
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file,b);

            for(int i =0 ; i <data.length ; i++) {
                fos.write(data[i].getBytes());

            }
            fos.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //實現時間選擇器 選擇時間並顯示
    public void showTimePickDiolog(final EditText time){

        final Calendar c = Calendar.getInstance();
        s_hour = c.get(Calendar.HOUR_OF_DAY);
        s_minute = c.get(Calendar.MINUTE);
        TimePickerDialog tpd = new TimePickerDialog(this,new TimePickerDialog.OnTimeSetListener(){
            public void onTimeSet(TimePicker view , int s_hour,int s_minute){
                time.setText(s_hour+"："+String.format("%02d", s_minute));
            }
        },s_hour,s_minute,false);
        tpd.show();
    }

    //讀取並排列
    public void reload_sort(File file) {
        list = new ArrayList<Curse>();
        FileInputStream in = null;
        StringBuffer data = new StringBuffer();
        //讀取多少課程
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

                list.add(new Curse(tmp[0],tmp[1],tmp[2]
                        ,Integer.parseInt(tmp[3]),new Time(Integer.parseInt(tmp_1[0]),Integer.parseInt(tmp_1[1])), new Time(Integer.parseInt(tmp_2[0]),Integer.parseInt(tmp_2[1]))));

            }


        }
        catch (Exception e) {  }
        finally {
            try {
                in.close();
            } catch (Exception e) { }
        }

        //進行排序 依據星期
        //排序順序為 日期 → 開始時間
        Collections.sort(list, new Comparator<Curse>(){
            @Override
            public int compare(Curse o1, Curse o2) {
                if(o1.get_Curse_Date() == o2.get_Curse_Date()){
                    return o1.get_Curse_StartTime().h - o2.get_Curse_StartTime().h;
                }else{
                    return o1.get_Curse_Date() - o2.get_Curse_Date();
                }

            }
        });

        FileOutputStream fos = null;
        try {

            fos = new FileOutputStream(file,false);
            //讀取該檔案的內容
            for(int i =0 ; i <list.size() ; i++) {
                fos.write(list.get(i).toString().getBytes());

            }

        } catch (Exception e) {
        }

        finally {
            try {
                fos.close();
            } catch (Exception e) {}
        }


    }

    //編輯更改
    public void reload_sort(File file,int position,String[] s) {
        list = new ArrayList<Curse>();
        FileInputStream in = null;
        StringBuffer data = new StringBuffer();
        //讀取多少課程
        try {
            //開啟 getFilesDir() 目錄底下名稱為 test.txt 檔案
            in = new FileInputStream(file);
            //讀取該檔案的內容
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(in, "utf-8"));
            //讀取字串
            String line;
            int i=0;
            while ((line = reader.readLine()) != null) {
                String temp = "";
                if(TAG == i){
                    for(int j = 0 ; j <s.length ;j++){
                         temp += s[j];
                    }
                    line = temp;
                    Log.e("HHH",temp);
                    TAG = -1;
                }

                String [] tmp = line.split(",");
                String [] tmp2 = tmp[4].split("~");
                String [] tmp_1 = tmp2[0].split("："); //Start 時間
                String [] tmp_2 = tmp2[1].split("："); //End時間

                tmp_2[1] = tmp_2[1].replace("\n","");
                // lisst 名稱 地點 日期 開始時間 結束時間

                list.add(new Curse(tmp[0],tmp[1],tmp[2]
                        ,Integer.parseInt(tmp[3]),new Time(Integer.parseInt(tmp_1[0]),Integer.parseInt(tmp_1[1])), new Time(Integer.parseInt(tmp_2[0]),Integer.parseInt(tmp_2[1]))));

                i++;
            }


        }
        catch (Exception e) { Log.e("HHH",e.getMessage()); }
        finally {
            try {
                in.close();
            } catch (Exception e) { }
        }

        //進行排序 依據星期
        //排序順序為 日期 → 開始時間
        Collections.sort(list, new Comparator<Curse>(){
            @Override
            public int compare(Curse o1, Curse o2) {
                if(o1.get_Curse_Date() == o2.get_Curse_Date()){
                    return o1.get_Curse_StartTime().h - o2.get_Curse_StartTime().h;
                }else{
                    return o1.get_Curse_Date() - o2.get_Curse_Date();
                }

            }
        });

        FileOutputStream fos = null;
        try {

            fos = new FileOutputStream(file,false);
            //讀取該檔案的內容
            for(int i =0 ; i <list.size() ; i++) {
                fos.write(list.get(i).toString().getBytes());

            }

        } catch (Exception e) {
        }

        finally {
            try {
                fos.close();
            } catch (Exception e) {}
        }


    }

    public static void load_list(ArrayList list){
        Curse_create.list = list;
    }

    //例外建構
    class MyException extends Exception{

        public MyException(){
        }
    }

    //控制返回鍵
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub

        if (keyCode == KeyEvent.KEYCODE_BACK) { // 攔截返回鍵
            Intent intent = new Intent(Curse_create.this, Curse_page.class);
            startActivity(intent);
        }
        return true;
    }


}
