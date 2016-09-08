package com.example.kirito.simpletxtreader;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * Created by kirito on 2016/9/7.
 */
public class LoadData extends AsyncTask<String,Void,String> {
    private static final String TAG = "LoadData";
    private callBack mCallBack;

    @Override
    protected String doInBackground(String... params) {
        File file = new File(params[0]);
        BufferedReader br = null;
        StringBuffer sb = new StringBuffer();
        try{
            br  = new BufferedReader(new FileReader(file));
            String line = "";
            while ((line = br.readLine()) != null){
                sb.append(line);
                //sb.append('\n');
            }
            br.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return sb.toString();
    }

    @Override
    protected void onPostExecute(String s) {
        if (mCallBack != null){
            mCallBack.setData(s);
        }
    }

    public void setCallBack(callBack callBack){
        this.mCallBack = callBack;
    }

    public interface callBack{
        void setData(String data);
    }

}
