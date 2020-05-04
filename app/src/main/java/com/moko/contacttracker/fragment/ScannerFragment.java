package com.moko.contacttracker.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.moko.contacttracker.R;
import com.moko.contacttracker.activity.DeviceInfoActivity;
import com.moko.contacttracker.service.MokoService;
import com.moko.support.MokoSupport;
import com.moko.support.task.OrderTask;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.carbswang.android.numberpickerview.library.NumberPickerView;

public class ScannerFragment extends Fragment implements SeekBar.OnSeekBarChangeListener {
    private static final String TAG = ScannerFragment.class.getSimpleName();
    @Bind(R.id.sb_storage_interval)
    SeekBar sbStorageInterval;
    @Bind(R.id.tv_storage_interval_value)
    TextView tvStorageIntervalValue;
    @Bind(R.id.tv_storage_interval_tips)
    TextView tvStorageIntervalTips;
    @Bind(R.id.npv_tracking_notify)
    NumberPickerView npvTrackingNotify;
    @Bind(R.id.iv_scanner_trigger)
    ImageView ivScannerTrigger;
    @Bind(R.id.et_scanner_trigger)
    EditText etScannerTrigger;
    @Bind(R.id.cl_scanner_trigger)
    ConstraintLayout clScannerTrigger;

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
        npvTrackingNotify.setValue(0);
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
    public void onDestroyView() {
        Log.i(TAG, "onDestroyView: ");
        super.onDestroyView();
        ButterKnife.unbind(this);
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

    @OnClick({R.id.iv_scanner_trigger, R.id.tv_filter_options, R.id.tv_tracked_data})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_scanner_trigger:
                isScannerTriggerOpen = !isScannerTriggerOpen;
                ivScannerTrigger.setImageResource(isScannerTriggerOpen ? R.drawable.ic_checked : R.drawable.ic_unchecked);
                clScannerTrigger.setVisibility(isScannerTriggerOpen ? View.VISIBLE : View.GONE);
                break;
            case R.id.tv_filter_options:
                // TODO: 2020/5/4 跳转过滤条件
                break;
            case R.id.tv_tracked_data:
                // TODO: 2020/5/4 跳转追踪数据
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


    public void saveParams(MokoService mokoService) {
        final int storageIntervalProgress = sbStorageInterval.getProgress();
        final int trackNotify = npvTrackingNotify.getValue();
        final String scannerTriggerStr = etScannerTrigger.getText().toString();
        List<OrderTask> orderTasks = new ArrayList<>();

        orderTasks.add(mokoService.setStorageInterval(storageIntervalProgress));

        orderTasks.add(mokoService.setStoreAlert(trackNotify));

        if (isScannerTriggerOpen) {
            int scannerTrigger = Integer.parseInt(scannerTriggerStr);
            orderTasks.add(mokoService.setScannerMoveCondition(scannerTrigger));
        } else {
            orderTasks.add(mokoService.setScannerMoveCondition(0));
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
}