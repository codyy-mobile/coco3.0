package com.codyy.cocolibrary;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import androidx.annotation.IntDef;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

import java.nio.ByteBuffer;

/**
 * COCO服务调用方式优化
 * Created by lijian on 2017/6/6.
 */

public class COCO {
    private static volatile COCO INSTANCE;
    private static volatile boolean bound;

    public static COCO getDefault() {
        if (INSTANCE == null)
            synchronized (COCO.class) {
                if (INSTANCE == null) {
                    INSTANCE = new COCO();
                }
            }
        return INSTANCE;
    }

    private ServiceConnection mServiceConnection;
    private COCOService mCOCOService;

    /**
     * 绑定COCO服务
     *
     * @param context  context
     * @param url      url格式ws://ip:port/ws(←效率更高) 或者wss://ip:port/wss(←安全性更好)
     * @param listener 监听
     */
    public void bind(@NonNull final Context context, @NonNull final String url, @NonNull final COCOListener listener) {
        if (bound) return;
        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mCOCOService = ((COCOService.COCOBinder) service).getCOCOService();
                bound = true;
                mCOCOService.createCOCO(url);
                mCOCOService.setOnCOCOListener(listener);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                bound = false;
            }
        };
        Intent intent = new Intent(context, COCOService.class);
        context.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * 在COCO连接失败超过最大尝试次数时,可手动调用此方法重新创建连接,连接成功后,需重新调用具体业务发送COCO消息流程
     *
     * @param url url格式ws://ip:port/ws(←效率更高) 或者wss://ip:port/wss(←安全性更好)
     */
    public void reConnectCOCO(@NonNull String url) {
        if (bound && mCOCOService != null) mCOCOService.createCOCO(url);
    }

    /**
     * 解除绑定COCO服务
     */
    public void unbind(@NonNull Context context) {
        if (mServiceConnection != null && bound) {
            try {
                context.unbindService(mServiceConnection);
            } catch (Exception e) {

            } finally {
                bound = false;
            }
        }
        mServiceConnection = null;
        mCOCOService = null;
    }

    /**
     * 发送coco消息
     *
     * @param message 消息类型为String
     */
    public void post(@NonNull String message) {
        if (mCOCOService != null) mCOCOService.send(message);
    }

    /**
     * 发送coco消息
     *
     * @param message 消息类型为byte[]
     */
    public void post(@NonNull byte[] message) {
        if (mCOCOService != null) mCOCOService.send(message);
    }

    /**
     * 发送coco消息
     *
     * @param message 消息类型为ByteBuffer
     */
    public void post(@NonNull ByteBuffer message) {
        if (mCOCOService != null) mCOCOService.send(message);
    }

    /**
     * 设置重连次数
     *
     * @param retryCount 重试次数
     */
    public void setMaxRetryCount(@IntRange(from = 0, to = Integer.MAX_VALUE) int retryCount) {
        if (mCOCOService != null) mCOCOService.setMaxRetryCount(retryCount);
    }
}
