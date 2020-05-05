package com.moko.contacttracker.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Message;

import com.moko.support.MokoConstants;
import com.moko.support.MokoSupport;
import com.moko.support.callback.MokoOrderTaskCallback;
import com.moko.support.entity.ConfigKeyEnum;
import com.moko.support.entity.OrderType;
import com.moko.support.handler.BaseMessageHandler;
import com.moko.support.log.LogModule;
import com.moko.support.task.GetAdvIntervalTask;
import com.moko.support.task.GetBatteryTask;
import com.moko.support.task.GetConnectionModeTask;
import com.moko.support.task.GetDeviceModelTask;
import com.moko.support.task.GetDeviceNameTask;
import com.moko.support.task.GetDeviceTypeTask;
import com.moko.support.task.GetFirmwareVersionTask;
import com.moko.support.task.GetHardwareVersionTask;
import com.moko.support.task.GetMajorTask;
import com.moko.support.task.GetManufacturerTask;
import com.moko.support.task.GetMeasurePowerTask;
import com.moko.support.task.GetMinorTask;
import com.moko.support.task.GetProductDateTask;
import com.moko.support.task.GetScanModeTask;
import com.moko.support.task.GetSoftwareVersionTask;
import com.moko.support.task.GetStoreAlertTask;
import com.moko.support.task.GetTransmissionTask;
import com.moko.support.task.GetUUIDTask;
import com.moko.support.task.OpenNotifyTask;
import com.moko.support.task.OrderTask;
import com.moko.support.task.OrderTaskResponse;
import com.moko.support.task.SetAdvIntervlTask;
import com.moko.support.task.SetConnectionModeTask;
import com.moko.support.task.SetDeviceNameTask;
import com.moko.support.task.SetMajorTask;
import com.moko.support.task.SetMeasurePowerTask;
import com.moko.support.task.SetMinorTask;
import com.moko.support.task.SetPasswordTask;
import com.moko.support.task.SetResetTask;
import com.moko.support.task.SetScanModeTask;
import com.moko.support.task.SetStoreAlertTask;
import com.moko.support.task.SetTransmissionTask;
import com.moko.support.task.SetUUIDTask;
import com.moko.support.task.WriteConfigTask;

/**
 * @Date 2020/4/21
 * @Author wenzheng.liu
 * @Description
 * @ClassPath com.moko.contacttracker.service.MokoService
 */
public class MokoService extends Service implements MokoOrderTaskCallback {

    @Override
    public void onOrderResult(OrderTaskResponse response) {
        Intent intent = new Intent(new Intent(MokoConstants.ACTION_ORDER_RESULT));
        intent.putExtra(MokoConstants.EXTRA_KEY_RESPONSE_ORDER_TASK, response);
        sendOrderedBroadcast(intent, null);
    }

    @Override
    public void onOrderTimeout(OrderTaskResponse response) {
        Intent intent = new Intent(new Intent(MokoConstants.ACTION_ORDER_TIMEOUT));
        intent.putExtra(MokoConstants.EXTRA_KEY_RESPONSE_ORDER_TASK, response);
        sendOrderedBroadcast(intent, null);
    }

    @Override
    public void onOrderFinish() {
        sendOrderedBroadcast(new Intent(MokoConstants.ACTION_ORDER_FINISH), null);
    }

    @Override
    public void onCreate() {
        LogModule.v("MokoService...onCreate");
        mHandler = new ServiceHandler(this);
        super.onCreate();
    }

    public void connectBluetoothDevice(String address) {
        MokoSupport.getInstance().connDevice(this, address);
    }

    public void disConnectBle() {
        MokoSupport.getInstance().disConnectBle();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogModule.v("MokoService...onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    private IBinder mBinder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {
        LogModule.v("MokoService...onBind");
        return mBinder;
    }

    @Override
    public void onLowMemory() {
        LogModule.v("MokoService...onLowMemory");
        disConnectBle();
        super.onLowMemory();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        LogModule.v("MokoService...onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        LogModule.v("MokoService...onDestroy");
        disConnectBle();
        super.onDestroy();
    }

    public class LocalBinder extends Binder {
        public MokoService getService() {
            return MokoService.this;
        }
    }

    public ServiceHandler mHandler;

    public class ServiceHandler extends BaseMessageHandler<MokoService> {

        public ServiceHandler(MokoService service) {
            super(service);
        }

        @Override
        protected void handleMessage(MokoService service, Message msg) {
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // READ
    ///////////////////////////////////////////////////////////////////////////

    public OrderTask getManufacturer() {
        GetManufacturerTask getManufacturerTask = new GetManufacturerTask(this);
        return getManufacturerTask;
    }

    public OrderTask getDeviceModel() {
        GetDeviceModelTask getDeviceModelTask = new GetDeviceModelTask(this);
        return getDeviceModelTask;
    }

    public OrderTask getProductDate() {
        GetProductDateTask getProductDateTask = new GetProductDateTask(this);
        return getProductDateTask;
    }

    public OrderTask getHardwareVersion() {
        GetHardwareVersionTask getHardwareVersionTask = new GetHardwareVersionTask(this);
        return getHardwareVersionTask;
    }

    public OrderTask getFirmwareVersion() {
        GetFirmwareVersionTask getFirmwareVersionTask = new GetFirmwareVersionTask(this);
        return getFirmwareVersionTask;
    }

    public OrderTask getSoftwareVersion() {
        GetSoftwareVersionTask getSoftwareVersionTask = new GetSoftwareVersionTask(this);
        return getSoftwareVersionTask;
    }

    public OrderTask getBattery() {
        GetBatteryTask getBatteryTask = new GetBatteryTask(this);
        return getBatteryTask;
    }

    public OrderTask getDeviceName() {
        GetDeviceNameTask getDeviceNameTask = new GetDeviceNameTask(this);
        return getDeviceNameTask;
    }

    public OrderTask getConnectionMode() {
        GetConnectionModeTask getConnectionModeTask = new GetConnectionModeTask(this);
        return getConnectionModeTask;
    }

    public OrderTask getDeviceType() {
        GetDeviceTypeTask getDeviceTypeTask = new GetDeviceTypeTask(this);
        return getDeviceTypeTask;
    }

    public OrderTask getMajor() {
        GetMajorTask getMajorTask = new GetMajorTask(this);
        return getMajorTask;
    }

    public OrderTask getMinor() {
        GetMinorTask getMinorTask = new GetMinorTask(this);
        return getMinorTask;
    }

    public OrderTask getMeasurePower() {
        GetMeasurePowerTask getMeasurePowerTask = new GetMeasurePowerTask(this);
        return getMeasurePowerTask;
    }

    public OrderTask getStoreAlert() {
        GetStoreAlertTask getStoreAlertTask = new GetStoreAlertTask(this);
        return getStoreAlertTask;
    }

    public OrderTask getTransmission() {
        GetTransmissionTask getTransmissionTask = new GetTransmissionTask(this);
        return getTransmissionTask;
    }

    public OrderTask getUUID() {
        GetUUIDTask getUUIDTask = new GetUUIDTask(this);
        return getUUIDTask;
    }

    public OrderTask getAdvInterval() {
        GetAdvIntervalTask getAdvIntervalTask = new GetAdvIntervalTask(this);
        return getAdvIntervalTask;
    }

    public OrderTask getScanMode() {
        GetScanModeTask getScanModeTask = new GetScanModeTask(this);
        return getScanModeTask;
    }

    public OrderTask getAdvTrigger() {
        WriteConfigTask task = new WriteConfigTask(this);
        task.setData(ConfigKeyEnum.GET_ADV_MOVE_CONDITION);
        return task;
    }

    public OrderTask getStoreTimeCondition() {
        WriteConfigTask task = new WriteConfigTask(this);
        task.setData(ConfigKeyEnum.GET_STORE_TIME_CONDITION);
        return task;
    }

    public OrderTask getScannerTrigger() {
        WriteConfigTask task = new WriteConfigTask(this);
        task.setData(ConfigKeyEnum.GET_SCAN_MOVE_CONDITION);
        return task;
    }

    public OrderTask getMacAddress() {
        WriteConfigTask task = new WriteConfigTask(this);
        task.setData(ConfigKeyEnum.GET_DEVICE_MAC);
        return task;
    }

    public OrderTask getTriggerSensitivity() {
        WriteConfigTask task = new WriteConfigTask(this);
        task.setData(ConfigKeyEnum.GET_MOVE_SENSITIVE);
        return task;
    }

    public OrderTask getButtonPower() {
        WriteConfigTask task = new WriteConfigTask(this);
        task.setData(ConfigKeyEnum.GET_TRIGGER_ENABLE);
        return task;
    }

    public OrderTask closePower() {
        WriteConfigTask task = new WriteConfigTask(this);
        task.setData(ConfigKeyEnum.CLOSE_DEVICE);
        return task;
    }

    ///////////////////////////////////////////////////////////////////////////
    // WRITE
    ///////////////////////////////////////////////////////////////////////////

    public OrderTask setAdvInterval(int advInterval) {
        SetAdvIntervlTask setAdvIntervlTask = new SetAdvIntervlTask(this);
        setAdvIntervlTask.setData(advInterval);
        return setAdvIntervlTask;
    }

    public OrderTask setConnectionMode(int connectionMode) {
        SetConnectionModeTask setConnectionModeTask = new SetConnectionModeTask(this);
        setConnectionModeTask.setData(connectionMode);
        return setConnectionModeTask;
    }

    public OrderTask setDeviceName(String deviceName) {
        SetDeviceNameTask setDeviceNameTask = new SetDeviceNameTask(this);
        setDeviceNameTask.setData(deviceName);
        return setDeviceNameTask;
    }

    public OrderTask setMajor(int major) {
        SetMajorTask setMajorTask = new SetMajorTask(this);
        setMajorTask.setData(major);
        return setMajorTask;
    }

    public OrderTask setMeasurePower(int measurePower) {
        SetMeasurePowerTask setMeasurePowerTask = new SetMeasurePowerTask(this);
        setMeasurePowerTask.setData(measurePower);
        return setMeasurePowerTask;
    }

    public OrderTask setMinor(int minor) {
        SetMinorTask setMinorTask = new SetMinorTask(this);
        setMinorTask.setData(minor);
        return setMinorTask;
    }

    public OrderTask setPassword(String password) {
        SetPasswordTask setPasswordTask = new SetPasswordTask(this);
        setPasswordTask.setData(password);
        return setPasswordTask;
    }

    public OrderTask setReset(String password) {
        SetResetTask setResetTask = new SetResetTask(this);
        setResetTask.setData(password);
        return setResetTask;
    }

    public OrderTask setScanMode(int scanMode) {
        SetScanModeTask setScanModeTask = new SetScanModeTask(this);
        setScanModeTask.setData(scanMode);
        return setScanModeTask;
    }

    public OrderTask setStoreAlert(int enable) {
        SetStoreAlertTask setStoreAlertTask = new SetStoreAlertTask(this);
        setStoreAlertTask.setData(enable);
        return setStoreAlertTask;
    }

    public OrderTask setTransmission(int transmission) {
        SetTransmissionTask setTransmissionTask = new SetTransmissionTask(this);
        setTransmissionTask.setData(transmission);
        return setTransmissionTask;
    }

    public OrderTask setUUID(String uuid) {
        SetUUIDTask setUUIDTask = new SetUUIDTask(this);
        setUUIDTask.setData(uuid);
        return setUUIDTask;
    }


    public WriteConfigTask setWriteConfig(ConfigKeyEnum configKeyEnum) {
        WriteConfigTask writeConfigTask = new WriteConfigTask(this);
        writeConfigTask.setData(configKeyEnum);
        return writeConfigTask;
    }

    public WriteConfigTask setTime() {
        WriteConfigTask writeConfigTask = new WriteConfigTask(this);
        writeConfigTask.setTime();
        return writeConfigTask;
    }

    public WriteConfigTask setAdvMoveCondition(int seconds) {
        WriteConfigTask writeConfigTask = new WriteConfigTask(this);
        writeConfigTask.setAdvMoveCondition(seconds);
        return writeConfigTask;
    }

    public WriteConfigTask setStorageInterval(int minutes) {
        WriteConfigTask writeConfigTask = new WriteConfigTask(this);
        writeConfigTask.setStoreTimeCondition(minutes);
        return writeConfigTask;
    }

    public WriteConfigTask setScannerMoveCondition(int seconds) {
        WriteConfigTask writeConfigTask = new WriteConfigTask(this);
        writeConfigTask.setScanMoveCondition(seconds);
        return writeConfigTask;
    }

    ///////////////////////////////////////////////////////////////////////////
    // NOTIFY
    ///////////////////////////////////////////////////////////////////////////
    public OrderTask openWriteConfigNotify() {
        OpenNotifyTask task = new OpenNotifyTask(OrderType.WRITE_CONFIG, this);
        return task;
    }

    public OrderTask openDisconnectedNotify() {
        OpenNotifyTask task = new OpenNotifyTask(OrderType.DISCONNECTED_NOTIFY, this);
        return task;
    }

    public OrderTask openPasswordNotify() {
        OpenNotifyTask task = new OpenNotifyTask(OrderType.PASSWORD, this);
        return task;
    }
}
