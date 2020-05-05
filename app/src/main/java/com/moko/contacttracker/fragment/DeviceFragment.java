package com.moko.contacttracker.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.moko.contacttracker.R;
import com.moko.contacttracker.activity.DeviceInfoActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DeviceFragment extends Fragment {
    private static final String TAG = DeviceFragment.class.getSimpleName();
    @Bind(R.id.tv_battery_voltage)
    TextView tvBatteryVoltage;
    @Bind(R.id.tv_mac_address)
    TextView tvMacAddress;
    @Bind(R.id.tv_product_model)
    TextView tvProductModel;
    @Bind(R.id.tv_software_version)
    TextView tvSoftwareVersion;
    @Bind(R.id.tv_firmware_version)
    TextView tvFirmwareVersion;
    @Bind(R.id.tv_hardware_version)
    TextView tvHardwareVersion;
    @Bind(R.id.tv_manufacture_date)
    TextView tvManufactureDate;
    @Bind(R.id.tv_manufacture)
    TextView tvManufacture;


    private DeviceInfoActivity activity;

    public DeviceFragment() {
    }


    public static DeviceFragment newInstance() {
        DeviceFragment fragment = new DeviceFragment();
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
        View view = inflater.inflate(R.layout.fragment_device, container, false);
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

    public void setBatteryValtage(int battery) {
        tvBatteryVoltage.setText(String.format("%dmV", battery));
    }

    public void setMacAddress(String macAddress) {
        tvMacAddress.setText(macAddress);
    }

    public void setProductModel(String productModel) {
        tvProductModel.setText(productModel);
    }

    public void setSoftwareVersion(String softwareVersion) {
        tvSoftwareVersion.setText(softwareVersion);
    }

    public void setFirmwareVersion(String firmwareVersion) {
        tvFirmwareVersion.setText(firmwareVersion);
    }

    public void setHardwareVersion(String hardwareVersion) {
        tvHardwareVersion.setText(hardwareVersion);
    }

    public void setManufactureDate(String manufactureDate) {
        tvManufactureDate.setText(manufactureDate);
    }

    public void setManufacture(String manufacture) {
        tvManufacture.setText(manufacture);
    }
}
