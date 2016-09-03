### 0.需求描述

Html文件中的css样式，可能在网页中显示的是一个样式，但是，这种样式在手机端却不合适，因此需要替换。

### 1.解决办法

一切尽在代码中。

```
        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return super.shouldOverrideUrlLoading(view, url);
            }
            @SuppressWarnings("deprecation")
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                // TODO: 16-9-3 拦截到指定的css资源，然后替换成本地的
                /** 这个方法是API 11 到 API21的，API21 以上，
                 *  {@link WebViewClient#shouldInterceptRequest(WebView, WebResourceRequest)}
                 * **/
                if (url.contains(".css")) {
                    Log.e(TAG, "shouldInterceptRequest: " + url );
                    WebResourceResponse resourceResponse = null;
                    InputStream in = null;
                    try {
                        in = getAssets().open("css.css");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    resourceResponse = new WebResourceResponse("text/css", "UTF-8", in);
                    if (resourceResponse != null)
                        return resourceResponse;
                }
                return super.shouldInterceptRequest(view, url);
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {

                return super.shouldInterceptRequest(view, request);
            }
        });
```