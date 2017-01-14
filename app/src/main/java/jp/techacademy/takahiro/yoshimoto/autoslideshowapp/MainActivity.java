package jp.techacademy.takahiro.yoshimoto.autoslideshowapp;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int PERMISSIONS_REQUEST_CODE = 100;

    Cursor cursor;
    Uri imageUri;
    ImageView imageView;
    Timer timer;
    MyTimer myTimer;
    Handler handle;
    Button button1;
    Button button2;
    Button button3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //android6.0以降の場合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //パーミッションの許可状態を確認する
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                //許可されている
                getContentsInfo();
            } else {
                //許可されていないので許可ダイアログを表示する
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CODE);
            }
            //android5系以下の場合
        } else {
            getContentsInfo();
        }

        //ボタンの設定を行う
        button1 = (Button)findViewById(R.id.button1);
        button2 = (Button)findViewById(R.id.button2);
        button3 = (Button)findViewById(R.id.button3);
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults){
        switch(requestCode){
            case PERMISSIONS_REQUEST_CODE:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    getContentsInfo();
                }
                break;
            default:
                break;
        }
    }


    public void getContentsInfo() {
        //画像を動かせるようにする
        imageView = (ImageView) findViewById(R.id.imageView);

        //画像の情報を取得する
        ContentResolver resolver = getContentResolver();
        cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, //データの種類
                null, //項目(null = 全項目)
                null, // フィルタ条件(null = フィルタなし)
                null, // フィルタ用パラメーター
                null // ソート(nullソートなし)
        );

        if(cursor.moveToFirst()){
            //indexからIDを取得し、そのIDから画像のURIを取得する
            int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
            Long id = cursor.getLong(fieldIndex);
            imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
            Log.d("photo", "uri : " + imageUri.toString());
            imageView.setImageURI(imageUri);
        }
    }

    private void onClickButton1(){
        //進むボタンを押したときの動作
        //次の画面に推移できるかどうかで分ける

        if(cursor.moveToNext()){
            int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
            Long id = cursor.getLong(fieldIndex);
            imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
            Log.d("photo", "uri : " + imageUri.toString());
            imageView.setImageURI(imageUri);
        }else{
            cursor.moveToFirst();
            int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
            Long id = cursor.getLong(fieldIndex);
            imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
            Log.d("photo", "uri : " + imageUri.toString());
            imageView.setImageURI(imageUri);
        }
    }
    private void onClickButton2() {
        //戻るボタンを押したときの動作

        if(cursor.moveToPrevious()){
            int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
            Long id = cursor.getLong(fieldIndex);
            imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
            Log.d("photo", "uri : " + imageUri.toString());
            imageView.setImageURI(imageUri);
        }else{
            cursor.moveToLast();
            int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
            Long id = cursor.getLong(fieldIndex);
            imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
            Log.d("photo", "uri : " + imageUri.toString());
            imageView.setImageURI(imageUri);
        }
    }


    int num = 0; //「再生」「停止」を切り替えるためのnumber。
                 // 「再生」= 0, 「停止」= 1

    private void onClickButton3() {

        if (num == 0) {
            //「再生」となっているときに押したときの動作
            button3.setText("停止");
            button1.setEnabled(false);
            button2.setEnabled(false);
            num = 1;
            int firstInterval = 2000; //起動までが1秒
            int interval = 2000;  //写真が変わる間隔が2秒

            timer = new Timer();
            myTimer = new MyTimer();
            handle = new Handler();

            //タイマーを開始する
            timer.schedule(myTimer, firstInterval, interval);

        }else if(num == 1){
            //「停止」となっているときに押したときの動作
            button3.setText("再生");
            button1.setEnabled(true);
            button2.setEnabled(true);
            num = 0;

            //タイマーを停止する
            timer.cancel();
        }
    }

    class MyTimer extends TimerTask{
        @Override
        public void run(){
            handle.post(new Runnable() {
                @Override
                public void run() {
                    onClickButton1();
                }
            });
        }
    }

   @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.button1:
                onClickButton1();
                break;

            case R.id.button2:
                onClickButton2();
                break;

            case R.id.button3:
                onClickButton3();
                break;
        }

    }

}