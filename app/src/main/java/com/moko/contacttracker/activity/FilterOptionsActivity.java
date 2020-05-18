package com.moko.contacttracker.activity;


import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.constraint.ConstraintLayout;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.moko.contacttracker.R;
import com.moko.contacttracker.dialog.AlertMessageDialog;
import com.moko.contacttracker.dialog.LoadingMessageDialog;
import com.moko.contacttracker.service.MokoService;
import com.moko.contacttracker.utils.ToastUtils;
import com.moko.support.MokoConstants;
import com.moko.support.MokoSupport;
import com.moko.support.entity.ConfigKeyEnum;
import com.moko.support.entity.OrderType;
import com.moko.support.event.ConnectStatusEvent;
import com.moko.support.task.OrderTask;
import com.moko.support.task.OrderTaskResponse;
import com.moko.support.utils.MokoUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FilterOptionsActivity extends BaseActivity implements SeekBar.OnSeekBarChangeListener {

    public static final String UUID_PATTERN = "[A-Fa-f0-9]{8}-(?:[A-Fa-f0-9]{4}-){3}[A-Fa-f0-9]{12}";
    private final String FILTER_ASCII = "\\A\\p{ASCII}*\\z";
    @Bind(R.id.sb_rssi_filter)
    SeekBar sbRssiFilter;
    @Bind(R.id.tv_rssi_filter_value)
    TextView tvRssiFilterValue;
    @Bind(R.id.tv_rssi_filter_tips)
    TextView tvRssiFilterTips;
    @Bind(R.id.iv_adv_data_filter)
    ImageView ivAdvDataFilter;
    @Bind(R.id.iv_mac_address)
    ImageView ivMacAddress;
    @Bind(R.id.et_mac_address)
    EditText etMacAddress;
    @Bind(R.id.iv_adv_name)
    ImageView ivAdvName;
    @Bind(R.id.et_adv_name)
    EditText etAdvName;
    @Bind(R.id.iv_ibeacon_uuid)
    ImageView ivIbeaconUuid;
    @Bind(R.id.et_ibeacon_uuid)
    EditText etIbeaconUuid;
    @Bind(R.id.iv_ibeacon_major)
    ImageView ivIbeaconMajor;
    @Bind(R.id.et_ibeacon_major)
    EditText etIbeaconMajor;
    @Bind(R.id.iv_ibeacon_minor)
    ImageView ivIbeaconMinor;
    @Bind(R.id.et_ibeacon_minor)
    EditText etIbeaconMinor;
    @Bind(R.id.iv_raw_adv_data)
    ImageView ivRawAdvData;
    @Bind(R.id.et_raw_adv_data)
    EditText etRawAdvData;
    @Bind(R.id.cl_adv_data_filter)
    ConstraintLayout clAdvDataFilter;
    private boolean mReceiverTag = false;
    public MokoService mMokoService;

    private Pattern pattern;

    private boolean savedParamsError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        ButterKnife.bind(this);

        sbRssiFilter.setOnSeekBarChangeListener(this);

        pattern = Pattern.compile(UUID_PATTERN);
        etIbeaconUuid.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String input = s.toString();
                if (!pattern.matcher(input).matches()) {
                    if (input.length() == 9 && !input.endsWith("-")) {
                        String show = input.substring(0, 8) + "-" + input.substring(8, input.length());
                        etIbeaconUuid.setText(show);
                        etIbeaconUuid.setSelection(show.length());
                    }
                    if (input.length() == 14 && !input.endsWith("-")) {
                        String show = input.substring(0, 13) + "-" + input.substring(13, input.length());
                        etIbeaconUuid.setText(show);
                        etIbeaconUuid.setSelection(show.length());
                    }
                    if (input.length() == 19 && !input.endsWith("-")) {
                        String show = input.substring(0, 18) + "-" + input.substring(18, input.length());
                        etIbeaconUuid.setText(show);
                        etIbeaconUuid.setSelection(show.length());
                    }
                    if (input.length() == 24 && !input.endsWith("-")) {
                        String show = input.substring(0, 23) + "-" + input.substring(23, input.length());
                        etIbeaconUuid.setText(show);
                        etIbeaconUuid.setSelection(show.length());
                    }
                    if (input.length() == 32 && input.indexOf("-") < 0) {
                        StringBuilder stringBuilder = new StringBuilder(input);
                        stringBuilder.insert(8, "-");
                        stringBuilder.insert(13, "-");
                        stringBuilder.insert(18, "-");
                        stringBuilder.insert(23, "-");
                        etIbeaconUuid.setText(stringBuilder.toString());
                        etIbeaconUuid.setSelection(stringBuilder.toString().length());
                    }
                }
            }
        });
        InputFilter filter = (source, start, end, dest, dstart, dend) -> {
            if (!(source + "").matches(FILTER_ASCII)) {
                return "";
            }

            return null;
        };
        etAdvName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10), filter});
        Intent intent = new Intent(this, MokoService.class);
        bindService(intent, mServiceConnection, BIND_AUTO_CREATE);
        EventBus.getDefault().register(this);
    }


    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mMokoService = ((MokoService.LocalBinder) service).getService();
            // 注册广播接收器
            IntentFilter filter = new IntentFilter();
            filter.addAction(MokoConstants.ACTION_ORDER_RESULT);
            filter.addAction(MokoConstants.ACTION_ORDER_TIMEOUT);
            filter.addAction(MokoConstants.ACTION_ORDER_FINISH);
            filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
            filter.setPriority(300);
            registerReceiver(mReceiver, filter);
            mReceiverTag = true;
            if (!MokoSupport.getInstance().isBluetoothOpen()) {
                MokoSupport.getInstance().enableBluetooth();
            } else {
                if (mMokoService == null) {
                    finish();
                    return;
                }
                showSyncingProgressDialog();
                List<OrderTask> orderTasks = new ArrayList<>();
                orderTasks.add(mMokoService.getRssiFilter());
                orderTasks.add(mMokoService.getFilterEnable());
                orderTasks.add(mMokoService.getFilterMac());
                orderTasks.add(mMokoService.getFilterName());
                orderTasks.add(mMokoService.getFilterUUID());
                orderTasks.add(mMokoService.getFilterMajor());
                orderTasks.add(mMokoService.getFilterMinor());
                orderTasks.add(mMokoService.getFilterAdvRawData());
                MokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    @Subscribe(threadMode = ThreadMode.POSTING, priority = 200)
    public void onConnectStatusEvent(ConnectStatusEvent event) {
        final String action = event.getAction();
        runOnUiThread(() -> {
            if (MokoConstants.ACTION_CONN_STATUS_DISCONNECTED.equals(action)) {
                setResult(RESULT_OK);
                finish();
            }
        });

    }


    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent != null) {
                String action = intent.getAction();
                if (!BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                    abortBroadcast();
                }
                if (MokoConstants.ACTION_ORDER_TIMEOUT.equals(action)) {
                }
                if (MokoConstants.ACTION_ORDER_FINISH.equals(action)) {
                    dismissSyncProgressDialog();
                }
                if (MokoConstants.ACTION_ORDER_RESULT.equals(action)) {
                    OrderTaskResponse response = (OrderTaskResponse) intent.getSerializableExtra(MokoConstants.EXTRA_KEY_RESPONSE_ORDER_TASK);
                    OrderType orderType = response.orderType;
                    int responseType = response.responseType;
                    byte[] value = response.responseValue;
                    switch (orderType) {
                        case WRITE_CONFIG:
                            if (value.length >= 2) {
                                int key = value[1] & 0xFF;
                                ConfigKeyEnum configKeyEnum = ConfigKeyEnum.fromConfigKey(key);
                                if (configKeyEnum == null) {
                                    return;
                                }
                                int length = value[3] & 0xFF;
                                switch (configKeyEnum) {
                                    case GET_STORE_RSSI_CONDITION:
                                        if (length == 1) {
                                            final int rssi = value[4];
                                            int progress = rssi + 127;
                                            sbRssiFilter.setProgress(progress);
                                            tvRssiFilterValue.setText(String.format("%ddBm", rssi));
                                            tvRssiFilterTips.setText(getString(R.string.rssi_filter, rssi));
                                        }
                                        break;
                                    case GET_FILTER_ENABLE:
                                        if (length == 1) {
                                            final int enable = value[4] & 0xFF;
                                            advDataFilterEnable = enable == 0;
                                            ivAdvDataFilter.setImageResource(advDataFilterEnable ? R.drawable.ic_checked : R.drawable.ic_unchecked);
                                            clAdvDataFilter.setVisibility(advDataFilterEnable ? View.VISIBLE : View.GONE);
                                        }
                                        break;
                                    case GET_FILTER_MAC:
                                        if (length > 0) {
                                            final int enable = value[4] & 0xFF;
                                            filterMacEnable = enable == 1;
                                            ivMacAddress.setImageResource(filterMacEnable ? R.drawable.ic_checked : R.drawable.ic_unchecked);
                                            etMacAddress.setVisibility(filterMacEnable ? View.VISIBLE : View.GONE);
                                            if (length > 1) {
                                                byte[] macBytes = Arrays.copyOfRange(value, 5, 4 + length);
                                                String filterMac = MokoUtils.bytesToHexString(macBytes).toUpperCase();
                                                etMacAddress.setText(filterMac);
                                            }
                                        }
                                        break;
                                    case GET_FILTER_NAME:
                                        if (length > 0) {
                                            final int enable = value[4] & 0xFF;
                                            filterNameEnable = enable == 1;
                                            ivAdvName.setImageResource(filterNameEnable ? R.drawable.ic_checked : R.drawable.ic_unchecked);
                                            etAdvName.setVisibility(filterNameEnable ? View.VISIBLE : View.GONE);
                                            if (length > 1) {
                                                byte[] nameBytes = Arrays.copyOfRange(value, 5, 4 + length);
                                                String filterName = new String(nameBytes);
                                                etAdvName.setText(filterName);
                                            }
                                        }
                                        break;
                                    case GET_FILTER_UUID:
                                        if (length > 0) {
                                            filterUUIDEnable = length != 1;
                                            ivIbeaconUuid.setImageResource(filterUUIDEnable ? R.drawable.ic_checked : R.drawable.ic_unchecked);
                                            etIbeaconUuid.setVisibility(filterUUIDEnable ? View.VISIBLE : View.GONE);
                                            if (length > 1) {
                                                byte[] uuidBytes = Arrays.copyOfRange(value, 4, 4 + length);
                                                String filterUUID = MokoUtils.bytesToHexString(uuidBytes).toUpperCase();
                                                StringBuilder stringBuilder = new StringBuilder(filterUUID);
                                                stringBuilder.insert(8, "-");
                                                stringBuilder.insert(13, "-");
                                                stringBuilder.insert(18, "-");
                                                stringBuilder.insert(23, "-");
                                                etIbeaconUuid.setText(stringBuilder.toString());
                                            }
                                        }
                                        break;
                                    case GET_FILTER_MAJOR:
                                        if (length > 0) {
                                            filterMajorEnable = length != 1;
                                            ivIbeaconMajor.setImageResource(filterMajorEnable ? R.drawable.ic_checked : R.drawable.ic_unchecked);
                                            etIbeaconMajor.setVisibility(filterMajorEnable ? View.VISIBLE : View.GONE);
                                            if (length > 1) {
                                                byte[] majorBytes = Arrays.copyOfRange(value, 4, 4 + length);
                                                int major = MokoUtils.toInt(majorBytes);
                                                etIbeaconMajor.setText(String.valueOf(major));
                                            }
                                        }
                                        break;
                                    case GET_FILTER_MINOR:
                                        if (length > 0) {
                                            filterMinorEnable = length != 1;
                                            ivIbeaconMinor.setImageResource(filterMinorEnable ? R.drawable.ic_checked : R.drawable.ic_unchecked);
                                            etIbeaconMinor.setVisibility(filterMinorEnable ? View.VISIBLE : View.GONE);
                                            if (length > 1) {
                                                byte[] minorBytes = Arrays.copyOfRange(value, 4, 4 + length);
                                                int minor = MokoUtils.toInt(minorBytes);
                                                etIbeaconMinor.setText(String.valueOf(minor));
                                            }
                                        }
                                        break;
                                    case GET_FILTER_ADV_RAW_DATA:
                                        if (length > 0) {
                                            final int enable = value[4] & 0xFF;
                                            filterRawAdvDataEnable = enable == 1;
                                            ivRawAdvData.setImageResource(filterRawAdvDataEnable ? R.drawable.ic_checked : R.drawable.ic_unchecked);
                                            etRawAdvData.setVisibility(filterRawAdvDataEnable ? View.VISIBLE : View.GONE);
                                            if (length > 1) {
                                                byte[] rawDataBytes = Arrays.copyOfRange(value, 5, 4 + length);
                                                String filterRawData = MokoUtils.bytesToHexString(rawDataBytes).toUpperCase();
                                                etRawAdvData.setText(filterRawData);
                                            }
                                        }
                                        break;
                                    case SET_STORE_RSSI_CONDITION:
                                    case SET_FILTER_MAC:
                                    case SET_FILTER_NAME:
                                    case SET_FILTER_UUID:
                                    case SET_FILTER_MAJOR:
                                    case SET_FILTER_MINOR:
                                    case SET_FILTER_ADV_RAW_DATA:
                                        if (length != 0) {
                                            savedParamsError = true;
                                        }
                                        break;
                                    case SET_FILTER_ENABLE:
                                        if (length != 0) {
                                            savedParamsError = true;
                                        }
                                        if (savedParamsError) {
                                            ToastUtils.showToast(FilterOptionsActivity.this, "Opps！Save failed. Please check the input characters and try again.");
                                        } else {
                                            AlertMessageDialog dialog = new AlertMessageDialog();
                                            dialog.setMessage("Saved Successfully！");
                                            dialog.setConfirm("OK");
                                            dialog.setCancelGone();
                                            dialog.show(getSupportFragmentManager());
                                        }
                                        break;
                                }
                            }
                            break;
                    }
                }
                if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                    int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                    switch (blueState) {
                        case BluetoothAdapter.STATE_TURNING_OFF:
                            dismissSyncProgressDialog();
//                            AlertDialog.Builder builder = new AlertDialog.Builder(FilterOptionsActivity.this);
//                            builder.setTitle("Dismiss");
//                            builder.setCancelable(false);
//                            builder.setMessage("The current system of bluetooth is not available!");
//                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
                            FilterOptionsActivity.this.setResult(RESULT_OK);
                            finish();
//                                }
//                            });
//                            builder.show();
                            break;
                    }
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReceiverTag) {
            mReceiverTag = false;
            // 注销广播
            unregisterReceiver(mReceiver);
        }
        unbindService(mServiceConnection);
        EventBus.getDefault().unregister(this);
    }

    private LoadingMessageDialog mLoadingMessageDialog;

    public void showSyncingProgressDialog() {
        mLoadingMessageDialog = new LoadingMessageDialog();
        mLoadingMessageDialog.setMessage("Syncing..");
        mLoadingMessageDialog.show(getSupportFragmentManager());

    }

    public void dismissSyncProgressDialog() {
        if (mLoadingMessageDialog != null)
            mLoadingMessageDialog.dismissAllowingStateLoss();
    }

    private boolean advDataFilterEnable;
    private boolean filterMacEnable;
    private boolean filterNameEnable;
    private boolean filterUUIDEnable;
    private boolean filterMajorEnable;
    private boolean filterMinorEnable;
    private boolean filterRawAdvDataEnable;

    @OnClick({R.id.tv_back, R.id.iv_save, R.id.iv_adv_data_filter, R.id.iv_mac_address,
            R.id.iv_adv_name, R.id.iv_ibeacon_uuid, R.id.iv_ibeacon_major,
            R.id.iv_ibeacon_minor, R.id.iv_raw_adv_data})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                finish();
                break;
            case R.id.iv_save:
                if (isValid()) {
                    showSyncingProgressDialog();
                    saveParams();
                } else {
                    ToastUtils.showToast(this, "Opps！Save failed. Please check the input characters and try again.");
                }
                break;
            case R.id.iv_adv_data_filter:
                advDataFilterEnable = !advDataFilterEnable;
                ivAdvDataFilter.setImageResource(advDataFilterEnable ? R.drawable.ic_checked : R.drawable.ic_unchecked);
                clAdvDataFilter.setVisibility(advDataFilterEnable ? View.VISIBLE : View.GONE);
                break;
            case R.id.iv_mac_address:
                filterMacEnable = !filterMacEnable;
                ivMacAddress.setImageResource(filterMacEnable ? R.drawable.ic_checked : R.drawable.ic_unchecked);
                etMacAddress.setVisibility(filterMacEnable ? View.VISIBLE : View.GONE);
                break;
            case R.id.iv_adv_name:
                filterNameEnable = !filterNameEnable;
                ivAdvName.setImageResource(filterNameEnable ? R.drawable.ic_checked : R.drawable.ic_unchecked);
                etAdvName.setVisibility(filterNameEnable ? View.VISIBLE : View.GONE);
                break;
            case R.id.iv_ibeacon_uuid:
                filterUUIDEnable = !filterUUIDEnable;
                ivIbeaconUuid.setImageResource(filterUUIDEnable ? R.drawable.ic_checked : R.drawable.ic_unchecked);
                etIbeaconUuid.setVisibility(filterUUIDEnable ? View.VISIBLE : View.GONE);
                break;
            case R.id.iv_ibeacon_major:
                filterMajorEnable = !filterMajorEnable;
                ivIbeaconMajor.setImageResource(filterMajorEnable ? R.drawable.ic_checked : R.drawable.ic_unchecked);
                etIbeaconMajor.setVisibility(filterMajorEnable ? View.VISIBLE : View.GONE);
                break;
            case R.id.iv_ibeacon_minor:
                filterMinorEnable = !filterMinorEnable;
                ivIbeaconMinor.setImageResource(filterMinorEnable ? R.drawable.ic_checked : R.drawable.ic_unchecked);
                etIbeaconMinor.setVisibility(filterMinorEnable ? View.VISIBLE : View.GONE);
                break;
            case R.id.iv_raw_adv_data:
                filterRawAdvDataEnable = !filterRawAdvDataEnable;
                ivRawAdvData.setImageResource(filterRawAdvDataEnable ? R.drawable.ic_checked : R.drawable.ic_unchecked);
                etRawAdvData.setVisibility(filterRawAdvDataEnable ? View.VISIBLE : View.GONE);
                break;
        }
    }

    private void saveParams() {
        final int progress = sbRssiFilter.getProgress();
        int filterRssi = progress - 127;
        List<OrderTask> orderTasks = new ArrayList<>();
        final String mac = etMacAddress.getText().toString();
        final String name = etAdvName.getText().toString();
        final String uuid = etIbeaconUuid.getText().toString();
        String uuidStr = uuid.replaceAll("-", "");
        final String major = etIbeaconMajor.getText().toString();
        final String minor = etIbeaconMinor.getText().toString();
        final String rawData = etRawAdvData.getText().toString();
        String majorStr = "";
        if (!TextUtils.isEmpty(major)) {
            majorStr = String.format("%04x", Integer.parseInt(major));
        }
        String minorStr = "";
        if (!TextUtils.isEmpty(minor)) {
            minorStr = String.format("%04x", Integer.parseInt(minor));
        }

        orderTasks.add(mMokoService.setFilterRssi(filterRssi));
        if (advDataFilterEnable) {
            orderTasks.add(mMokoService.setFilterMac(filterMacEnable ? mac : ""));
            orderTasks.add(mMokoService.setFilterName(filterNameEnable ? name : ""));
            orderTasks.add(mMokoService.setFilterUUID(filterUUIDEnable ? uuidStr : ""));
            orderTasks.add(mMokoService.setFilterMajor(filterMajorEnable ? majorStr : ""));
            orderTasks.add(mMokoService.setFilterMinor(filterMinorEnable ? minorStr : ""));
            orderTasks.add(mMokoService.setFilterAdvRawData(filterRawAdvDataEnable ? rawData : ""));
            orderTasks.add(mMokoService.setFilterEnable(0));
        } else {
            orderTasks.add(mMokoService.setFilterEnable(1));
        }

        MokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
    }

    private boolean isValid() {
        final String mac = etMacAddress.getText().toString();
        final String name = etAdvName.getText().toString();
        final String uuid = etIbeaconUuid.getText().toString();
        final String major = etIbeaconMajor.getText().toString();
        final String minor = etIbeaconMinor.getText().toString();
        final String rawData = etRawAdvData.getText().toString();
        if (!advDataFilterEnable) {
            return true;
        }
        if (filterMacEnable) {
            if (TextUtils.isEmpty(mac))
                return false;
            int length = mac.length();
            if (length % 2 != 0)
                return false;
        }
        if (filterNameEnable) {
            if (TextUtils.isEmpty(name))
                return false;
        }
        if (filterUUIDEnable) {
            if (TextUtils.isEmpty(uuid))
                return false;
            int length = uuid.length();
            if (length != 36)
                return false;
        }
        if (filterMajorEnable) {
            if (TextUtils.isEmpty(major))
                return false;
            if (Integer.parseInt(major) > 65535)
                return false;
        }
        if (filterMinorEnable) {
            if (TextUtils.isEmpty(minor))
                return false;
            if (Integer.parseInt(minor) > 65535)
                return false;
        }
        if (filterRawAdvDataEnable) {
            if (TextUtils.isEmpty(rawData))
                return false;
            int length = rawData.length();
            if (length % 2 != 0)
                return false;

        }
        return true;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        int rssi = progress - 127;
        tvRssiFilterValue.setText(String.format("%ddBm", rssi));
        tvRssiFilterTips.setText(getString(R.string.rssi_filter, rssi));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
