package com.moko.support.task;

import android.support.annotation.IntRange;

import com.moko.support.callback.MokoOrderTaskCallback;
import com.moko.support.entity.OrderType;
import com.moko.support.utils.MokoUtils;

public class SetMinorTask extends OrderTask {
    public byte[] data;

    public SetMinorTask(MokoOrderTaskCallback callback) {
        super(OrderType.MINOR, callback, OrderTask.RESPONSE_TYPE_WRITE);
    }

    public void setData(@IntRange(from = 0, to = 65535) int minor) {
        this.data = MokoUtils.toByteArray(minor, 2);
    }

    @Override
    public byte[] assemble() {
        return data;
    }
}
