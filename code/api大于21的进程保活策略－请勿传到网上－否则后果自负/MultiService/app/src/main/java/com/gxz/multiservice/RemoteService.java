package com.gxz.multiservice;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.gxz.multiservice.inter.MyProcess;

public class RemoteService extends Service {

    private MySerC mySerC;
    private MyBinder binder;

    @Override
    public IBinder onBind(Intent intent) {

        Log.i("-------->>>>>INFO", "RemoteService-onBind");

        binder = new MyBinder();
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (binder == null)
            binder = new MyBinder();
        mySerC = new MySerC();

        Log.i("-------->>>>>INFO", "RemoteService-onCreate");

    }

    class MyBinder extends MyProcess.Stub {

        @Override
        public String getPrecessName() throws RemoteException {
            return "RemoteService";
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i("-------->>>>>INFO", "RemoteService-onStartCommand");


        this.bindService(new Intent(this, LocalService.class), mySerC, Context.BIND_IMPORTANT);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("Foreground");
        builder.setContentText("I am a foreground service");
        builder.setContentInfo("Content Info");
        builder.setWhen(System.currentTimeMillis());
        PendingIntent pendingIntent = PendingIntent.getService(this, 1, intent, 0);
        builder.setContentIntent(pendingIntent);
        Notification notification = builder.build();

        //让改service前台✅.避免手机休眠时系统自动杀掉该服务
        //如果id为0,状态栏的notification则不会显示
        startForeground(0, notification);

        return START_STICKY;
    }

    class MySerC implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i("-------->>>>>INFO", "RemoteService-onServiceConnected");

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i("-------->>>>>INFO", "RemoteService-onServiceDisconnected");
            RemoteService.this.startService(new Intent(RemoteService.this, LocalService.class));
            RemoteService.this.bindService(new Intent(RemoteService.this, LocalService.class),
                    mySerC, Context.BIND_IMPORTANT);
        }

    }
}
