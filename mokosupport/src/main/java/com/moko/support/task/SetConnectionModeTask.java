package com.moko.support.task;

import com.moko.support.callback.MokoOrderTaskCallback;
import com.moko.support.entity.OrderType;

public class SetConnectionModeTask extends OrderTask {
    public byte[] data;

    public SetConnectionModeTask(MokoOrderTaskCallback callback) {
        super(OrderType.CONNECTION_MODE, callback, OrderTask.RESPONSE_TYPE_WRITE);
    }

    public void setData(int connectionMode) {
        this.data = new byte[1];
        data[0] = (byte) connectionMode;
    }

    @Override
    public byte[] assemble() {
        return data;
    }
}
