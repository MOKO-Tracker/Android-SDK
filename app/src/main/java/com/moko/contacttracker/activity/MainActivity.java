package com.moko.contacttracker.activity;

import android.os.Bundle;
import android.view.animation.Animation;

import com.moko.contacttracker.service.MokoService;

/**
 * @Date 2020/4/18
 * @Author wenzheng.liu
 * @Description 
 * @ClassPath com.moko.contacttracker.activity.MainActivity
 */
public class MainActivity extends BaseActivity {

    private Animation animation = null;
    private MokoService mMokoService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
