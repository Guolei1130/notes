package com.gl.retrofitdemo.upload;

public interface ProgressListener {
    void progress(long bytesRead,long contentLength,boolean done);
}
