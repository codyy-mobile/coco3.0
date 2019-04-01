package com.codyy.cocolibrary;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * COCO常量
 * Created by lijian on 2017/6/12.
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@interface COCOCode {
    /**
     * int: the timeout value to be used in milliseconds.
     */
    int COCO_CLIENT_TIME_OUT = 5000;
    /**
     * coco连接成功标识
     */
    int MESSAGE_WHAT_OPEN = 0;
    /**
     * coco收到消息标识
     */
    int MESSAGE_WHAT_MESSAGE = 1;
    /**
     * coco关闭标识
     */
    int MESSAGE_WHAT_CLOSE = 2;
    /**
     * coco异常错误标识
     */
    int MESSAGE_WHAT_ERROR = 3;
}
