package com.codyy.cocolibrary;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.framing.CloseFrame;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;

import static com.codyy.cocolibrary.COCOCode.COCO_CLIENT_TIME_OUT;
import static com.codyy.cocolibrary.COCOCode.MESSAGE_WHAT_CLOSE;
import static com.codyy.cocolibrary.COCOCode.MESSAGE_WHAT_ERROR;
import static com.codyy.cocolibrary.COCOCode.MESSAGE_WHAT_MESSAGE;
import static com.codyy.cocolibrary.COCOCode.MESSAGE_WHAT_OPEN;

/**
 * COCO服务
 * 调用顺序:
 * <ul>
 * <li>bindService(new Intent(this, COCOService.class), mServiceConnection, Context.BIND_AUTO_CREATE);</li>
 * <li>通过{@link android.content.ServiceConnection#onServiceConnected(ComponentName, IBinder)}的{@link COCOBinder#getCOCOService()}获取COCOService,并设置{@link COCOService#setOnCOCOListener(COCOListener)}监听</li>
 * <li>监听设置成功后,调用{@link COCOService#createCOCO(String)}创建coco连接</li>
 * <li>coco连接成功后,回调{@link COCOListener#onOpen()},接下来就可以发送coco消息</li>
 * <li>通过{@link COCOService#send(String)}发送coco消息</li>
 * <li>unbindService(mServiceConnection)停止coco服务</li>
 * </ul>
 * 代码用例请查看README.md
 * Created by lijian on 2017/6/2.
 */

public class COCOService extends Service implements Handler.Callback {
    private Handler mHandler;
    private WebSocketClient mWebSocketClient;
    private COCOListener mOnCOCOListener;
    private int maxRetryCount = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new Handler(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        clearWebSocketClient();
        mHandler = null;
        mOnCOCOListener = null;
    }

    private void clearWebSocketClient() {
        if (mWebSocketClient != null) {
            mWebSocketClient.close();
        }
        mWebSocketClient = null;
    }

    public void setMaxRetryCount(int maxRetryCount) {
        this.maxRetryCount = maxRetryCount;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new COCOBinder();
    }

    /**
     * 自定义COCOBinder
     */
    public class COCOBinder extends Binder {
        /**
         * 获取COCOService
         *
         * @return COCOService
         */
        public COCOService getCOCOService() {
            return COCOService.this;
        }
    }

    /**
     * 创建coco连接
     *
     * @param url url格式ws://ip:port/ws(←效率更高) 或者wss://ip:port/wss(←安全性更好)
     */
    public void createCOCO(String url) {
        try {
            if (TextUtils.isEmpty(url)) throw new NullPointerException("url can not be null");
            clearWebSocketClient();
            if (mWebSocketClient == null) {
                mWebSocketClient = new CocoClient(new URI(url));
                if (url.startsWith("wss")) {
//                    mWebSocketClient.setSocket(new CustomTrust().getSSLSocket(getResources().openRawResource(R.raw.myx5092)));
                    mWebSocketClient.connectBlocking();
                } else {
                    mWebSocketClient.connect();
                }
            }
        } catch (URISyntaxException | InterruptedException | NullPointerException e) {
            e.printStackTrace();
            mWebSocketClient = null;
        }
    }

    /**
     * 发送coco消息
     *
     * @param message 消息类型为String
     */
    public void send(@NonNull String message) {
        if (TextUtils.isEmpty(message)) return;
        if (mWebSocketClient != null && mWebSocketClient.isOpen()) {
            mWebSocketClient.send(message);
        }
    }

    /**
     * 发送coco消息
     *
     * @param message 消息类型为byte[]
     */
    public void send(@NonNull byte[] message) {
        if (mWebSocketClient != null && mWebSocketClient.isOpen()) {
            mWebSocketClient.send(message);
        }
    }

    /**
     * 发送coco消息
     *
     * @param message 消息类型为ByteBuffer
     */
    public void send(@NonNull ByteBuffer message) {
        if (mWebSocketClient != null && mWebSocketClient.isOpen()) {
            mWebSocketClient.send(message);
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MESSAGE_WHAT_OPEN:
                if (mOnCOCOListener != null) {
                    mOnCOCOListener.onOpen();
                }
                break;
            case MESSAGE_WHAT_MESSAGE:
                if (mOnCOCOListener != null) {
                    mOnCOCOListener.onMessage((String) msg.obj);
                }
                break;
            case MESSAGE_WHAT_CLOSE:
                if (mOnCOCOListener != null) {
                    mOnCOCOListener.onClose(msg.getData().getInt("code", 0), msg.getData().getString("reason", ""), msg.getData().getBoolean("remote", false));
                }
                break;
            case MESSAGE_WHAT_ERROR:
                if (mOnCOCOListener != null) {
                    mOnCOCOListener.onError((Exception) msg.obj);
                }
                break;
        }
        return true;
    }

    /**
     * 设置coco监听
     */
    public void setOnCOCOListener(COCOListener onCOCOListener) {
        mOnCOCOListener = onCOCOListener;
    }

    /**
     * coco client
     */
    private class CocoClient extends WebSocketClient {

        CocoClient(URI uri) {
            super(uri, new Draft_6455(), null, COCO_CLIENT_TIME_OUT);
        }

        @Override
        public void onOpen(ServerHandshake serverHandshake) {
            mHandler.sendEmptyMessage(MESSAGE_WHAT_OPEN);
        }

        @Override
        public void onMessage(String s) {
            Message message = new Message();
            message.what = MESSAGE_WHAT_MESSAGE;
            message.obj = s;
            checkHandlerNull(message);
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
            if (code == CloseFrame.NEVER_CONNECTED && mWebSocketClient != null && maxRetryCount > 0) {
                maxRetryCount--;
                Log.d("retry", maxRetryCount + "");
                createCOCO(mWebSocketClient.getURI().toString());
            } else {
                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putInt("code", code);
                bundle.putString("reason", reason);
                bundle.putBoolean("remote", remote);
                message.setData(bundle);
                message.what = MESSAGE_WHAT_CLOSE;
                checkHandlerNull(message);
            }
        }

        @Override
        public void onError(Exception e) {
            Message message = new Message();
            message.what = MESSAGE_WHAT_ERROR;
            message.obj = e;
            checkHandlerNull(message);
        }

        private void checkHandlerNull(Message message) {
            if (mHandler != null) {
                mHandler.sendMessage(message);
            }
        }
    }

}
