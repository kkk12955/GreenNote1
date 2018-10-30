package com.im.tku.greennote;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Folder_Note extends Activity {
    private static final String ROOT_path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/GreenNote/Note";
    private String [] Opition = {"開啟","分享","刪除","取消"};
    private static final String PRE_LEVEL = "上一頁";
    public static final int FIRST_ITEM = 0;
    public static final int SECOND_ITEM = 1;
    private String IMG_ITEM = "image";
    private String NAME_ITEM = "name";
    private List<Map<String, Object>> filesList;
    private List<String> names;
    private List<String> paths;
    private File[] files;
    private Map<String, Object> filesMap;
    private int[] fileImg = {
            R.drawable.folderblue,
            R.drawable.folder_note};
    private SimpleAdapter simpleAdapter;
    private ListView listView;
    private String nowPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder_note);
        getActionBar().hide(); //隱藏標題
        initData();
        initView();
    }

    private void initView() {
        simpleAdapter = new SimpleAdapter(this,
                filesList, R.layout.simple_adapter, new String[]{IMG_ITEM, NAME_ITEM},
                new int[]{R.id.image, R.id.text});
        listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(simpleAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final String target = paths.get(position);
                if(target.equals(ROOT_path)){
                    nowPath = paths.get(position);
                    getFileDirectory(ROOT_path );
                    simpleAdapter.notifyDataSetChanged();
                } else if(target.equals(PRE_LEVEL)){
                    nowPath = paths.get(position);
                    getFileDirectory(new File(nowPath).getParent());
                    simpleAdapter.notifyDataSetChanged();
                } else {
                    final File file = new File(target);
                    if (file.canRead()) {
                        if (file.isDirectory()) {
                            nowPath = paths.get(position);
                            getFileDirectory(paths.get(position));
                            simpleAdapter.notifyDataSetChanged();
                        } else{
                            final String string = paths.get(position);
                            AlertDialog.Builder dialog_list = new AlertDialog.Builder(Folder_Note.this);
                            dialog_list.setTitle("請選擇字體顏色");
                            dialog_list.setItems(Opition,new DialogInterface.OnClickListener(){
                                public void onClick(DialogInterface dialog, int which) {
                                    switch(which){
                                        case 0:
                                            Intent intent = new Intent(Folder_Note.this, Editpage.class);
                                            Bundle bundle = new Bundle();
                                            bundle.putString("TAG","load");
                                            intent.putExtras(bundle);

                                            try {

                                                FileOutputStream fos = openFileOutput("note_path", Context.MODE_PRIVATE);
                                                fos.write(string.getBytes());
                                                fos.close();
                                            } catch (FileNotFoundException e) {
                                                e.printStackTrace();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                            startActivity(intent);
                                            break;
                                        case 1:
                                            shareTo("分享",target,"分享");
                                            break;
                                        case 2:
                                            file.delete();
                                            getFileDirectory(nowPath);
                                            simpleAdapter.notifyDataSetChanged();

                                            break;
                                        case 3:

                                            break;
                                    }
                                }
                            });
                            dialog_list.show();




                        }
                    } else{
                        Toast.makeText(Folder_Note.this, R.string.can_not_read, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void initData() {
        filesList = new ArrayList<>();
        names = new ArrayList<>();
        paths = new ArrayList<>();
        getFileDirectory(ROOT_path);
    }

    private void getFileDirectory(String path){
        filesList.clear();
        paths.clear();
        if(!path.equals(ROOT_path)){
            //回根目錄
            filesMap = new HashMap<>();
            names.add(path);
            paths.add(FIRST_ITEM, path);
            filesMap.put(IMG_ITEM, fileImg[0]);
            filesMap.put(NAME_ITEM, path);
            filesList.add(filesMap);
            //回上一層
            filesMap = new HashMap<>();
            names.add(PRE_LEVEL);
            paths.add(SECOND_ITEM, new File(path).getParent());
            filesMap.put(IMG_ITEM, fileImg[0]);
            filesMap.put(NAME_ITEM, PRE_LEVEL);
            filesList.add(filesMap);
        }

        files = new File(path).listFiles();
        for(int i = 0; i < files.length; i++){
            filesMap = new HashMap<>();
            names.add(files[i].getName());
            paths.add(files[i].getPath());
            if(files[i].isDirectory()){
                filesMap.put(IMG_ITEM, fileImg[0]);
            }
            else {
                filesMap.put(IMG_ITEM, fileImg[1]);
            }
            filesMap.put(NAME_ITEM, files[i].getName());
            filesList.add(filesMap);
        }
    }
    private void shareTo(String subject, String body, String chooserTitle) {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
        sharingIntent.putExtra(Intent.EXTRA_STREAM, body);
        startActivity(Intent.createChooser(sharingIntent, chooserTitle));
    }

}
