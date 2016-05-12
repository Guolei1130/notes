### Snackbar源码解析
* 视图的创建过程
* SnackBarManager的管理
* SnackBar的显示与隐藏

#### SnackBarManager的管理过程
从SnackBar的show方法说起。
```
public void show() {
        SnackbarManager.getInstance().show(mDuration, mManagerCallback);
    }
```
传入了2个参数，一个是显示的时间，一个是SnackbarManager.Callback的一个对象(用来发送显示和隐藏的消息)
那么，我们来看SnackBarManager的show方法，分开来看。
```
if (isCurrentSnackbarLocked(callback)) {
                // Means that the callback is already in the queue. We'll just update the duration
                mCurrentSnackbar.duration = duration;

                // If this is the Snackbar currently being shown, call re-schedule it's
                // timeout
                mHandler.removeCallbacksAndMessages(mCurrentSnackbar);
                scheduleTimeoutLocked(mCurrentSnackbar);
                return;
            } else if (isNextSnackbarLocked(callback)) {
                // We'll just update the duration
                mNextSnackbar.duration = duration;
            } else {
                // Else, we need to create a new record and queue it
                mNextSnackbar = new SnackbarRecord(duration, callback);
            }
```
上面的判断步骤如下：
 * 如果是当前正在显示的SnackBar对应的CallBack
  * 更新显示时长
  * 从消息队列中移除
  * scheduleTimeoutLocked()发送定时消息
 * 如果是下一个要显示的
  * 更新显示时长
 * 都不是 就创建一个SnackbarRecord对象

上面的一些判断有了，我们来看下面的一个片段，还是show方法中
```
if (mCurrentSnackbar != null && cancelSnackbarLocked(mCurrentSnackbar,
                    Snackbar.Callback.DISMISS_EVENT_CONSECUTIVE)) {
                // If we currently have a Snackbar, try and cancel it and wait in line
                return;
            } else {
                // Clear out the current snackbar
                mCurrentSnackbar = null;
                // Otherwise, just show it now
                showNextSnackbarLocked();
            }
```
 * 如果已经有在显示的了，直接返回
 * 没有的话 显示下一个。

在这里要说一下，mCurrentSnackbar 和mNextSnackbar 有点绕
* mCurrentSnackbar 当前正在显示的
* 和mNextSnackbar 下一个要显示的

```
private void showNextSnackbarLocked() {
        if (mNextSnackbar != null) {
            mCurrentSnackbar = mNextSnackbar;
            mNextSnackbar = null;

            final Callback callback = mCurrentSnackbar.callback.get();
            if (callback != null) {
                callback.show();
            } else {
                // The callback doesn't exist any more, clear out the Snackbar
                mCurrentSnackbar = null;
            }
        }
    }
```
这里会调用CallBack的show方法去显示。我们在回到SnackBar的mManagerCallback中，看到是立即发送一个MSG_SHOW的消息。我们在sHandler中看到调用了SnackBar的showView方法。这里是用来判断显示view的，我们具体不去关心如何显示的。仔细看能看到 animateViewIn();方法。在这个方法中，看到onViewShown方法，这个是用来干什么的？
```
private void onViewShown() {
        SnackbarManager.getInstance().onShown(mManagerCallback);
        if (mCallback != null) {
            mCallback.onShown(this);
        }
    }
```
恩 还看不出什么来，我们去SnackbarManager里面看onShow方法。
```
public void onShown(Callback callback) {
        synchronized (mLock) {
            if (isCurrentSnackbarLocked(callback)) {
                scheduleTimeoutLocked(mCurrentSnackbar);
            }
        }
    }
```
似乎在设置超时的处理。找到代码。
```
private void scheduleTimeoutLocked(SnackbarRecord r) {
        if (r.duration == Snackbar.LENGTH_INDEFINITE) {
            // If we're set to indefinite, we don't want to set a timeout
            return;
        }

        int durationMs = LONG_DURATION_MS;
        if (r.duration > 0) {
            durationMs = r.duration;
        } else if (r.duration == Snackbar.LENGTH_SHORT) {
            durationMs = SHORT_DURATION_MS;
        }
        mHandler.removeCallbacksAndMessages(r);
        mHandler.sendMessageDelayed(Message.obtain(mHandler, MSG_TIMEOUT, r), durationMs);
    }
```
```
mHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                switch (message.what) {
                    case MSG_TIMEOUT:
                        handleTimeout((SnackbarRecord) message.obj);
                        return true;
                }
                return false;
            }
        });
```
```
private void handleTimeout(SnackbarRecord record) {
        synchronized (mLock) {
            if (mCurrentSnackbar == record || mNextSnackbar == record) {
                cancelSnackbarLocked(record, Snackbar.Callback.DISMISS_EVENT_TIMEOUT);
            }
        }
    }
```
```
private boolean cancelSnackbarLocked(SnackbarRecord record, int event) {
        final Callback callback = record.callback.get();
        if (callback != null) {
            // Make sure we remove any timeouts for the SnackbarRecord
            mHandler.removeCallbacksAndMessages(record);
            callback.dismiss(event);
            return true;
        }
        return false;
    }
```
直到这里才调用callback的dismiss方法隐藏。
```
@Override
        public void dismiss(int event) {
            sHandler.sendMessage(sHandler.obtainMessage(MSG_DISMISS, event, 0, Snackbar.this));
        }
```
渐渐调用到了hideView方法去隐藏。