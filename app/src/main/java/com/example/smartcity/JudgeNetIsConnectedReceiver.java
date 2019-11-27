package com.example.smartcity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

/**
 * 声明网络是否连接成功的广播接受者
 * @author dell
 *
 */
public class JudgeNetIsConnectedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean isNotConnected=intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
        if(!isNotConnected || judgeNetIsConnected(context)){
            Toast.makeText(context, "网络连接成功！", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(context, "您的网络不给力，请检查网络！", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 判断网络连接是否成功
     * @param context 上下文对象
     * @return 网络连接是否成功
     */
    public static boolean judgeNetIsConnected(Context context) {
        ConnectivityManager connectivityManager= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo= connectivityManager.getActiveNetworkInfo();
        if(networkInfo==null){
            return false;
        }
        return networkInfo.isConnected();
    }

}
