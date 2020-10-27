package com.moko.support.task;

import android.support.annotation.IntRange;

import com.moko.support.entity.OrderType;

public class SetConnectionModeTask extends OrderTask {
    public byte[] data;

    public SetConnectionModeTask() {
        super(OrderType.CONNECTION_MODE, OrderTask.RESPONSE_TYPE_WRITE);
    }

    public void setData(@IntRange(from = 0, to = 1) int connectionMode) {
        this.data = new byte[1];
        data[0] = (byte) connectionMode;
    }

    @Override
    public byte[] assemble() {
        return data;
    }
}
