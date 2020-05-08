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
import com.moko.contacttracker.dialog.AlertMessageDialog;
import com.moko.contacttracker.dialog.ChangePasswordDialog;
import com.moko.contacttracker.dialog.ResetDialog;
import com.moko.contacttracker.dialog.TriggerSensitivityDialog;

import java.util.Timer;
import java.util.TimerTask;

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
                showChangePasswordDialog();
                break;
            case R.id.tv_factory_reset:
                showResetDialog();
                break;
            case R.id.tv_update_firmware:
                activity.chooseFirmwareFile();
                break;
            case R.id.tv_trigger_sensitivity:
                showTriggerSensitivityDialog();
                break;
            case R.id.iv_beacon_scanner:
                showBeaconScannerDialog();
                break;
            case R.id.iv_connectable:
                showConnectableDialog();
                break;
            case R.id.iv_button_power:
                showButtonPowerDialog();
                break;
            case R.id.iv_power_off:
                showPowerOffDialog();
                break;
        }
    }

    private void showTriggerSensitivityDialog() {
        final TriggerSensitivityDialog dialog = new TriggerSensitivityDialog(getActivity());
        dialog.setData(sensitivityStr);
        dialog.setOnSensitivityClicked(sensitivity -> activity.setSensitivity(sensitivity));
        dialog.show();
    }

    private void showResetDialog() {
        final ResetDialog dialog = new ResetDialog(getActivity());
        dialog.setOnPasswordClicked(password -> activity.reset(password));
        dialog.show();
        Timer resetTimer = new Timer();
        resetTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                activity.runOnUiThread(() -> dialog.showKeyboard());
            }
        }, 200);
    }

    private void showChangePasswordDialog() {
        final ChangePasswordDialog dialog = new ChangePasswordDialog(getActivity());
        dialog.setOnPasswordClicked(password -> activity.changePassword(password));
        dialog.show();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override

            public void run() {
                activity.runOnUiThread(() -> dialog.showKeyboard());
            }
        }, 200);
    }

    private void showBeaconScannerDialog() {
        AlertMessageDialog dialog = new AlertMessageDialog();
        dialog.setTitle("Warning!");
        if (scannerState) {
            dialog.setMessage("If you turn off Beacon Scanner function, the Beacon will stop scanning.");
        } else {
            dialog.setMessage("If you turn on Beacon Scanner function, the Beacon will start scanning.");
        }
        dialog.setConfirm("OK");
        dialog.setCancelGone();
        dialog.setOnAlertConfirmListener(() -> {
            int value = !scannerState ? 1 : 0;
            activity.changeScannerState(value);
        });
        dialog.show(activity.getSupportFragmentManager());
    }

    private void showConnectableDialog() {
        AlertMessageDialog dialog = new AlertMessageDialog();
        dialog.setTitle("Warning!");
        if (connectState) {
            dialog.setMessage("Are you sure to make the device Unconnectable？");
        } else {
            dialog.setMessage("Are you sure to make the device connectable？");
        }
        dialog.setConfirm("OK");
        dialog.setCancelGone();
        dialog.setOnAlertConfirmListener(() -> {
            int value = !connectState ? 1 : 0;
            activity.changeConnectState(value);
        });
        dialog.show(activity.getSupportFragmentManager());
    }

    private void showButtonPowerDialog() {
        AlertMessageDialog dialog = new AlertMessageDialog();
        dialog.setTitle("Warning!");
        if (buttonPowerState) {
            dialog.setMessage("If you turn off the Button Power function, you cannot turn off the beacon power with the button.");
        } else {
            dialog.setMessage("If you turn on the Button Power function, you can turn off the beacon power with the button.");
        }
        dialog.setConfirm("OK");
        dialog.setCancelGone();
        dialog.setOnAlertConfirmListener(() -> {
            int value = !buttonPowerState ? 1 : 0;
            activity.changeButtonPowerState(value);
        });
        dialog.show(activity.getSupportFragmentManager());
    }

    private void showPowerOffDialog() {
        AlertMessageDialog dialog = new AlertMessageDialog();
        dialog.setTitle("Warning!");
        dialog.setMessage("Are you sure to turn off the device? Please make sure the device has a button to turn on!");
        dialog.setConfirm("OK");
        dialog.setCancelGone();
        dialog.setOnAlertConfirmListener(() -> {
            activity.powerOff();
        });
        dialog.show(activity.getSupportFragmentManager());
    }

    private String sensitivityStr;

    public void setSensitivity(int sensitivity) {
        sensitivityStr = String.valueOf(sensitivity);
        int value = 248 - (sensitivity - 7);
        tvTriggerSensitivity.setText(getString(R.string.trigger_sensitivity, value));
    }

    private boolean scannerState;

    public void setBeaconScanner(int scanner) {
        scannerState = scanner == 1;
        ivBeaconScanner.setImageResource(scanner == 1 ? R.drawable.ic_checked : R.drawable.ic_unchecked);
    }

    private boolean connectState;

    public void setConnectable(int connectable) {
        connectState = connectable == 1;
        ivConnectable.setImageResource(connectable == 1 ? R.drawable.ic_checked : R.drawable.ic_unchecked);
    }

    private boolean buttonPowerState;

    public void setButtonPower(int enable) {
        buttonPowerState = enable == 1;
        ivButtonPower.setImageResource(enable == 1 ? R.drawable.ic_checked : R.drawable.ic_unchecked);
    }
}
