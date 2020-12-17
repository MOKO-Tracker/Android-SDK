package com.moko.support.task;

import com.moko.support.entity.OrderType;

import androidx.annotation.IntRange;

public class SetStoreAlertTask extends OrderTask {
    public byte[] data;

    public SetStoreAlertTask() {
        super(OrderType.STORE_ALERT, OrderTask.RESPONSE_TYPE_WRITE);
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
