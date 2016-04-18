#### 1.什么是严苛模式
严苛模式是一个开发工具，能够检测程序中的违例，从而修复。最常用的地方就是主线程中disk的读写和network。目前能有两大策略，线程策略（ThreadPolicy）和Vm策略（VmPolicy）。

#### 2.如何使用
在Application、Activity的onCreate的supper.onCreate之间配置。
```
 public void onCreate() {
     if (DEVELOPER_MODE) {
         StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                 .detectDiskReads()
                 .detectDiskWrites()
                 .detectNetwork()   // or .detectAll() for all detectable problems
                 .penaltyLog()
                 .build());
         StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                 .detectLeakedSqlLiteObjects()
                 .detectLeakedClosableObjects()
                 .penaltyLog()
                 .penaltyDeath()
                 .build());
     }
     super.onCreate();
 }
```
#### 3.如何配置
我们可以通过建造者模式去灵活配置。
##### 3.1 检测项
* ThreadPolicy
 * detectAll 				检测所有潜在的违例
 * detectCustomSlowCalls 	自定义耗时操作
 * detectDiskReads 			读磁盘
 * detectDiskWrites 		写磁盘
 * detectNetwork  			检查网络
 * detectResourceMismatches	检查资源类型是否匹配
* VmPolicy
 * detectAll				检测所有潜在的
 * detectActivityLeaks 		检测Activity的泄露
 * detectCleartextNetwork   检测明文的网络
 * detectFileUriExposure    检测file://或者是content://
 * detectLeakedClosableObjects 检查为管理的Closable对象
 * detectLeakedRegistrationObjects 检测需要注册类型是否解注
 * detectLeakedSqlLiteObjects 检测sqlite对象，如cursors

##### 3.2 检测到违规项之后的表现形式
* penaltyDeath crash，在所有表现形式最后运行，
* penaltyDeathOnNetwork crash,在所有值钱，必须调用detectNetwork去允许这个。
* penaltyDialog 弹出dialog
* penaltyDropBox 将日志吸入到dropbox中
* penaltyFlashScreen 屏幕闪烁
* penaltyLog log日志

#### 4.演示
因为4.0以上网络请求发生在主线程会crash，所以我们通过在主线程中写文件来模拟。
在Activity的onCreate方法中的super.onCreate之前加入下面代码。
```
StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()
                .penaltyLog()
                .penaltyDialog()
                .build());
```
接着加入以下代码，并加入权限
```
public void writeToExternalStorage() {
        File path = Environment.getExternalStorageDirectory();
        File destFile = new File(path, "strictmode.txt");
        try {
            OutputStream output = new FileOutputStream(destFile, true);
            output.write("测试strictmnode".getBytes());
            output.flush();
            output.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
```
运行程序，会在logcat中输出如下日志，以及在app中弹出dialog，截图如下。
![Log日志](https://github.com/Guolei1130/ATips/blob/master/image/strictmode/strictmode_1.png)
![运行截图](https://github.com/Guolei1130/ATips/blob/master/image/strictmode/strickmode_2.png)
