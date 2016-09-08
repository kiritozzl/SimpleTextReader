package com.example.kirito.simpletxtreader;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.StringReader;

/**
 * Created by kirito on 2016/9/7.
 */
public class ContentActivity extends AppCompatActivity {
    private EditText et;
    private String path;
    private String old_data;
    private String new_data;
    private LoadData load;

    private static final String TAG = "ContentActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content);

        et = (EditText) findViewById(R.id.et);
        path = getIntent().getStringExtra("path");
        setTitle(path);
        load = new LoadData();
        load.setCallBack(new LoadData.callBack() {
            @Override
            public void setData(String data) {
                et.setText(data);
                et.setSelection(0);
                old_data = data;
            }
        });
        load.execute(path);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getRepeatCount() == 0 && keyCode == KeyEvent.KEYCODE_BACK){
            new_data = et.getText().toString();
            Log.e(TAG, "onKeyDown: old---"+old_data );
            Log.e(TAG, "onKeyDown: new---"+new_data );
            if (old_data.equals(new_data)){
                this.finish();
            }else {
                showAlertDialog(new_data);
            }
        }
        return true;
    }

    private void showAlertDialog(final String data){
        AlertDialog.Builder builder = new AlertDialog.Builder(ContentActivity.this).setTitle("是否保存更改内容？")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveChange(data);
                        //ContentActivity.this.finish();
                        Intent i = new Intent(ContentActivity.this,MainActivity.class);
                        startActivity(i);
                        ContentActivity.this.finish();
                    }
                }).setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ContentActivity.this.finish();
                    }
                });
        builder.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ContentActivity.this.finish();
    }

    private void saveChange(String data){
        OutputStreamWriter osw = null;
        try{
            osw = new OutputStreamWriter(new FileOutputStream(new File(path)));
            osw.write(data);
            osw.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
