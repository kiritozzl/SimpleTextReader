package com.example.kirito.simpletxtreader;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ListView lv;
    private String sd_path;
    private File sd_file;
    private List<String> file_name;
    private List<String> file_txt_path;
    private List<String> file_size;
    private List<Item> itemList;
    private ProgressDialog dialog;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lv = (ListView) findViewById(R.id.lv);
        sd_path = Environment.getExternalStorageDirectory().getAbsolutePath();
        setTitle("当前SD卡路径:"+sd_path);
        sd_file = new File(sd_path);
        //Log.e(TAG, "onCreate: sd_file---"+sd_file.getAbsolutePath() );
        file_name = new ArrayList<>();
        file_txt_path = new ArrayList<>();
        file_size = new ArrayList<>();

        buildDialog();
        loadList loadList = new loadList();
        loadList.execute();
    }

    private class loadList extends AsyncTask<Void,Void,Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            listFileTxt(sd_file);
            initItemList();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            ListAdapter adpter = new ListAdapter(MainActivity.this,itemList);
            lv.setClickable(true);
            lv.setAdapter(adpter);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String path = ((Item)parent.getItemAtPosition(position)).getPath();
                    //Log.e(TAG, "onItemClick: path ---"+path );
                    Intent intent = new Intent(MainActivity.this,ContentActivity.class);
                    intent.putExtra("path",path);
                    startActivity(intent);
                    MainActivity.this.finish();
                }
            });
            dialog.dismiss();
        }
    }

    private void buildDialog(){
        dialog = new ProgressDialog(MainActivity.this);
        dialog.setTitle("加载中...");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    }

    private void initItemList(){
        itemList = new ArrayList<>();
        for (int i = 0; i < file_size.size(); i++) {
            Item item = new Item();
            item.setName(file_name.get(i));
            item.setPath(file_txt_path.get(i));
            item.setSize(file_size.get(i));
            itemList.add(item);
        }
    }

    private void listFileTxt(File file){
        File [] files = file.listFiles();
        try{
            for (File f : files) {
                if (!f.isDirectory()){
                    if (f.getName().endsWith(".txt")){
                        //获取并计算文件大小
                        long size = f.length();
                        String t_size = "";
                        if (size <= 1024){
                            t_size = size + "B";
                        }else if (size > 1024 && size <= 1024 * 1024){
                            size /= 1024;
                            t_size = size + "KB";
                        }else {
                            size = size / (1024 * 1024);
                            t_size = size + "MB";
                        }
                        file_size.add(t_size);//文件大小
                        file_name.add(f.getName());//文件名称
                        file_txt_path.add(f.getAbsolutePath());//文件路径
                    }
                }else if (f.isDirectory()){
                    //如果是目录，迭代进入该目录
                    listFileTxt(f);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
