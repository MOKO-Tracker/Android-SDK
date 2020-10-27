package com.moko.support.task;

import android.support.annotation.IntRange;

import com.moko.support.entity.OrderType;
import com.moko.support.utils.MokoUtils;

public class SetMajorTask extends OrderTask {
    public byte[] data;

    public SetMajorTask() {
        super(OrderType.MAJOR, OrderTask.RESPONSE_TYPE_WRITE);
    }

    public void setData(@IntRange(from = 0, to = 65535) int major) {
        this.data = MokoUtils.toByteArray(major, 2);
    }

    @Override
    public byte[] assemble() {
        return data;
    }
}
