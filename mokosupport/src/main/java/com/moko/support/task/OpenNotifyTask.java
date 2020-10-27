package com.moko.support.task;


import com.moko.support.MokoSupport;
import com.moko.support.entity.OrderType;
import com.moko.support.log.LogModule;

public class OpenNotifyTask extends OrderTask {
    public byte[] data;

    public OpenNotifyTask(OrderType orderType) {
        super(orderType, OrderTask.RESPONSE_TYPE_NOTIFY);
    }

    @Override
    public byte[] assemble() {
        return data;
    }

    @Override
    public boolean timeoutPreTask() {
        LogModule.i(orderType.getName() + "超时");
        MokoSupport.getInstance().pollTask();
        MokoSupport.getInstance().onOpenNotifyTimeout();
        return false;
    }
}
