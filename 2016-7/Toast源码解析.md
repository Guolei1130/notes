### 1.Toast的用法

```

Toast.makeText(this, "Toast", Toast.LENGTH_SHORT).show();

```

### 2.makeText入手

```

    public static Toast makeText(Context context, CharSequence text, @Duration int duration) {

        Toast result = new Toast(context);



        LayoutInflater inflate = (LayoutInflater)

                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View v = inflate.inflate(com.android.internal.R.layout.transient_notification, null);

        TextView tv = (TextView)v.findViewById(com.android.internal.R.id.message);

        tv.setText(text);

        

        result.mNextView = v;

        result.mDuration = duration;



        return result;

    }

```

只是加载了一个布局。接下来我们看show方法

### 3.Toast#show

```

    public void show() {

        if (mNextView == null) {

            throw new RuntimeException("setView must have been called");

        }



        INotificationManager service = getService();

        String pkg = mContext.getOpPackageName();

        TN tn = mTN;

        tn.mNextView = mNextView;



        try {

            service.enqueueToast(pkg, tn, mDuration);

        } catch (RemoteException e) {

            // Empty

        }

    }

```

* 如果mNextView==null，抛出异常

* 获取INotificationManager

```

    static private INotificationManager getService() {

        if (sService != null) {

            return sService;

        }

        sService = INotificationManager.Stub.asInterface(ServiceManager.getService("notification"));

        return sService;

    }

```

* 将toast插入队列。



** 这里的INotificationManager，是在启动的时候加载到ServiceManager的mCache里的，关于如何加载的这里略过，对应的服务端实现在NotificationManagerService里 **



### 4.NotificationManagerService里的enqueueToast方法。

```

    @Override

    public void enqueueToast(String pkg, ITransientNotification callback, int duration)

    {

        //省略代码



        final boolean isSystemToast = isCallerSystem() || ("android".equals(pkg));



        //省略代码



        synchronized (mToastQueue) {

            int callingPid = Binder.getCallingPid();

            long callingId = Binder.clearCallingIdentity();

            try {

                ToastRecord record;

                int index = indexOfToastLocked(pkg, callback);

                // If it's already in the queue, we update it in place, we don't

                // move it to the end of the queue.

                if (index >= 0) {

                    record = mToastQueue.get(index);

                    record.update(duration);

                } else {

                    // Limit the number of toasts that any given package except the android

                    // package can enqueue.  Prevents DOS attacks and deals with leaks.

                    if (!isSystemToast) {

                        int count = 0;

                        final int N = mToastQueue.size();

                        for (int i=0; i<N; i++) {

                            final ToastRecord r = mToastQueue.get(i);

                            if (r.pkg.equals(pkg)) {

                                count++;

                                if (count >= MAX_PACKAGE_NOTIFICATIONS) {

                                    Slog.e(TAG, "Package has already posted " + count

                                            + " toasts. Not showing more. Package=" + pkg);

                                    return;

                                }

                            }

                        }

                    }



                    record = new ToastRecord(callingPid, pkg, callback, duration);

                    mToastQueue.add(record);

                    index = mToastQueue.size() - 1;

                    keepProcessAliveLocked(callingPid);

                }

                // If it's at index 0, it's the current toast.  It doesn't matter if it's

                // new or just been updated.  Call back and tell it to show itself.

                // If the callback fails, this will remove it from the list, so don't

                // assume that it's valid after this.

                if (index == 0) {

                    showNextToastLocked();

                }

            } finally {

                Binder.restoreCallingIdentity(callingId);

            }

        }

    }

```

代码略长，听我慢慢道来、

* 算出当前Toast在ToastQueue中的索引，

* 如果>=0,怎说明已经在队列中，更新时间即可

* ! >= 0的情况下

 * 如果不是系统Toast，就判断当前包名下，队列中有多少个toast，>=50，直接返回，并更新index

 * 将其包装一下加入队列

* 设置该Toast所在的进程为前台进程

* 如果index为0，显示。



### 5.NotificationManagerService#showNextToastLocked



```

    void showNextToastLocked() {

        ToastRecord record = mToastQueue.get(0);

        while (record != null) {

            if (DBG) Slog.d(TAG, "Show pkg=" + record.pkg + " callback=" + record.callback);

            try {

                record.callback.show();

                scheduleTimeoutLocked(record);

                return;

            } catch (RemoteException e) {

                Slog.w(TAG, "Object died trying to show notification " + record.callback

                        + " in package " + record.pkg);

                // remove it from the list and let the process die

                int index = mToastQueue.indexOf(record);

                if (index >= 0) {

                    mToastQueue.remove(index);

                }

                keepProcessAliveLocked(record.pid);

                if (mToastQueue.size() > 0) {

                    record = mToastQueue.get(0);

                } else {

                    record = null;

                }

            }

        }

    }

```

代码比较简单，调用ToastRecore.callback的show方法去显示，并且发一个延时消息。

```

    private void scheduleTimeoutLocked(ToastRecord r)

    {

        mHandler.removeCallbacksAndMessages(r);

        Message m = Message.obtain(mHandler, MESSAGE_TIMEOUT, r);

        long delay = r.duration == Toast.LENGTH_LONG ? LONG_DELAY : SHORT_DELAY;

        mHandler.sendMessageDelayed(m, delay);

    }

```

那么，这里的record的callback的show方法是什么呢。record是我们从队列中取出来的，他的初始化方法在enqueueToast方法中。

```

record = new ToastRecord(callingPid, pkg, callback, duration);

```

而这里的callback是enqueueToast的参数，也就是说，是我们在Toast中出传入的。Toast中相关代码如下

```

service.enqueueToast(pkg, tn, mDuration);

```

这里的tn是在toast初始化的时候初始化的。因此，将会掉TN的show方法。

### 6.TN#show

```

        @Override

        public void show() {

            if (localLOGV) Log.v(TAG, "SHOW: " + this);

            mHandler.post(mShow);

        }

```

```

        final Runnable mShow = new Runnable() {

            @Override

            public void run() {

                handleShow();

            }

        };

```

会看到，最后就会调用handleShow方法。

### 7.TN#handleShow

```

    public void handleShow() {

        if (localLOGV) Log.v(TAG, "HANDLE SHOW: " + this + " mView=" + mView

                + " mNextView=" + mNextView);

        if (mView != mNextView) {

            // remove the old view if necessary

            handleHide();

            mView = mNextView;

            Context context = mView.getContext().getApplicationContext();

            String packageName = mView.getContext().getOpPackageName();

            if (context == null) {

                context = mView.getContext();

            }

            mWM = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);

            // We can resolve the Gravity here by using the Locale for getting

            // the layout direction

            final Configuration config = mView.getContext().getResources().getConfiguration();

            final int gravity = Gravity.getAbsoluteGravity(mGravity, config.getLayoutDirection());

            mParams.gravity = gravity;

            if ((gravity & Gravity.HORIZONTAL_GRAVITY_MASK) == Gravity.FILL_HORIZONTAL) {

                mParams.horizontalWeight = 1.0f;

            }

            if ((gravity & Gravity.VERTICAL_GRAVITY_MASK) == Gravity.FILL_VERTICAL) {

                mParams.verticalWeight = 1.0f;

            }

            mParams.x = mX;

            mParams.y = mY;

            mParams.verticalMargin = mVerticalMargin;

            mParams.horizontalMargin = mHorizontalMargin;

            mParams.packageName = packageName;

            if (mView.getParent() != null) {

                if (localLOGV) Log.v(TAG, "REMOVE! " + mView + " in " + this);

                mWM.removeView(mView);

            }

            if (localLOGV) Log.v(TAG, "ADD! " + mView + " in " + this);

            mWM.addView(mView, mParams);

            trySendAccessibilityEvent();

        }

    }

```

上面的代码很简单，就是设置View的参数，并通过WindowManager添加显示。



### 8.NotificationManagerService#cancelToastLocked



这个方法中，调用TN的hide方法隐藏当前Toast，并从列表中移除，将下一个Toast所在的进程挂到前台，并显示。

hide方法中，只是调用WindowManager的removeView移除。

大家自己去看源码去吧。

