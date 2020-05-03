package com.moko.support.task;

import com.moko.support.callback.MokoOrderTaskCallback;
import com.moko.support.entity.OrderType;

public class SetStoreAlertTask extends OrderTask {
    public byte[] data;

    public SetStoreAlertTask(MokoOrderTaskCallback callback) {
        super(OrderType.SCAN_MODE, callback, OrderTask.RESPONSE_TYPE_WRITE);
    }

    public void setData(int enable) {
        this.data = new byte[1];
        data[0] = (byte) enable;
    }

    @Override
    public byte[] assemble() {
        return data;
    }
}
