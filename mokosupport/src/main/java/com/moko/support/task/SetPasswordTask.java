package com.moko.support.task;

import com.moko.support.callback.MokoOrderTaskCallback;
import com.moko.support.entity.OrderType;

public class SetPasswordTask extends OrderTask {
    public byte[] data;

    public SetPasswordTask(MokoOrderTaskCallback callback) {
        super(OrderType.PASSWORD, callback, OrderTask.RESPONSE_TYPE_WRITE);
    }

    public void setData(String password) {
        this.data = password.getBytes();
    }

    @Override
    public byte[] assemble() {
        return data;
    }
}
