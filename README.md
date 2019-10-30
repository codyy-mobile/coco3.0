[![](https://jitpack.io/v/codyy-mobile/coco3.0.svg)](https://jitpack.io/#codyy-mobile/coco3.0)

### COCO bind
```
    COCO.getDefault().bind(context,url,COCOListener);//url="ws://ip:port/ws" or "wss://ip:port/wss"
```
### COCO post message
```
    COCO.getDefault().post(String message|byte[] message|ByteBuffer message);
```
### COCO reconnect
```
    COCO.getDefault().reConnectCOCO(String url);
```
### COCO unbind
```
    COCO.getDefault().unbind(context);
```
### COCO setMaxRetryCount(new)
```
    COCO.getDefault().setMaxRetryCount(int retryCount);
```

### COCO3.0 私有仓库使用方法
```
// gradle 全局配置增加maven 私有地址
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
//app
dependencies {
    implementation 'com.github.codyy-mobile:coco3.0:1.2.0'
}
```

### <font color="red">注意事项</font>
如果主工程中包含此<font color="red">'org.java-websocket:Java-WebSocket:1.4.0'</font> websocket库引用，请在引用'com.github.codyy-mobile:coco3.0:1.2.0'<font color="red">将websocket库剔除</font>
