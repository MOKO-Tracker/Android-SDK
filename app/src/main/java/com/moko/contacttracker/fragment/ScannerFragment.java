package com.moko.contacttracker.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.moko.contacttracker.R;
import com.moko.contacttracker.activity.DeviceInfoActivity;
import com.moko.contacttracker.activity.ExportDataActivity;
import com.moko.contacttracker.activity.FilterOptionsActivity;
import com.moko.contacttracker.activity.FilterOptionsNewActivity;
import com.moko.support.MokoSupport;
import com.moko.support.OrderTaskAssembler;
import com.moko.support.task.OrderTask;

import java.util.ArrayList;
import java.util.List;

import androidx.constraintlayout.widget.ConstraintLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.carbswang.android.numberpickerview.library.NumberPickerView;

public class ScannerFragment extends Fragment implements SeekBar.OnSeekBarChangeListener, NumberPickerView.OnValueChangeListener {
    private static final String TAG = ScannerFragment.class.getSimpleName();
    @BindView(R.id.sb_storage_interval)
    SeekBar sbStorageInterval;
    @BindView(R.id.tv_storage_interval_value)
    TextView tvStorageIntervalValue;
    @BindView(R.id.tv_storage_interval_tips)
    TextView tvStorageIntervalTips;
    @BindView(R.id.npv_tracking_notify)
    NumberPickerView npvTrackingNotify;
    @BindView(R.id.iv_scanner_trigger)
    ImageView ivScannerTrigger;
    @BindView(R.id.et_scanner_trigger)
    EditText etScannerTrigger;
    @BindView(R.id.cl_scanner_trigger)
    ConstraintLayout clScannerTrigger;
    @BindView(R.id.scanner_trigger)
    TextView scannerTrigger;
    @BindView(R.id.npv_vibrations_number)
    NumberPickerView npvVibrationsNumber;
    @BindView(R.id.rl_vibrations_number)
    RelativeLayout rlVibrationsNumber;

    private DeviceInfoActivity activity;

    public ScannerFragment() {
    }


    public static ScannerFragment newInstance() {
        ScannerFragment fragment = new ScannerFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView: ");
        View view = inflater.inflate(R.layout.fragment_scanner, container, false);
        ButterKnife.bind(this, view);
        activity = (DeviceInfoActivity) getActivity();
        sbStorageInterval.setOnSeekBarChangeListener(this);
        npvTrackingNotify.setDisplayedValues(getResources().getStringArray(R.array.tracking_notify));
        npvTrackingNotify.setMaxValue(3);
        npvTrackingNotify.setMinValue(0);
        npvTrackingNotify.setValue(0);
        npvTrackingNotify.setOnValueChangedListener(this);

        npvVibrationsNumber.setDisplayedValues(getResources().getStringArray(R.array.vibrations_number));
        npvVibrationsNumber.setMaxValue(5);
        npvVibrationsNumber.setMinValue(0);
        npvVibrationsNumber.setValue(0);
        return view;
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.sb_storage_interval:
                tvStorageIntervalValue.setText(String.format("%dmin", progress));
                tvStorageIntervalTips.setText(getString(R.string.storage_interval, progress));
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onPause() {
        Log.i(TAG, "onPause: ");
        super.onPause();
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy: ");
        super.onDestroy();
    }

    private boolean isScannerTriggerOpen;

    public void setScannerTriggerClose() {
        isScannerTriggerOpen = false;
        ivScannerTrigger.setImageResource(R.drawable.ic_unchecked);
        clScannerTrigger.setVisibility(View.GONE);
    }

    public void setScannerTrigger(int duration) {
        isScannerTriggerOpen = true;
        ivScannerTrigger.setImageResource(R.drawable.ic_checked);
        clScannerTrigger.setVisibility(View.VISIBLE);
        etScannerTrigger.setText(String.valueOf(duration));
        etScannerTrigger.setSelection(String.valueOf(duration).length());
    }

    public void disableTrigger() {
        scannerTrigger.setVisibility(View.GONE);
        ivScannerTrigger.setVisibility(View.GONE);
    }

    @OnClick({R.id.iv_scanner_trigger, R.id.tv_filter_options, R.id.tv_tracked_data})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_scanner_trigger:
                isScannerTriggerOpen = !isScannerTriggerOpen;
                ivScannerTrigger.setImageResource(isScannerTriggerOpen ? R.drawable.ic_checked : R.drawable.ic_unchecked);
                clScannerTrigger.setVisibility(isScannerTriggerOpen ? View.VISIBLE : View.GONE);
                break;
            case R.id.tv_filter_options:
                if (activity.isUseNewFunction) {
                    startActivity(new Intent(getActivity(), FilterOptionsNewActivity.class));
                    return;
                }
                startActivity(new Intent(getActivity(), FilterOptionsActivity.class));
                break;
            case R.id.tv_tracked_data:
                startActivity(new Intent(getActivity(), ExportDataActivity.class));
                break;
        }
    }

    public boolean isValid() {
        if (isScannerTriggerOpen) {
            String scannerTriggerStr = etScannerTrigger.getText().toString();
            if (TextUtils.isEmpty(scannerTriggerStr))
                return false;
            int advTrigger = Integer.parseInt(scannerTriggerStr);
            if (advTrigger < 1 || advTrigger > 65535)
                return false;
        }
        return true;
    }


    public void saveParams() {
        final int storageIntervalProgress = sbStorageInterval.getProgress();
        final int trackNotify = npvTrackingNotify.getValue();
        final String scannerTriggerStr = etScannerTrigger.getText().toString();
        List<OrderTask> orderTasks = new ArrayList<>();

        orderTasks.add(OrderTaskAssembler.setStorageInterval(storageIntervalProgress));

        orderTasks.add(OrderTaskAssembler.setStoreAlert(trackNotify));
        if (activity.isUseNewFunction && trackNotify > 1) {
            final int vibrationsNumber = npvVibrationsNumber.getValue() + 1;
            orderTasks.add(OrderTaskAssembler.setVibrationNumber(vibrationsNumber));
        }

        if (isScannerTriggerOpen) {
            int scannerTrigger = Integer.parseInt(scannerTriggerStr);
            orderTasks.add(OrderTaskAssembler.setScannerMoveCondition(scannerTrigger));
        } else {
            orderTasks.add(OrderTaskAssembler.setScannerMoveCondition(0));
        }
        MokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
    }

    public void setStorageInterval(int time) {
        if (time <= 100)
            sbStorageInterval.setProgress(time);
    }

    public void setTrackNotify(int trackNotify) {
        if (trackNotify <= 3)
            npvTrackingNotify.setValue(trackNotify);
    }

    public void setVibrationsNumber(int vibrationsNumber) {
        final int trackNotify = npvTrackingNotify.getValue();
        if (trackNotify > 1) {
            rlVibrationsNumber.setVisibility(View.VISIBLE);
        }
        npvVibrationsNumber.setValue(vibrationsNumber - 1);
    }

    @Override
    public void onValueChange(NumberPickerView picker, int oldVal, int newVal) {
        if (activity.isUseNewFunction)
            rlVibrationsNumber.setVisibility(newVal > 1 ? View.VISIBLE : View.GONE);
    }
}
