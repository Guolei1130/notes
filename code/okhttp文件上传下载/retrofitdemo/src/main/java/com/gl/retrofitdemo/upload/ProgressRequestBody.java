package com.gl.retrofitdemo.upload;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

public class ProgressRequestBody extends RequestBody {

    private final RequestBody requestBody;
    private final ProgressListener listener;

    private BufferedSink bufferedSink;

    public ProgressRequestBody(RequestBody body,ProgressListener listener){
        this.requestBody = body;
        this.listener = listener;
    }

    @Override
    public MediaType contentType() {
        return requestBody.contentType();
    }

    @Override
    public long contentLength() throws IOException {
        return requestBody.contentLength();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        if (bufferedSink == null){
            bufferedSink = Okio.buffer(sink(sink));
        }
        requestBody.writeTo(bufferedSink);
        bufferedSink.flush();
    }

    private Sink sink(Sink sink) throws IOException {
        return new ForwardingSink(sink) {
            long bytesWritten = 0L;
            long contentLength = contentLength();
            @Override
            public void write(Buffer source, long byteCount) throws IOException {
                super.write(source,byteCount);
                bytesWritten += byteCount;
                listener.progress(bytesWritten,contentLength,byteCount == contentLength);
            }
        };
    }
}
