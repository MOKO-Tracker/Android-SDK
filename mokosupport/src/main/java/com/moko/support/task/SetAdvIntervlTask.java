package com.moko.support.task;

import com.moko.support.callback.MokoOrderTaskCallback;
import com.moko.support.entity.OrderType;
import com.moko.support.utils.MokoUtils;

public class SetAdvIntervlTask extends OrderTask {
    public byte[] data;

    public SetAdvIntervlTask(MokoOrderTaskCallback callback) {
        super(OrderType.ADV_INTERVAL, callback, OrderTask.RESPONSE_TYPE_WRITE);
    }

    public void setData(int advInterval) {
        this.data = MokoUtils.toByteArray(advInterval, 2);
    }

    @Override
    public byte[] assemble() {
        return data;
    }
}
