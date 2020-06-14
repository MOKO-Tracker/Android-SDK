package com.moko.contacttracker.dialog;

import android.content.Context;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.moko.contacttracker.R;

import butterknife.Bind;
import butterknife.OnClick;

public class ScanWindowDialog extends BaseDialog<Integer> implements SeekBar.OnSeekBarChangeListener {

    @Bind(R.id.sb_scan_window)
    SeekBar sbScanWindow;
    @Bind(R.id.tv_scan_window_value)
    TextView tvScanWindowValue;

    public ScanWindowDialog(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.dialog_scan_window;
    }

    @Override
    protected void renderConvertView(View convertView, Integer progress) {
        switch (progress) {
            case 0:
                tvScanWindowValue.setText("0ms/1000ms");
                break;
            case 1:
                tvScanWindowValue.setText("1000ms/1000ms");
                break;
            case 2:
                tvScanWindowValue.setText("500ms/1000ms");
                break;
            case 3:
                tvScanWindowValue.setText("250ms/1000ms");
                break;
            case 4:
                tvScanWindowValue.setText("125ms/1000ms");
                break;
        }
        if (progress > 0) {
            progress -= 1;
        } else {
            progress = 4;
        }
        sbScanWindow.setProgress(progress);
        sbScanWindow.setOnSeekBarChangeListener(this);
    }

    @OnClick({R.id.tv_cancel, R.id.tv_ensure})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel:
                dismiss();
                break;
            case R.id.tv_ensure:
                int progress = sbScanWindow.getProgress();
                dismiss();
                if (scanWindowListener != null)
                    scanWindowListener.onEnsure(progress);
                break;
        }
    }

    private ScanWindowListener scanWindowListener;

    public void setOnScanWindowClicked(ScanWindowListener scanWindowListener) {
        this.scanWindowListener = scanWindowListener;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (progress) {
            case 4:
                tvScanWindowValue.setText("0ms/1000ms");
                break;
            case 0:
                tvScanWindowValue.setText("1000ms/1000ms");
                break;
            case 1:
                tvScanWindowValue.setText("500ms/1000ms");
                break;
            case 2:
                tvScanWindowValue.setText("250ms/1000ms");
                break;
            case 3:
                tvScanWindowValue.setText("125ms/1000ms");
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public interface ScanWindowListener {

        void onEnsure(int scanMode);
    }
}
