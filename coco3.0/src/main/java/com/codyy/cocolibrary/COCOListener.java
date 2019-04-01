package com.codyy.cocolibrary;


/**
 * coco监听接口
 * Created by lijian on 2017/6/6.
 */

public interface COCOListener {
    /**
     * coco连接成功,可以发送/接收数据
     * Called after <var>onHandshakeReceived</var> returns <var>true</var>.
     * Indicates that a complete WebSocket connection has been established,
     * and we are ready to send/receive data.
     */
    void onOpen();

    /**
     * 收到coco消息
     *
     * @param message 消息内容,The UTF-8 decoded message that was received.
     */
    void onMessage(String message);

    /**
     * coco关闭
     *
     * @param code   The codes can be looked up here: {@link CloseFrame}
     * @param reason Additional information string
     * @param remote Returns whether or not the closing of the connection was initiated by the remote host.
     */
    void onClose(int code, String reason, boolean remote);

    /**
     * coco异常
     *
     * @param e 异常信息, The exception that occurred. <br>
     *          Might be null if the exception is not related to any specific connection. For example if the server port could not be bound.
     */
    void onError(Exception e);
}
