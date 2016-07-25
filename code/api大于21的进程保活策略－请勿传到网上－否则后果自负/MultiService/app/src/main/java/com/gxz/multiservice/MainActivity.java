package com.gxz.multiservice;

import android.annotation.SuppressLint;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

@SuppressLint("NewApi")

public class MainActivity extends AppCompatActivity {
    private int mJobId = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        //双进程守护service

        this.startService(new Intent(this, LocalService.class));
        this.startService(new Intent(this, RemoteService.class));

        JobScheduler scheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        ComponentName componentName = new ComponentName(this, JobHandlerService.class);
        // JobInfo.Builder接收两个参数，
//      第一个参数是你要运行的任务的标识符，
//      第二个是这个Service组件的类名。
        JobInfo.Builder builder = new JobInfo.Builder(++mJobId, componentName);
        // builder允许你设置很多不同的选项来控制任务的执行
        // 这里设置的是让任务每隔10秒运行一次
        builder.setPeriodic(10000);
        scheduler.schedule(builder.build());

    }
}
