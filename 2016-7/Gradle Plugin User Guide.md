#### 1. Basic Project Setup

##### 1.1 Simple build files
```

buildscript {
    repositories { 
        jcenter()
    }


    dependencies {
        classpath 'com.android.tools.build:gradle:1.3.1'
    }
}


apply plugin: 'com.android.application'


android {
    compileSdkVersion 23
    buildToolsVersion "23.1.0"
}
```
* builescript域
 * repositories 配置仓库
 * dependencies 配置插件for android
* apply 引用插件
* android android相关配置


##### 1.2 Project Structure 项目结构
```
android {
    sourceSets {
        main {
            manifest.srcFile 'AndroidManifest.xml'
            java.srcDirs = ['src']
            resources.srcDirs = ['src']
            aidl.srcDirs = ['src']
            renderscript.srcDirs = ['src']
            res.srcDirs = ['res']
            assets.srcDirs = ['assets']
        }

        androidTest.setRoot('tests')
    }
}
```
##### 1.3 Build Tasks
* assemble 
* check 
* build
* clean

##### 1.4 Basic Build Customization
* minSdkVersion
* targetSdkVersion
* versionCode
* versionName
* applicationId
* testApplicationId
* testInstrumentationRunner

```


def computeVersionName() {
    ...
}


android {
    compileSdkVersion 23
    buildToolsVersion "23.0.1"


    defaultConfig {
        versionCode 12 
        versionName computeVersionName()
        minSdkVersion 16
        targetSdkVersion 23
    }
}
```

##### 1.5 Build Types
```
android {
    buildTypes {
        debug {
            applicationIdSuffix ".debug"
        }


        jnidebug {
            initWith(buildTypes.debug)
            applicationIdSuffix ".jnidebug"
            jniDebuggable true
        }
    }
}
```

##### 1.6 Signing Configurations
```
android {
    signingConfigs {
        debug {
            storeFile file("debug.keystore")
        }


        myConfig {
            storeFile file("other.keystore")
            storePassword "android"
            keyAlias "androiddebugkey"
            keyPassword "android"
        }
    }


    buildTypes {
        foo {
            signingConfig signingConfigs.myConfig
        }
    }
}
```

##### 1.7 Dependencies, Android Libraries and Multi-project setup
* compile: main application
* androidTestCompile: test application
* debugCompile: debug Build Type
* releaseCompile: release Build Type.

##### 1.8 Library projects
```
apply plugin: 'com.android.library'
```
##### 1.9 Lint support

```
android {
    lintOptions {
        // turn off checking the given issue id's
        disable 'TypographyFractions','TypographyQuotes'

        // turn on the given issue id's
        enable 'RtlHardcoded','RtlCompat', 'RtlEnabled'

        // check *only* the given issue id's
        check 'NewApi', 'InlinedApi'
    }
}
```
#### 2. Advanced Build Customization

##### 2.1 Running ProGuard
```
android {
    buildTypes {
        release {
            minifyEnabled true
            proguardFile getDefaultProguardFile('proguard-android.txt')
        }
    }

    productFlavors {
        flavor1 {
        }
        flavor2 {
            proguardFile 'some-other-rules.txt'
        }
    }
}
```
##### 2.2 Shrinking Resources


#### Apk Splits

##### 1. Introduction
android {
  ...
  splits {
    density {
      enable true
      exclude "ldpi", "tvdpi", "xxxhdpi"
      compatibleScreens 'small', 'normal', 'large', 'xlarge'
    }
  }

enable: enables the density split mechanism
exclude: By default all densities are included, you can remove some densities.
include: indicate which densities to be included
reset(): reset the list of densities to be included to an empty string (this allows, in conjunctions with include, to indicate which one to use rather than which ones to ignore)
compatibleScreens: indicates a list of compatible screens. This will inject a matching <compatible-screens><screen ...> node in the manifest. This is optional.

##### 2. ABIs Splits
android {
  ...
  splits {
    abi {
      enable true
      reset()
      include 'x86', 'armeabi-v7a', 'mips'
      universalApk true
    }
  }
}

enable: enables the ABIs split mechanism
exclude: By default all ABIs are included, you can remove some ABIs.
include: indicate which ABIs to be included
reset(): reset the list of ABIs to be included to an empty string (this allows, in conjunctions with include, to indicate which one to use rather than which ones to ignore)
universalApk: indicates whether to package a universal version (with all ABIs) or not. Default is false.

Sample: ndkSanAngeles



_ _ _
### 小技巧
控制所有model的版本
在主工程build.gradle文件下
```
ext {
  compileSdkVersion = 19
  buildToolsVersion = "19.0.1"
}
```
在Android module中 这样引用
```
android {
  compileSdkVersion rootProject.ext.compileSdkVersion
  buildToolsVersion rootProject.ext.buildToolsVersion
}
```
_ _ _
### Resource Shrinking 资源压缩
```
android {
    ...

    buildTypes {
        release {
            minifyEnabled true
            shrinkResources true
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
        }
    }
}
```
在压缩资源之前，要先打开代码混淆开关。

可以使用Keeping Resources 保持资源部被压缩

### Res Configs
```
android {
    defaultConfig {
        ...
        resConfigs "en", "fr"
        }
}
```
_ _ _
### Support Annotations 
地址 http://tools.android.com/tech-docs/support-annotations
