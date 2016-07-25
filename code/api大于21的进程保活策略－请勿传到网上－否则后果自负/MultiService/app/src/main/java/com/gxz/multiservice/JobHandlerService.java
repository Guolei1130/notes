package com.gxz.multiservice;

import android.annotation.SuppressLint;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * ================================================
 * 作    者：顾修忠-guxiuzhong@youku.com/gfj19900401@163.com
 * 版    本：
 * 创建日期：16/7/24-下午10:00
 * 描    述：
 * 修订历史：
 * ================================================
 */
@SuppressLint("NewApi")
public class JobHandlerService extends JobService {


    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("-----------------INFO", "JobHandlerService-onCreate");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("-----------------INFO", "JobHandlerService-onStartCommand");

        return START_NOT_STICKY;
    }


    @Override
    public boolean onStartJob(JobParameters params) {
        Log.i("-----------------INFO", "JobHandlerService-onStartJob");

        Log.i("INFO", "onStartCommand");

        Toast.makeText(this, "start job:" + params.getJobId(), Toast.LENGTH_SHORT).show();

        this.startService(new Intent(this, LocalService.class));
        this.startService(new Intent(this, RemoteService.class));

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.i("-----------------INFO", "JobHandlerService-onStopJob");

        return false;
    }
}
