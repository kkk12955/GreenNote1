package com.im.tku.greennote;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Note_create extends Activity {

    //筆記名稱
    final private String FILENAME = "note_path";

    private ImageButton Btn_create_note;
    private EditText editNoteName;
    private Spinner Spin_Cat;
    //分類選擇lsit
    private ArrayAdapter<String> cat_list;
    private ArrayList<String> list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_create);
        getActionBar().hide(); //隱藏標題

        Btn_create_note = (ImageButton) findViewById(R.id.Btn_create_note);
        editNoteName = (EditText) findViewById(R.id.ed_curse_name);


        list = new ArrayList<>();
        Intent intent = this.getIntent();
        list = intent.getStringArrayListExtra("cat_lsit");


        //下拉式選單生成
        Spin_Cat = (Spinner) super.findViewById(R.id.Spin_Cat);
        cat_list = new ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,list);
        Spin_Cat.setAdapter(cat_list);

        editNoteName.setTextColor(Color.GRAY);
        editNoteName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editNoteName.getText().toString().equals("請輸入檔案名稱")){
                    editNoteName.setTextColor(Color.BLACK);
                    editNoteName.setText("");
                }
            }
        });

        Btn_create_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editNoteName.getText().toString().equals("請輸入檔案名稱")){
                    Toast.makeText(Note_create.this,"請輸入檔案名稱！" , Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    String string = Environment.getExternalStorageDirectory().getAbsolutePath() + "/GreenNote/Note/"+Spin_Cat.getSelectedItem().toString()+"/"+editNoteName.getText().toString()+".bin";
                    FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
                    fos.write(string.getBytes());
                    fos.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Editpage.imgcheck = false;
                Intent intent = new Intent();
                intent.setClass(Note_create.this,Editpage.class);
                intent.putExtra("TAG","new");
                startActivity(intent);
            }
        });
    }
}
