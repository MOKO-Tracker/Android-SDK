package com.moko.support.task;

import android.support.annotation.IntRange;

import com.moko.support.callback.MokoOrderTaskCallback;
import com.moko.support.entity.OrderType;
import com.moko.support.utils.MokoUtils;

public class SetAdvIntervlTask extends OrderTask {
    public byte[] data;

    public SetAdvIntervlTask(MokoOrderTaskCallback callback) {
        super(OrderType.ADV_INTERVAL, callback, OrderTask.RESPONSE_TYPE_WRITE);
    }

    public void setData(@IntRange(from = 1, to = 100) int advInterval) {
        this.data = MokoUtils.toByteArray(advInterval, 1);
    }

    @Override
    public byte[] assemble() {
        return data;
    }
}
