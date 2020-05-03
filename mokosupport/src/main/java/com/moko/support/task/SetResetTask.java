package com.moko.support.task;

import com.moko.support.callback.MokoOrderTaskCallback;
import com.moko.support.entity.OrderType;

public class SetResetTask extends OrderTask {
    public byte[] data;

    public SetResetTask(MokoOrderTaskCallback callback) {
        super(OrderType.RESET, callback, OrderTask.RESPONSE_TYPE_WRITE);
    }

    public void setData(String password) {
        this.data = password.getBytes();
    }

    @Override
    public byte[] assemble() {
        return data;
    }
}
