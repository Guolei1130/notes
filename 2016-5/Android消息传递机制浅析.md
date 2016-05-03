### 1.Looper、Handler、MessageQueue的关系
* Looper 用于线程的消息循环,一个线程只能有一个Looper对象
* Handler
 * 执行任务调度和发生一些操作（在未来某时刻）
 * 执行其他线程中的队列消息
* MessageQueue 消息队列

>一个Looper有一个MessageQueue，并不断从MessageQueue中取出消息，提交给Handel处理。

### 2.Looper
以主线程为例。Looper的初始化过程是这样的。
代码地址：/frameworks/base/core/java/android/app/ActivityThread.java#main(String[] args)
```
Looper.prepareMainLooper();
xxxx
Looper.loop();
```
而非UI线程的初始化过程
```
Looper.prepare()
xxx
Looper.loop();
```
不管是主线程还是其他线程，都会调用prepare(boolean quitAllowed)方法，参数为是否允许退出循环，主线程是不允许的，而其他线程是允许的。
```
	private static void prepare(boolean quitAllowed) {
        if (sThreadLocal.get() != null) {
            throw new RuntimeException("Only one Looper may be created per thread");
        }
        sThreadLocal.set(new Looper(quitAllowed));
    }
```
接下来看Looper的构造方法
```
	private Looper(boolean quitAllowed) {
        mQueue = new MessageQueue(quitAllowed);
        mThread = Thread.currentThread();
    }
```
从构造方法中可以看出，将Looper、MessageQueue、currentThread之间建立的关联。
那么，Looper对象是如何从消息队列中不断地取出消息呢？代码比较长，我们拆开来看。
```
final Looper me = myLooper();
```
```
	public static @Nullable Looper myLooper() {
        return sThreadLocal.get();
    }
```
取出和当前线程对应的Looper对象(这里需要讲解下ThreadLocal)
```
	for (;;) {
            Message msg = queue.next(); // might block
            if (msg == null) {
                // No message indicates that the message queue is quitting.
                return;
            }
            msg.target.dispatchMessage(msg);

            if (logging != null) {
                logging.println("<<<<< Finished to " + msg.target + " " + msg.callback);
            }

            // Make sure that during the course of dispatching the
            // identity of the thread wasn't corrupted.
            final long newIdent = Binder.clearCallingIdentity();
            msg.recycleUnchecked();
        }
    }
```
上面的代码中去掉了一些日志代码。可以看到,死循环从MessageQueue中取出消息，并调用msg.target.dispatchMessage(msg)方法去分发小心，最后回收。

### 3.Handler
既然知道了Looper，那么我们需要知道消息是怎么来的。
```
Message msg = new Message();
msg.what = xxx;
msg.obj = xxx;
handler.sendMessage(msg);
```
我们通常都是通过上面的步骤去发送的，所以，追踪下代码。

sendMessage->sendMessageDelayed->sendMessageAtTime->enqueueMessage
到这里就来到了一个关键点了，我们看下代码。
```
    private boolean enqueueMessage(MessageQueue queue, Message msg, long uptimeMillis) {
        msg.target = this;
        if (mAsynchronous) {
            msg.setAsynchronous(true);
        }
        return queue.enqueueMessage(msg, uptimeMillis);
    }
```
还记得我们上面分发消息的地方么？同样有target，这个是什么呢？我们进Message的源码里一探究竟。
```
Handler target
```
好，是一个handler对象，也就是我们当前线程的一个handler对象，也就是发送消息的那个handler对象。所以，我们现在来看看handler的dispatchMessage方法。
```
    public void dispatchMessage(Message msg) {
        if (msg.callback != null) {
            handleCallback(msg);
        } else {
            if (mCallback != null) {
                if (mCallback.handleMessage(msg)) {
                    return;
                }
            }
            handleMessage(msg);
        }
    }
```
调用关系和初始化的关系有关。
 * Message.obtain(Handler h, Runnable callback) 构造消息时，会调用handlerCallback(msg)方法
 * 初始化Handler的构造函数带CallBack参数是，调用mCallBack的 handleMessage(msg)方法
 * 否则，调用handlerMessage(msg)方法。

回到enqueueMessage方法，发现是通过调用MessageQueue的enqueueMessage方法来插入消息的。
### 4.MessageQueue
消息队列，字面意思是消息队列，然而我们知道，队列的方式是先进先出，而我们的消息时候时间调度的，因此，并不符合先进先出的思想，所以，消息队列实际上是个链表，这样我们才能往任意位置插入消息。enqueueMessage的代码如下：
```
    boolean enqueueMessage(Message msg, long when) {
        if (msg.target == null) {
            throw new IllegalArgumentException("Message must have a target.");
        }
        if (msg.isInUse()) {
            throw new IllegalStateException(msg + " This message is already in use.");
        }

        synchronized (this) {
            if (mQuitting) {
                IllegalStateException e = new IllegalStateException(
                        msg.target + " sending message to a Handler on a dead thread");
                Log.w(TAG, e.getMessage(), e);
                msg.recycle();
                return false;
            }

            msg.markInUse();
            msg.when = when;
            Message p = mMessages;
            boolean needWake;
            if (p == null || when == 0 || when < p.when) {
                // New head, wake up the event queue if blocked.
                msg.next = p;
                mMessages = msg;
                needWake = mBlocked;
            } else {
                // Inserted within the middle of the queue.  Usually we don't have to wake
                // up the event queue unless there is a barrier at the head of the queue
                // and the message is the earliest asynchronous message in the queue.
                needWake = mBlocked && p.target == null && msg.isAsynchronous();
                Message prev;
                for (;;) {
                    prev = p;
                    p = p.next;
                    if (p == null || when < p.when) {
                        break;
                    }
                    if (needWake && p.isAsynchronous()) {
                        needWake = false;
                    }
                }
                msg.next = p; // invariant: p == prev.next
                prev.next = msg;
            }

            // We can assume mPtr != 0 because mQuitting is false.
            if (needWake) {
                nativeWake(mPtr);
            }
        }
        return true;
    }
```
上面的步骤是这样子的。
* 判断是否关联了handler
* 判断是否用过(已经插入到链表中的)
* 当前队列是否处在退出状态
 * 退出状态  回收资源，插入链表失败
 * 不是退出状态
 	 * 改变Message的状态为已经use，并获取message的when时间
 	 * 在链表中找到合适的位置插入
 	 	* 和链表头结点比较时间，如发生时间在头结点消息之前，插入到头结点
 	 	* 死循环，找到该消息比链表中的消息早发生的消息，插入到那条消息前面，否则就插入到链表表尾

在最前面Looper里，一直通过queue.next()去读取链表里的消息，所以，我们来看下next方法。由于next的方法比较长，我们一段一段来看。
```
                final long now = SystemClock.uptimeMillis();
                Message prevMsg = null;
                Message msg = mMessages;
                if (msg != null && msg.target == null) {
                    // Stalled by a barrier.  Find the next asynchronous message in the queue.
                    do {
                        prevMsg = msg;
                        msg = msg.next;
                    } while (msg != null && !msg.isAsynchronous());
                }
                if (msg != null) {
                    if (now < msg.when) {
                        // Next message is not ready.  Set a timeout to wake up when it is ready.
                        nextPollTimeoutMillis = (int) Math.min(msg.when - now, Integer.MAX_VALUE);
                    } else {
                        // Got a message.
                        mBlocked = false;
                        if (prevMsg != null) {
                            prevMsg.next = msg.next;
                        } else {
                            mMessages = msg.next;
                        }
                        msg.next = null;
                        if (DEBUG) Log.v(TAG, "Returning message: " + msg);
                        msg.markInUse();
                        return msg;
                    }
                } else {
                    // No more messages.
                    nextPollTimeoutMillis = -1;
                }

```
 * 假如当前Message不为null 但是没和handler关联的话，就找下一个消息，知道找到不是null也关联到handler的Message
 * 如果当前时间小于Message的when的话，就计算时间差，并复制给nextPollTimeoutMillis
 * 不小于的话
 	* 假如步骤1中链表头的消息没关联handler，就将步骤1中找出的不是null也关联了handler的Message的上一条Message.next指向该Message.next(这里有点绕，其实就相当于在链表中移除了改消息)，并返回该消息
 	* 否则，将mMessages(表头)指向msg.next(也是移除了该消息) 

 * 下面的代码忽略(ps:我看不懂...)

### 5.总结
消息传递机制是Android中最常见的，使用比较多的。







