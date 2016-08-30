package com.gl.retrofitdemo.download;

public interface ProgressResponseListener {
    void onResponseProgress(long bytesRead,long contentength,boolean done);
}
