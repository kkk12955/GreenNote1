package com.im.tku.greennote;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class Folder extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder);

        getActionBar().hide(); //隱藏標題

        //繼承介面物件
        final ImageButton btn_doc = (ImageButton)findViewById(R.id.Btn_Doc);
        final ImageButton btn_img = (ImageButton)findViewById(R.id.Btn_Img);

        btn_doc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Folder.this, Folder_Note.class);
                startActivity(intent);
            }
        });

        btn_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Folder.this, Folder_Image.class);
                startActivity(intent);
            }
        });



    }
}