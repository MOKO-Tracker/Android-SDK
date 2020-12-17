package com.moko.contacttracker.activity;


import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.moko.contacttracker.R;
import com.moko.contacttracker.dialog.AlertMessageDialog;
import com.moko.contacttracker.dialog.LoadingMessageDialog;
import com.moko.contacttracker.utils.ToastUtils;
import com.moko.support.MokoConstants;
import com.moko.support.MokoSupport;
import com.moko.support.OrderTaskAssembler;
import com.moko.support.entity.ConfigKeyEnum;
import com.moko.support.entity.OrderType;
import com.moko.support.event.ConnectStatusEvent;
import com.moko.support.event.OrderTaskResponseEvent;
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

import androidx.constraintlayout.widget.ConstraintLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FilterOptionsNewActivity extends BaseActivity implements SeekBar.OnSeekBarChangeListener {

    public static final String UUID_PATTERN = "[A-Fa-f0-9]{8}-(?:[A-Fa-f0-9]{4}-){3}[A-Fa-f0-9]{12}";
    private final String FILTER_ASCII = "\\A\\p{ASCII}*\\z";
    @BindView(R.id.sb_rssi_filter)
    SeekBar sbRssiFilter;
    @BindView(R.id.tv_rssi_filter_value)
    TextView tvRssiFilterValue;
    @BindView(R.id.tv_rssi_filter_tips)
    TextView tvRssiFilterTips;
    @BindView(R.id.iv_adv_data_filter)
    ImageView ivAdvDataFilter;
    @BindView(R.id.iv_mac_address)
    ImageView ivMacAddress;
    @BindView(R.id.et_mac_address)
    EditText etMacAddress;
    @BindView(R.id.iv_adv_name)
    ImageView ivAdvName;
    @BindView(R.id.et_adv_name)
    EditText etAdvName;
    @BindView(R.id.iv_ibeacon_uuid)
    ImageView ivIbeaconUuid;
    @BindView(R.id.et_ibeacon_uuid)
    EditText etIbeaconUuid;
    @BindView(R.id.iv_ibeacon_major)
    ImageView ivIbeaconMajor;
    @BindView(R.id.iv_ibeacon_minor)
    ImageView ivIbeaconMinor;
    @BindView(R.id.iv_raw_adv_data)
    ImageView ivRawAdvData;
    @BindView(R.id.et_raw_adv_data)
    EditText etRawAdvData;
    @BindView(R.id.cl_adv_data_filter)
    ConstraintLayout clAdvDataFilter;
    @BindView(R.id.et_ibeacon_major_min)
    EditText etIbeaconMajorMin;
    @BindView(R.id.et_ibeacon_major_max)
    EditText etIbeaconMajorMax;
    @BindView(R.id.ll_ibeacon_major)
    LinearLayout llIbeaconMajor;
    @BindView(R.id.et_ibeacon_minor_min)
    EditText etIbeaconMinorMin;
    @BindView(R.id.et_ibeacon_minor_max)
    EditText etIbeaconMinorMax;
    @BindView(R.id.ll_ibeacon_minor)
    LinearLayout llIbeaconMinor;
    private boolean mReceiverTag = false;

    private Pattern pattern;

    private boolean savedParamsError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_new);
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
        InputFilter inputFilter = (source, start, end, dest, dstart, dend) -> {
            if (!(source + "").matches(FILTER_ASCII)) {
                return "";
            }

            return null;
        };
        etAdvName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10), inputFilter});
        
        EventBus.getDefault().register(this);
        // 注册广播接收器
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);
        mReceiverTag = true;
        if (!MokoSupport.getInstance().isBluetoothOpen()) {
            MokoSupport.getInstance().enableBluetooth();
        } else {
            showSyncingProgressDialog();
            List<OrderTask> orderTasks = new ArrayList<>();
            orderTasks.add(OrderTaskAssembler.getRssiFilter());
            orderTasks.add(OrderTaskAssembler.getFilterEnable());
            orderTasks.add(OrderTaskAssembler.getFilterMac());
            orderTasks.add(OrderTaskAssembler.getFilterName());
            orderTasks.add(OrderTaskAssembler.getFilterUUID());
            orderTasks.add(OrderTaskAssembler.getFilterMajorRange());
            orderTasks.add(OrderTaskAssembler.getFilterMinorRange());
            orderTasks.add(OrderTaskAssembler.getFilterAdvRawData());
            MokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
        }
    }

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

    @Subscribe(threadMode = ThreadMode.POSTING, priority = 200)
    public void onOrderTaskResponseEvent(OrderTaskResponseEvent event) {
        EventBus.getDefault().cancelEventDelivery(event);
        final String action = event.getAction();
        runOnUiThread(() -> {
            if (MokoConstants.ACTION_CURRENT_DATA.equals(action)) {
            }
            if (MokoConstants.ACTION_ORDER_TIMEOUT.equals(action)) {
            }
            if (MokoConstants.ACTION_ORDER_FINISH.equals(action)) {
                dismissSyncProgressDialog();
            }
            if (MokoConstants.ACTION_ORDER_RESULT.equals(action)) {
                OrderTaskResponse response = event.getResponse();
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
                                case GET_FILTER_MAJOR_RANGE:
                                    if (length > 0) {
                                        filterMajorEnable = length != 1;
                                        ivIbeaconMajor.setImageResource(filterMajorEnable ? R.drawable.ic_checked : R.drawable.ic_unchecked);
                                        llIbeaconMajor.setVisibility(filterMajorEnable ? View.VISIBLE : View.GONE);
                                        if (length > 1) {
                                            byte[] majorMinBytes = Arrays.copyOfRange(value, 4, 6);
                                            int majorMin = MokoUtils.toInt(majorMinBytes);
                                            etIbeaconMajorMin.setText(String.valueOf(majorMin));
                                            byte[] majorMaxBytes = Arrays.copyOfRange(value, 6, 8);
                                            int majorMax = MokoUtils.toInt(majorMaxBytes);
                                            etIbeaconMajorMax.setText(String.valueOf(majorMax));
                                        }
                                    }
                                    break;
                                case GET_FILTER_MINOR_RANGE:
                                    if (length > 0) {
                                        filterMinorEnable = length != 1;
                                        ivIbeaconMinor.setImageResource(filterMinorEnable ? R.drawable.ic_checked : R.drawable.ic_unchecked);
                                        llIbeaconMinor.setVisibility(filterMinorEnable ? View.VISIBLE : View.GONE);
                                        if (length > 1) {
                                            byte[] minorMinBytes = Arrays.copyOfRange(value, 4, 6);
                                            int minorMin = MokoUtils.toInt(minorMinBytes);
                                            etIbeaconMinorMin.setText(String.valueOf(minorMin));
                                            byte[] minorMaxBytes = Arrays.copyOfRange(value, 6, 8);
                                            int minorMax = MokoUtils.toInt(minorMaxBytes);
                                            etIbeaconMinorMax.setText(String.valueOf(minorMax));
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
                                case SET_FILTER_MAJOR_RANGE:
                                case SET_FILTER_MINOR_RANGE:
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
                                        ToastUtils.showToast(FilterOptionsNewActivity.this, "Opps！Save failed. Please check the input characters and try again.");
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
        });
    }


    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent != null) {
                String action = intent.getAction();
                if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                    int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                    switch (blueState) {
                        case BluetoothAdapter.STATE_TURNING_OFF:
                            dismissSyncProgressDialog();
                            FilterOptionsNewActivity.this.setResult(RESULT_OK);
                            finish();
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
                llIbeaconMajor.setVisibility(filterMajorEnable ? View.VISIBLE : View.GONE);
                break;
            case R.id.iv_ibeacon_minor:
                filterMinorEnable = !filterMinorEnable;
                ivIbeaconMinor.setImageResource(filterMinorEnable ? R.drawable.ic_checked : R.drawable.ic_unchecked);
                llIbeaconMinor.setVisibility(filterMinorEnable ? View.VISIBLE : View.GONE);
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
        final String majorMin = etIbeaconMajorMin.getText().toString();
        final String majorMax = etIbeaconMajorMax.getText().toString();
        final String minorMin = etIbeaconMinorMin.getText().toString();
        final String minorMax = etIbeaconMinorMax.getText().toString();
        final String rawData = etRawAdvData.getText().toString();

        orderTasks.add(OrderTaskAssembler.setFilterRssi(filterRssi));
        if (advDataFilterEnable) {
            orderTasks.add(OrderTaskAssembler.setFilterMac(filterMacEnable ? mac : ""));
            orderTasks.add(OrderTaskAssembler.setFilterName(filterNameEnable ? name : ""));
            orderTasks.add(OrderTaskAssembler.setFilterUUID(filterUUIDEnable ? uuidStr : ""));
            orderTasks.add(OrderTaskAssembler.setFilterMajorRange(
                    filterMajorEnable ? 1 : 0,
                    filterMajorEnable ? Integer.parseInt(majorMin) : 0,
                    filterMajorEnable ? Integer.parseInt(majorMax) : 0));
            orderTasks.add(OrderTaskAssembler.setFilterMinorRange(
                    filterMinorEnable ? 1 : 0,
                    filterMinorEnable ? Integer.parseInt(minorMin) : 0,
                    filterMinorEnable ? Integer.parseInt(minorMax) : 0));
            orderTasks.add(OrderTaskAssembler.setFilterAdvRawData(filterRawAdvDataEnable ? rawData : ""));
            orderTasks.add(OrderTaskAssembler.setFilterEnable(0));
        } else {
            orderTasks.add(OrderTaskAssembler.setFilterEnable(1));
        }

        MokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
    }

    private boolean isValid() {
        final String mac = etMacAddress.getText().toString();
        final String name = etAdvName.getText().toString();
        final String uuid = etIbeaconUuid.getText().toString();
        final String majorMin = etIbeaconMajorMin.getText().toString();
        final String majorMax = etIbeaconMajorMax.getText().toString();
        final String minorMin = etIbeaconMinorMin.getText().toString();
        final String minorMax = etIbeaconMinorMax.getText().toString();
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
            if (TextUtils.isEmpty(majorMin))
                return false;
            if (Integer.parseInt(majorMin) > 65535)
                return false;
            if (TextUtils.isEmpty(majorMax))
                return false;
            if (Integer.parseInt(majorMax) > 65535)
                return false;
            if (Integer.parseInt(majorMin) > Integer.parseInt(majorMax))
                return false;

        }
        if (filterMinorEnable) {
            if (TextUtils.isEmpty(minorMin))
                return false;
            if (Integer.parseInt(minorMin) > 65535)
                return false;
            if (TextUtils.isEmpty(minorMax))
                return false;
            if (Integer.parseInt(minorMax) > 65535)
                return false;
            if (Integer.parseInt(minorMin) > Integer.parseInt(minorMax))
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
