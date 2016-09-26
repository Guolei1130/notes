### 1.类简介
一个同步助手，允许一个或多个线程去等待直到一组操作完成。
>共享锁，当锁计数器教导到0时，将释放等待的线程。当countdownlatch的锁计数器为1时，可以当做一中开关来使用。

>通过指定一个数来初始化
>await方法阻塞同不过过countdown方法减到0，所有的等待线程就会被释放，并且后续的await调用会立即返回。
>这个只能初始化一次，如果想要重置，请用CyclicBarrier。

一个简单的例子如下：

```
    public static void main(String[] args) throws InterruptedException {
        final CountDownLatch downLatch = new CountDownLatch(5);
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 5; i++) {
                    System.err.println(i);
                    downLatch.countDown();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        downLatch.await();

        System.err.println("结束了");
    }
```