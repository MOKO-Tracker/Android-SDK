# Tracker Android SDK Instruction DOC（English）

----

## 1. Import project

**1.1 Import "Module mokosupport" to root directory**

**1.2 Edit "settings.gradle" file**

```
include ':app', ':mokosupport'
```

**1.3 Edit "build.gradle" file under the APP project**


	dependencies {
		...
		implementation project(path: ':mokosupport')
	}


----

## 2. How to use

**Initialize sdk at project initialization**

```
MokoSupport.getInstance().init(getApplicationContext());
```

**SDK provides three main functions:**

* Scan the device;
* Connect to the device;
* Send and receive data.

### 2.1 Scan the device

 **Start scanning**

```
MokoSupport.getInstance().startScanDevice(callback);
```

 **End scanning**

```
MokoSupport.getInstance().stopScanDevice();
```
 **Implement the scanning callback interface**

```java
/**
 * @ClassPath com.moko.support.callback.MokoScanDeviceCallback
 */
public interface MokoScanDeviceCallback {
    void onStartScan();

    void onScanDevice(DeviceInfo device);

    void onStopScan();
}
```
* **Analysis `DeviceInfo` ; inferred `BeaconInfo`**

```
BeaconInfo beaconInfo = new BeaconInfoParseableImpl().parseDeviceInfo(device);
```

Device types can be distinguished by `parseDeviceInfo(DeviceInfo deviceInfo)`.Refer `deviceInfo.scanResult.getScanRecord().getServiceData()` we can get parcelUuid,etc.

```
        Iterator iterator = map.keySet().iterator();
        if (iterator.hasNext()) {
            ParcelUuid parcelUuid = (ParcelUuid) iterator.next();
            if (parcelUuid.toString().startsWith("0000ff02")) {
                byte[] bytes = map.get(parcelUuid);
                if (bytes != null) {
                    major = String.valueOf(MokoUtils.toInt(Arrays.copyOfRange(bytes, 0, 2)));
                    minor = String.valueOf(MokoUtils.toInt(Arrays.copyOfRange(bytes, 2, 4)));
                    rssi_1m = bytes[4];
                    txPower = bytes[5];
                    connectable = (bytes[6] >> 4) & 0x0f;
                    track = bytes[6] & 0x0F;
                    battery = MokoUtils.toInt(Arrays.copyOfRange(bytes, 6, 8));
                }
            } else {
                return null;
            }
        }

```

### 2.2 Connect to the device


```
MokoSupport.getInstance().connDevice(context, address, mokoConnStateCallback);
```

When connecting to the device, context, MAC address and callback by EventBus.


```java
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConnectStatusEvent(ConnectStatusEvent event) {
        String action = event.getAction();
        if (MokoConstants.ACTION_CONN_STATUS_DISCONNECTED.equals(action)) {
            ...
        }
        if (MokoConstants.ACTION_DISCOVER_SUCCESS.equals(action)) {
            ...
        }
    }
```

It uses `EventBus` to notify activity after receiving the status, and send and receive data after connecting to the device with `broadcast`

### 2.3 Send and receive data.

All the request data is encapsulated into **TASK**, and sent to the device in a **QUEUE** way.
SDK gets task status from task callback (`MokoOrderTaskCallback`) after sending tasks successfully.

* **Task**

At present, all the tasks sent from the SDK can be divided into 4 types:

> 1.  READ：Readable
> 2.  WRITE：Writable
> 3.  NOTIFY：Can be listened( Need to enable the notification property of the relevant characteristic values)
> 4.  WRITE_NO_RESPONSE：After enabling the notification property, send data to the device and listen to the data returned by device.
> 5.  RESPONSE_TYPE_DISABLE_NOTIFY close the notification property.

Encapsulated tasks are as follows:

|Task Class|Task Type|Function
|----|----|----
|`OpenNotifyTask`|NOTIFY|Enable notification property


Custom device information
--

|Task Class|Task Type|Function
|----|----|----
|`GetManufacturerTask`|READ|Get manufacturer.
|`GetDeviceModelTask`|READ|Get product model.
|`GetProductDateTask`|READ|Get production date.
|`GetHardwareVersionTask`|READ|Get hardware version.
|`GetFirmwareVersionTask`|READ|Get firmware version.
|`GetSoftwareVersionTask`|READ|Get software version.
|`GetBatteryTask`|READ|Get battery capacity.
|`GetDeviceNameTask`|READ|Get adv name.
|`GetConnectionModeTask`|READ|Get connectable.
|`GetDeviceTypeTask`|READ|Get deviceType,5:no-3-Axis.

Adv iBeacon information
--

|Task Class|Task Type|Function
|----|----|----
|`GetMajorTask`|READ|Get major.
|`GetMinorTask`|READ|Get minor.
|`GetMeasurePowerTask`|READ|Get iBeacon measurePower.
|`GetTransmissionTask`|READ|Get iBeacon transmission.
|`GetUUIDTask`|READ|Get iBeacon UUID.
|`GetAdvIntervalTask`|READ|Get adv interval.
|`WriteConfigTask`|WRITE|Write `GET_ADV_MOVE_CONDITION`，get adv trigger.
|`SetMajorTask`|WRITE|Set major.
|`SetMinorTask`|WRITE|Set minor.
|`SetMeasurePowerTask`|WRITE|Set iBeacon measurePower.
|`SetTransmissionTask`|WRITE|Set iBeacon transmission.
|`SetUUIDTask`|WRITE|Set iBeacon UUID.
|`SetAdvIntervalTask`|WRITE|Set adv interval.
|`WriteConfigTask`|WRITE|Call `setAdvMoveCondition(int seconds)`，set adv trigger.

...

* **Create tasks**

The task callback (`MokoOrderTaskCallback`) and task type need to be passed when creating a task. Some tasks also need corresponding parameters to be passed.

Examples of creating tasks are as follows:

```
	 // read
    public OrderTask getManufacturer() {
        GetManufacturerTask getManufacturerTask = new GetManufacturerTask(this);
        return getManufacturerTask;
    }
    // write
    public OrderTask setConnectionMode(int connectionMode) {
        SetConnectionModeTask setConnectionModeTask = new SetConnectionModeTask(this);
        setConnectionModeTask.setData(connectionMode);
        return setConnectionModeTask;
    } 
    public OrderTask deleteTrackedData() {
        WriteConfigTask task = new WriteConfigTask(this);
        task.setData(ConfigKeyEnum.DELETE_STORE_DATA);
        return task;
    }
    // notify
    public OrderTask openTrackedNotify() {
        OpenNotifyTask task = new OpenNotifyTask(OrderType.STORE_DATA_NOTIFY, this);
        return task;
    }   
    
```

* **Send tasks**

```
MokoSupport.getInstance().sendOrder(OrderTask... orderTasks);
```

The task can be one or more.

* **Task callback**

```java
/**
 * @ClassPath com.moko.support.callback.OrderCallback
 */
public interface MokoOrderTaskCallback {

    void onOrderResult(OrderTaskResponse response);

    void onOrderTimeout(OrderTaskResponse response);

    void onOrderFinish();
}
```
`void onOrderResult(OrderTaskResponse response);`

	After the task is sent to the device, the data returned by the device can be obtained by using the `onOrderResult` function, and you can determine witch class the task is according to the `response.orderType` function. The `response.responseValue` is the returned data.

`void onOrderTimeout(OrderTaskResponse response);`

	Every task has a default timeout of 3 seconds to prevent the device from failing to return data due to a fault and the fail will cause other tasks in the queue can not execute normally. After the timeout, the `onOrderTimeout` will be called back. You can determine witch class the task is according to the `response.orderType` function and then the next task continues.

`void onOrderFinish();`

	When the task in the queue is empty, `onOrderFinish` will be called back.

* **Listening task**

If the task belongs to `NOTIFY` and ` WRITE_NO_RESPONSE` task has been sent, the task is in listening state. When there is data returned from the device, the data will be sent in the form of broadcast, and the action of receiving broadcast is `MokoConstants.ACTION_CURRENT_DATA`.

```
String action = intent.getAction();
...
if (MokoConstants.ACTION_CURRENT_DATA.equals(action)) {
                    OrderType orderType = (OrderType) intent.getSerializableExtra(MokoConstants.EXTRA_KEY_CURRENT_DATA_TYPE);
                    byte[] value = intent.getByteArrayExtra(MokoConstants.EXTRA_KEY_RESPONSE_VALUE);
                    ...
                }
```

Get `OrderTaskResponse` from the **intent** of `onReceive`, and the corresponding **key** value is `MokoConstants.EXTRA_KEY_RESPONSE_ORDER_TASK`.

## 3. Special instructions

> 1. AndroidManifest.xml of SDK has declared to access SD card and get Bluetooth permissions.
> 2. The SDK comes with logging, and if you want to view the log in the SD card, please to use "LogModule". The log path is : root directory of SD card/ContactTracker/ContactTracker. It only records the log of the day and the day before.
> 3. Just connecting to the device successfully, it needs to delay 1 second before sending data, otherwise the device can not return data normally.
> 4. We suggest that sending and receiving data should be executed in the "Service". There will be a certain delay when the device returns data, and you can broadcast data to the "Activity" after receiving in the "Service". Please refer to the "Demo Project".















