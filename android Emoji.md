#### Android 中处理ecmoji表情

>现在Android中是支持Emoji表情的，Emoji表情为Unicode编码，所以将输入的内容全部转化为Unicode变化，用的时候在转换回来就好。一下为代码

* string 转化为Unicode编码
```
private String string2Unicode(String string){
        StringBuffer unicode = new StringBuffer();
        for (int i = 0; i < string.length(); i++) {
            // 取出每一个字符
            char c = string.charAt(i);
            // 转换为unicode
            unicode.append("\\u" + Integer.toHexString(c));
        }

        return unicode.toString();
    }
```

* Unicode编码转String
```
public static String unicode2String(String unicode) {

        StringBuffer string = new StringBuffer();

        String[] hex = unicode.split("\\\\u");

        for (int i = 1; i < hex.length; i++) {

            // 转换出每一个代码点
            int data = Integer.parseInt(hex[i], 16);

            // 追加成string
            string.append((char) data);
        }

        return string.toString();
    }
```

当然，github上还有一些emoji表情的库，他们就是将Unicode编码和本地的图片对应起来，实际上是不需要的。