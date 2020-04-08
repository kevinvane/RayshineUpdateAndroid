# Rayshine 测试 App 在线更新

这是一个测试项目，比较粗糙。



#导入

```gradle
implementation 'com.github.kevinvane:RayshineUpdateAndroid:${Version}'
```

**buildscript**

```gradle
repositories {
    maven { url "https://jitpack.io" }
}
```

#依赖
```gradle
implementation 'com.android.support:appcompat-v7:28.+'
implementation 'com.squareup.okhttp3:okhttp:3.12.0'
implementation 'com.yanzhenjie:permission:2.0.0-rc5'
implementation 'com.liulishuo.okdownload:okdownload:1.0.7'
```