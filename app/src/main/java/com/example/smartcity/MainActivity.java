package com.example.smartcity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private JudgeNetIsConnectedReceiver judgeNetIsConnectedReceiver = new JudgeNetIsConnectedReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
        申请定位权限
         */
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }
        /*
        开启网络状态监听
         */
        IntentFilter intentFilter=new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        this.registerReceiver(judgeNetIsConnectedReceiver, intentFilter);
        setContentView(R.layout.activity_initial);
    }

    public void buttonOnClick(View v) {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, SearchActivity.class);
        startActivity(intent);
    }

    /*
    获取权限的回调函数
     */
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        int cnt = 0;
        for(String recPermission : permissions) {
            if(recPermission.equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                if(grantResults[cnt] == PackageManager.PERMISSION_DENIED) {
                    /*
                    无法获取定位权限，app无法继续运行
                     */
                    Toast.makeText(this, "获取定位权限失败", Toast.LENGTH_SHORT).show();
                    this.finish();
                }
            }
            cnt++;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        /*
        停止网络状态的监听
         */
        if(this.judgeNetIsConnectedReceiver!=null){
            this.unregisterReceiver(judgeNetIsConnectedReceiver);
        }
    }
}
