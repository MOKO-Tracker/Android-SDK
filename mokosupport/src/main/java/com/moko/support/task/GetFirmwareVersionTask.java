package com.moko.support.task;

import com.moko.support.callback.MokoOrderTaskCallback;
import com.moko.support.entity.OrderType;

public class GetFirmwareVersionTask extends OrderTask {

    public byte[] data;

    public GetFirmwareVersionTask(MokoOrderTaskCallback callback) {
        super(OrderType.FIRMWARE_VERSION, callback, OrderTask.RESPONSE_TYPE_READ);
    }

    @Override
    public byte[] assemble() {
        return data;
    }
}
