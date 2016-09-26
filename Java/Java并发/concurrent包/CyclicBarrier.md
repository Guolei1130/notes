### 1.类简介
>循环障碍点
>一个同步工具，允许一系列线程全部等待彼此都到达一个障碍点。被叫做cyclicde的原因就是等待的线程被释放之后，允许重新使用。

* CyclicBarrier(int parties):指定参与者个数
* CyclicBarrier(int parties,Runnable barrierAction):指定一个屏障操作，此操作将会有最后一个进入barrier的线程执行。

* await 在所有到达之前处于屏障状态


简单的小例子

```
    public static void main(String[] args){
        final CyclicBarrier cyclicBarrier = new CyclicBarrier(10, new Runnable() {
            @Override
            public void run() {
                System.err.println("到齐了。可以开车了");
            }
        });
        for (int i = 0; i < 10; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    System.err.println("老子刷卡了");
                    try {
                        cyclicBarrier.await();
                        System.err.println("老子上车了");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (BrokenBarrierException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

    }
```