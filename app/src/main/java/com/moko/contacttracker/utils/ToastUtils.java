package com.moko.contacttracker.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

import es.dmoral.toasty.Toasty;

/**
 * @Date 2020/4/18
 * @Author wenzheng.liu
 * @Description
 * @ClassPath com.moko.contacttracker.utils.ToastUtils
 */
final public class ToastUtils {

    public static void showToast(Context context, int tipID) {
        String tip = (String) context.getResources().getText(tipID);
        showToast(context, tip);
    }

    public static void showToast(Context context, String tip) {
        Toast toast = Toasty.normal(context, tip);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

}
