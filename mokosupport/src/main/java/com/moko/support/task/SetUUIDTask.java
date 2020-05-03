package com.moko.support.task;

import com.moko.support.callback.MokoOrderTaskCallback;
import com.moko.support.entity.OrderType;
import com.moko.support.utils.MokoUtils;

public class SetUUIDTask extends OrderTask {
    public byte[] data;

    public SetUUIDTask(MokoOrderTaskCallback callback) {
        super(OrderType.UUID, callback, OrderTask.RESPONSE_TYPE_WRITE);
    }

    public void setData(String uuid) {
        this.data = MokoUtils.hex2bytes(uuid);
    }

    @Override
    public byte[] assemble() {
        return data;
    }
}
