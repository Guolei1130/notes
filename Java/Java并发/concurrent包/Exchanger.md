
### 1.类简介

也是一个同步工具，用于在一对线程之间交换数据。只有每个线程都进入exchange方法并给出对象时，才能接受其他线程返回时给出的对象。在遗传算法和管道设计中非常有用。

### 2. 简单例子
```
    public static void main(String[] args){
        final Exchanger exchanger = new Exchanger();
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.err.println("我" + "我吧钱给你了");
                try {
                    String result = (String) exchanger.exchange("50块钱");
                    System.err.println("我"+result);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    System.err.println("老板"+"等钱到账中");
                    String result = (String) exchanger.exchange("烟");
                    System.err.println("老板"+result);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
```














































