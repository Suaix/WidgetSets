package com.summerxia.widgetlibray.utils;

import android.content.Context;

/**
 * Created by SummerXia on 2016/7/15.
 */
public class CommonUtils {

    public static float dip2Px(Context context, float dip) {
        return context.getResources().getDisplayMetrics().density * dip;
    }

    public static float px2Dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (pxValue / scale + 0.5f);
    }

}
