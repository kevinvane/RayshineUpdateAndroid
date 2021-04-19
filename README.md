# Rayshine 测试 App 在线更新

这是一个测试项目，比较粗糙。

###一行代码检查更新
```
RayshineUpdate.check(this,"test")
```

### 下载通知栏Icon覆盖

在您项目中的drawable目录创建相同名称的png图片即可覆盖，Icon图片最好是有镂空效果，即只有 “单色+透明” 两部部分。
```
drawable/notification_icon.png
```

###导入

```gradle
implementation 'com.github.kevinvane:RayshineUpdateAndroid:${Version}'
```

**buildscript**

```gradle
repositories {
    maven { url "https://jitpack.io" }
}
```

###版本

1. androidx新版本
```
versionCode 22
versionName "2.2"
```

2. 旧的兼容版本
```
versionCode 5
versionName "1.5"
```

###androidx新版依赖
```gradle
implementation 'androidx.appcompat:appcompat:1.2.0'
implementation 'com.liulishuo.okdownload:okhttp:1.0.7'
implementation 'com.yanzhenjie:permission:2.0.3'
implementation 'com.liulishuo.okdownload:okdownload:1.0.7'
```

###旧版依赖
```gradle
implementation 'com.android.support:appcompat-v7:28.+'
implementation 'com.squareup.okhttp3:okhttp:3.12.0'
implementation 'com.yanzhenjie:permission:2.0.0-rc5'
implementation 'com.liulishuo.okdownload:okdownload:1.0.7'
```