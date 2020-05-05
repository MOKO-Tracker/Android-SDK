package com.moko.contacttracker.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.moko.contacttracker.R;
import com.moko.contacttracker.activity.DeviceInfoActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingFragment extends Fragment {
    private static final String TAG = SettingFragment.class.getSimpleName();
    @Bind(R.id.tv_change_password)
    TextView tvChangePassword;
    @Bind(R.id.tv_factory_reset)
    TextView tvFactoryReset;
    @Bind(R.id.tv_update_firmware)
    TextView tvUpdateFirmware;
    @Bind(R.id.tv_trigger_sensitivity)
    TextView tvTriggerSensitivity;
    @Bind(R.id.iv_beacon_scanner)
    ImageView ivBeaconScanner;
    @Bind(R.id.iv_connectable)
    ImageView ivConnectable;
    @Bind(R.id.iv_button_power)
    ImageView ivButtonPower;
    @Bind(R.id.iv_power_off)
    ImageView ivPowerOff;


    private DeviceInfoActivity activity;

    public SettingFragment() {
    }


    public static SettingFragment newInstance() {
        SettingFragment fragment = new SettingFragment();
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
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        ButterKnife.bind(this, view);
        activity = (DeviceInfoActivity) getActivity();
        return view;
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

    @OnClick({R.id.tv_change_password, R.id.tv_factory_reset, R.id.tv_update_firmware,
            R.id.tv_trigger_sensitivity, R.id.iv_beacon_scanner, R.id.iv_connectable,
            R.id.iv_button_power, R.id.iv_power_off})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_change_password:
                break;
            case R.id.tv_factory_reset:
                break;
            case R.id.tv_update_firmware:
                break;
            case R.id.tv_trigger_sensitivity:
                break;
            case R.id.iv_beacon_scanner:
                break;
            case R.id.iv_connectable:
                break;
            case R.id.iv_button_power:
                break;
            case R.id.iv_power_off:
                break;
        }
    }

    public void setSensitivity(int sensitivity) {
        tvTriggerSensitivity.setText(getString(R.string.trigger_sensitivity, sensitivity));
    }

    public void setBeaconScanner(int scanner) {
        ivBeaconScanner.setImageResource(scanner == 1 ? R.drawable.ic_checked : R.drawable.ic_unchecked);
    }

    public void setConnectable(int connectable) {
        ivConnectable.setImageResource(connectable == 1 ? R.drawable.ic_checked : R.drawable.ic_unchecked);
    }

    public void setButtonPower(int enable) {
        ivButtonPower.setImageResource(enable == 1 ? R.drawable.ic_checked : R.drawable.ic_unchecked);
    }
}
