package com.moko.support.callback;

import com.moko.support.task.OrderTaskResponse;

/**
 * @Date 2020/4/20
 * @Author wenzheng.liu
 * @Description 
 * @ClassPath com.moko.support.callback.MokoOrderTaskCallback
 */
public interface MokoOrderTaskCallback {

    void onOrderResult(OrderTaskResponse response);

    void onOrderTimeout(OrderTaskResponse response);

    void onOrderFinish();
}
