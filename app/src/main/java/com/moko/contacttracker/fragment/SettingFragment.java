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
import com.moko.contacttracker.dialog.ScanWindowDialog;
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
    @Bind(R.id.tv_scan_window)
    TextView tvScanWindow;
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
            R.id.tv_trigger_sensitivity, R.id.tv_scan_window, R.id.iv_connectable,
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
            case R.id.tv_scan_window:
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
        final ScanWindowDialog dialog = new ScanWindowDialog(getActivity());
        dialog.setData(scannerState ? startTime : 0);
        dialog.setOnScanWindowClicked(scanMode -> {
            String scanModeStr = "";
            switch (scanMode) {
                case 4:
                    scanModeStr = "0ms/1000ms";
                    break;
                case 0:
                    scanModeStr = "1000ms/1000ms";
                    break;
                case 1:
                    scanModeStr = "500ms/1000ms";
                    break;
                case 2:
                    scanModeStr = "250ms/1000ms";
                    break;
                case 3:
                    scanModeStr = "125ms/1000ms";
                    break;
            }
            tvScanWindow.setText(String.format("Scan Window(%s)", scanModeStr));
            if (scanMode < 4) {
                scanMode += 1;
                activity.changeScannerState(1, scanMode);
            } else {
                activity.changeScannerState(0);
            }
        });
        dialog.show();
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
    private int startTime;

    public void setBeaconScanner(int scanner) {
        scannerState = scanner == 1;
        if (!scannerState) {
            tvScanWindow.setText("Scan Window(0ms/1000ms)");
        }
    }

    public void setScanStartTime(int startTime) {
        this.startTime = startTime;
        String scanModeStr = "";
        switch (startTime) {
            case 1:
                scanModeStr = "1000ms/1000ms";
                break;
            case 2:
                scanModeStr = "500ms/1000ms";
                break;
            case 3:
                scanModeStr = "250ms/1000ms";
                break;
            case 4:
                scanModeStr = "125ms/1000ms";
                break;
        }
        tvScanWindow.setText(scannerState ? String.format("Scan Window(%s)", scanModeStr)
                : "Scan Window(0ms/1000ms)");
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
