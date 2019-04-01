#### 发布指令:
```
//cd cms sdk root dir
gradle clean build uploadArchives
```
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
        maven { url 'http://maven.5idoo.com/nexus/content/groups/mobile/' }
    }
}
//app
dependencies {
    implementation 'com.codyy.mobile:coco3.0:1.0.8'
}
```

### <font color="red">注意事项</font>
如果主工程中包含此<font color="red">'org.java-websocket:Java-WebSocket:1.3.9'</font> websocket库引用，请在引用'com.codyy.mobile:coco3.0:1.0.8'<font color="red">将websocket库剔除</font>