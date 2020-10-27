package com.moko.support.task;

import com.moko.support.entity.OrderType;

public class SetPasswordTask extends OrderTask {
    public byte[] data;

    public SetPasswordTask() {
        super(OrderType.PASSWORD, OrderTask.RESPONSE_TYPE_WRITE);
    }

    public void setData(String password) {
        this.data = password.getBytes();
    }

    @Override
    public byte[] assemble() {
        return data;
    }
}
