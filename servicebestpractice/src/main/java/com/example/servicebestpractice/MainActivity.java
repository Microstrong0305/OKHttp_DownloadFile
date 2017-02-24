package com.example.servicebestpractice;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private DownloadService.DownloadBinder downloadBinder;

    private ServiceConnection connection=new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            downloadBinder=(DownloadService.DownloadBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button startDownload=(Button) findViewById(R.id.start_download);
        startDownload.setOnClickListener(this);
        Button pauseDownload=(Button) findViewById(R.id.pause_download);
        pauseDownload.setOnClickListener(this);
        Button cancelDownload=(Button)findViewById(R.id.cancel_download);
        cancelDownload.setOnClickListener(this);
        Intent intent=new Intent(this,DownloadService.class);
        //这一点至关重要，因为启动服务可以保证DownloadService一直在后台运行，绑定服务则可以让MaiinActivity和DownloadService
        //进行通信，因此两个方法的调用都必不可少。
        startService(intent);  //启动服务
        bindService(intent,connection,BIND_AUTO_CREATE);//绑定服务
        /**
         *运行时权限处理：我们需要再用到权限的地方，每次都要检查是否APP已经拥有权限
         * 下载功能，需要些SD卡的权限，我们在写入之前检查是否有WRITE_EXTERNAL_STORAGE权限,没有则申请权限
         */
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
    }

    public void onClick(View v){
        if(downloadBinder==null){
            return;
        }
        switch (v.getId()){
            case R.id.start_download:
                //String url="http://raw.githubusercontent.com/guolindev/eclipse/master/eclipse-inst-win64.exe";
                String url="http://10.0.2.2:8080/ChromeSetup.exe";
                downloadBinder.startDownload(url);
                break;
            case R.id.pause_download:
                downloadBinder.pauseDownload();
                break;
            case R.id.cancel_download:
                downloadBinder.cancelDownload();
                break;
            default:
                break;
        }
    }

    /**
     * 用户选择允许或拒绝后,会回调onRequestPermissionsResult
     * @param requestCode  请求码
     * @param permissions
     * @param grantResults  授权结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults) {
       switch (requestCode){
           case 1:
               if(grantResults.length>0&&grantResults[0]!= PackageManager.PERMISSION_GRANTED){
                   Toast.makeText(this,"拒绝权限将无法使用程序",Toast.LENGTH_SHORT).show();
                   finish();
               }
           break;
       }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //解除绑定服务
        unbindService(connection);
    }
}
