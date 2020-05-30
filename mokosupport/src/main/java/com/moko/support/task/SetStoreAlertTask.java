package com.moko.support.task;

import android.support.annotation.IntRange;

import com.moko.support.callback.MokoOrderTaskCallback;
import com.moko.support.entity.OrderType;

public class SetStoreAlertTask extends OrderTask {
    public byte[] data;

    public SetStoreAlertTask(MokoOrderTaskCallback callback) {
        super(OrderType.STORE_ALERT, callback, OrderTask.RESPONSE_TYPE_WRITE);
    }

    public void setData(@IntRange(from = 0, to = 3) int enable) {
        this.data = new byte[1];
        data[0] = (byte) enable;
    }

    @Override
    public byte[] assemble() {
        return data;
    }
}
