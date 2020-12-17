package com.moko.support.task;

import com.moko.support.entity.OrderType;
import com.moko.support.utils.MokoUtils;

import androidx.annotation.IntRange;

public class SetAdvIntervlTask extends OrderTask {
    public byte[] data;

    public SetAdvIntervlTask() {
        super(OrderType.ADV_INTERVAL, OrderTask.RESPONSE_TYPE_WRITE);
    }

    public void setData(@IntRange(from = 1, to = 100) int advInterval) {
        this.data = MokoUtils.toByteArray(advInterval, 1);
    }

    @Override
    public byte[] assemble() {
        return data;
    }
}
