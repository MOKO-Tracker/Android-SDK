package com.moko.support.task;

import com.moko.support.entity.OrderType;

public class SetTransmissionTask extends OrderTask {
    public byte[] data;

    public SetTransmissionTask() {
        super(OrderType.TRANSMISSION, OrderTask.RESPONSE_TYPE_WRITE);
    }

    public void setData(int transmisson) {
        this.data = new byte[1];
        data[0] = (byte) transmisson;
    }

    @Override
    public byte[] assemble() {
        return data;
    }
}
