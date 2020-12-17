package com.moko.contacttracker.dialog;

import android.content.Context;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.moko.contacttracker.R;

import butterknife.BindView;
import butterknife.OnClick;

public class TriggerSensitivityDialog extends BaseDialog<String> implements SeekBar.OnSeekBarChangeListener {

    @BindView(R.id.sb_sensitivity)
    SeekBar sbSensitivity;
    @BindView(R.id.tv_sensitivity_value)
    TextView tvSensitivityValue;

    public TriggerSensitivityDialog(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.dialog_sensitivity;
    }

    @Override
    protected void renderConvertView(View convertView, String sensitivity) {
        int progress = Integer.parseInt(sensitivity) - 7;
        String value = String.valueOf(248 - progress);
        tvSensitivityValue.setText(value);
        sbSensitivity.setProgress(progress);
        sbSensitivity.setOnSeekBarChangeListener(this);
    }

    @OnClick({R.id.tv_cancel, R.id.tv_ensure})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel:
                dismiss();
                break;
            case R.id.tv_ensure:
                int progress = sbSensitivity.getProgress();
                int sensitivity = progress + 7;
                dismiss();
                if (sensitivityListener != null)
                    sensitivityListener.onEnsure(sensitivity);
                break;
        }
    }

    private SensitivityListener sensitivityListener;

    public void setOnSensitivityClicked(SensitivityListener sensitivityListener) {
        this.sensitivityListener = sensitivityListener;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        String value = String.valueOf(248 - progress);
        tvSensitivityValue.setText(value);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public interface SensitivityListener {

        void onEnsure(int sensitivity);
    }
}
