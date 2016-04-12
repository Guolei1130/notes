#### Android 中处理ecmoji表情

>现在Android中是支持Emoji表情的，Emoji表情为Unicode编码，所以将输入的内容全部转化为Unicode变化，用的时候在转换回来就好。一下为代码

#####Android代码

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

##### IOS代码

* unicode转string

	```
+(NSString *)replaceUnicode:(NSString *)aUnicodeString
{
	NSString *tempStr1 = [aUnicodeString stringByReplacingOccurrencesOfString:@"\\u"withString:@"\\U"];
	
	NSString *tempStr2 = [tempStr1 stringByReplacingOccurrencesOfString:@"\""withString:@"\\\""];
	
	NSString *tempStr3 = [[@"\""stringByAppendingString:tempStr2] stringByAppendingString:@"\""];
	
	NSData *tempData = [tempStr3 dataUsingEncoding:NSUTF8StringEncoding];
	NSString* returnStr = [NSPropertyListSerialization propertyListFromData:tempData
														   mutabilityOption:NSPropertyListImmutable
																	 format:NULL
														   errorDescription:NULL];
	return [returnStr stringByReplacingOccurrencesOfString:@"\\r\\n"withString:@"\n";
}

	```

* string 转unicode

	```
+(NSString *)utf8ToUnicode:(NSString *)string
{
	NSUInteger length = [string length];
	NSMutableString *s = [NSMutableString stringWithCapacity:0];
	for (int i = 0;i < length; i++)
	{
		unichar _char = [string characterAtIndex:i];
		//判断是否为英文和数字
		if (_char <= '9' && _char >='0')
		{
			[s appendFormat:@"%@",[string substringWithRange:NSMakeRange(i,1)]];
		}
		else if(_char >='a' && _char <= 'z')
		{
			[s appendFormat:@"%@",[string substringWithRange:NSMakeRange(i,1)]];
		}
		else if(_char >='A' && _char <= 'Z')
		{
			[s appendFormat:@"%@",[string substringWithRange:NSMakeRange(i,1)]];
		}
		else
		{
			[s appendFormat:@"\\u%x",[string characterAtIndex:i]];
		}
	}
	return s;
}

	```

##### 优缺点

 * 优点
  * Android端可以减少体积，并且在IOS端输入任何表情的情况下，Android端最多只会显示错误，而不会显示乱码
  * IOS端无影响

 * 缺点
  * IOS端输入极其复杂的表情，Android端显示的表情不匹配 
