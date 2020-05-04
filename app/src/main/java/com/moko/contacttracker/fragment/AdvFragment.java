package com.moko.contacttracker.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.moko.contacttracker.entity.TxPowerEnum;

import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AdvFragment extends Fragment implements SeekBar.OnSeekBarChangeListener {
    private static final String TAG = AdvFragment.class.getSimpleName();
    public static final String UUID_PATTERN = "[A-Fa-f0-9]{8}-(?:[A-Fa-f0-9]{4}-){3}[A-Fa-f0-9]{12}";
    @Bind(R.id.et_adv_name)
    EditText etAdvName;
    @Bind(R.id.et_uuid)
    EditText etUuid;
    @Bind(R.id.et_major)
    EditText etMajor;
    @Bind(R.id.et_minor)
    EditText etMinor;
    @Bind(R.id.et_adv_interval)
    EditText etAdvInterval;
    @Bind(R.id.sb_rssi_1m)
    SeekBar sbRssi1m;
    @Bind(R.id.tv_rssi_1m_value)
    TextView tvRssi1mValue;
    @Bind(R.id.sb_tx_power)
    SeekBar sbTxPower;
    @Bind(R.id.tv_tx_power_value)
    TextView tvTxPowerValue;
    @Bind(R.id.iv_adv_trigger)
    ImageView ivAdvTrigger;
    @Bind(R.id.et_adv_trigger)
    EditText etAdvTrigger;
    @Bind(R.id.cl_adv_trigger)
    ConstraintLayout clAdvTrigger;


    private Pattern pattern;

    private DeviceInfoActivity activity;

    public AdvFragment() {
    }


    public static AdvFragment newInstance() {
        AdvFragment fragment = new AdvFragment();
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
        View view = inflater.inflate(R.layout.fragment_adv, container, false);
        ButterKnife.bind(this, view);
        activity = (DeviceInfoActivity) getActivity();
        sbRssi1m.setOnSeekBarChangeListener(this);
        sbTxPower.setOnSeekBarChangeListener(this);
        pattern = Pattern.compile(UUID_PATTERN);
        //限制只输入大写，自动小写转大写
//        etUuid.setTransformationMethod(new A2bigA());
        etUuid.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String input = s.toString().toUpperCase();
                if (!pattern.matcher(input).matches()) {
                    if (input.length() == 9 && !input.endsWith("-")) {
                        String show = input.substring(0, 8) + "-" + input.substring(8, input.length());
                        etUuid.setText(show);
                        etUuid.setSelection(show.length());
                    }
                    if (input.length() == 14 && !input.endsWith("-")) {
                        String show = input.substring(0, 13) + "-" + input.substring(13, input.length());
                        etUuid.setText(show);
                        etUuid.setSelection(show.length());
                    }
                    if (input.length() == 19 && !input.endsWith("-")) {
                        String show = input.substring(0, 18) + "-" + input.substring(18, input.length());
                        etUuid.setText(show);
                        etUuid.setSelection(show.length());
                    }
                    if (input.length() == 24 && !input.endsWith("-")) {
                        String show = input.substring(0, 23) + "-" + input.substring(23, input.length());
                        etUuid.setText(show);
                        etUuid.setSelection(show.length());
                    }
                }
            }
        });
//        setDefault();
        return view;
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.sb_rssi_1m:
                int rssi_1m = progress - 127;
                tvRssi1mValue.setText(String.format("%ddBm", rssi_1m));
                break;
            case R.id.sb_tx_power:
                TxPowerEnum txPowerEnum = TxPowerEnum.fromOrdinal(progress);
                if (txPowerEnum == null)
                    return;
                int txPower = txPowerEnum.getTxPower();
                tvTxPowerValue.setText(String.format("%ddBm", txPower));
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

    public void setDeviceName(String deviceName) {
        etAdvName.setText(deviceName);
        etAdvName.setSelection(deviceName.length());
    }

    public void setUUID(String uuid) {
        StringBuilder stringBuilder = new StringBuilder(uuid);
        stringBuilder.insert(8, "-");
        stringBuilder.insert(13, "-");
        stringBuilder.insert(18, "-");
        stringBuilder.insert(23, "-");
        etUuid.setText(stringBuilder.toString());
        int length = stringBuilder.toString().length();
        etUuid.setSelection(length);
    }

    public void setMajor(String major) {
        etMajor.setText(major);
        etMajor.setSelection(major.length());
    }

    public void setMinor(String minor) {
        etMinor.setText(minor);
        etMinor.setSelection(minor.length());
    }

    public void setAdvInterval(int advInterval) {
        etAdvInterval.setText(String.valueOf(advInterval));
        etAdvInterval.setSelection(String.valueOf(advInterval).length());
    }

    public void MeasurePower(int rssi_1m) {
        int progress = rssi_1m + 127;
        sbRssi1m.setProgress(progress);
        tvRssi1mValue.setText(String.format("%ddBm", rssi_1m));
    }

    public void setTransmission(int txPower) {
        int progress = TxPowerEnum.fromTxPower(txPower).ordinal();
        sbTxPower.setProgress(progress);
        tvTxPowerValue.setText(String.format("%ddBm", txPower));
    }

    private boolean isAdvTriggerOpen;

    public void setAdvTriggerClose() {
        isAdvTriggerOpen = false;
        ivAdvTrigger.setImageResource(R.drawable.ic_unchecked);
        clAdvTrigger.setVisibility(View.GONE);
    }

    public void setAdvTrigger(int duration) {
        isAdvTriggerOpen = true;
        ivAdvTrigger.setImageResource(R.drawable.ic_checked);
        clAdvTrigger.setVisibility(View.VISIBLE);
        etAdvTrigger.setText(String.valueOf(duration));
        etAdvTrigger.setSelection(String.valueOf(duration).length());
    }

    @OnClick({R.id.iv_adv_trigger})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_adv_trigger:
                isAdvTriggerOpen = !isAdvTriggerOpen;
                ivAdvTrigger.setImageResource(isAdvTriggerOpen ? R.drawable.ic_checked : R.drawable.ic_unchecked);
                clAdvTrigger.setVisibility(isAdvTriggerOpen ? View.VISIBLE : View.GONE);
                break;
        }
    }
}
