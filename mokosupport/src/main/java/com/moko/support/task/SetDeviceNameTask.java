package com.moko.support.task;

import com.moko.support.callback.MokoOrderTaskCallback;
import com.moko.support.entity.OrderType;
import com.moko.support.utils.MokoUtils;

public class SetDeviceNameTask extends OrderTask {
    public byte[] data;

    public SetDeviceNameTask(MokoOrderTaskCallback callback) {
        super(OrderType.DEVICE_NAME, callback, OrderTask.RESPONSE_TYPE_WRITE);
    }

    public void setData(String deviceName) {
        this.data = deviceName.getBytes();
    }

    @Override
    public byte[] assemble() {
        return data;
    }
}
