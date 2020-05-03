package com.moko.contacttracker.adapter;

import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.moko.contacttracker.R;
import com.moko.contacttracker.entity.BeaconInfo;

public class BeaconListAdapter extends BaseQuickAdapter<BeaconInfo, BaseViewHolder> {
    public BeaconListAdapter() {
        super(R.layout.list_item_device);
    }

    @Override
    protected void convert(BaseViewHolder helper, BeaconInfo item) {
        final String rssi = String.format("%ddBm", item.rssi);
        helper.setText(R.id.tv_rssi, rssi);
        final String name = TextUtils.isEmpty(item.name) ? "N/A" : item.name;
        helper.setText(R.id.tv_name, name);
        helper.setText(R.id.tv_mac, String.format("MAC:%s", item.mac));
        helper.setText(R.id.tv_battery, String.format("%dmV", item.battery));
        final String txPower = String.format("Tx Power:%ddBm", item.txPower);
        helper.setText(R.id.tv_tx_power, txPower);
        helper.setVisible(R.id.tv_connect, item.connectable == 1);
        final String track = String.format("Track:%s", item.track == 0 ? "OFF" : "ON");
        helper.setText(R.id.tv_track, track);
        final String intervalTime = item.intervalTime == 0 ? "<->N/A" : String.format("<->%dms", item.intervalTime);
        helper.setText(R.id.tv_track_interval, intervalTime);
        final String uuid = TextUtils.isEmpty(item.uuid) ? "N/A" : item.uuid;
        helper.setText(R.id.tv_uuid, uuid);
        final String major = TextUtils.isEmpty(item.major) ? "N/A" : item.major;
        helper.setText(R.id.tv_major, major);
        final String minor = TextUtils.isEmpty(item.minor) ? "N/A" : item.minor;
        helper.setText(R.id.tv_minor, minor);
        final String rssi_1m = String.format("%ddBm", item.rssi_1m);
        helper.setText(R.id.tv_rssi_1m, rssi_1m);
        final String proximity = TextUtils.isEmpty(item.proximity) ? "N/A" : item.proximity;
        helper.setText(R.id.tv_proximity, proximity);
        helper.addOnClickListener(R.id.tv_connect);
    }
}
