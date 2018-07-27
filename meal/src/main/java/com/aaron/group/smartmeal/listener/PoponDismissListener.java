package com.aaron.group.smartmeal.listener;

import android.app.Activity;
import android.widget.PopupWindow;

import com.aaron.group.smartmeal.utils.CommonUtils;

/**
 * 说明: 弹出框关闭后调用，调整背景的透明度

 */

public class PoponDismissListener implements PopupWindow.OnDismissListener {

    private Activity aty;
    public
    PoponDismissListener ( Activity aty )
    {
        this.aty = aty;
    }
    @Override
    public
    void onDismiss ( )
    {
        CommonUtils.backgroundAlpha(aty, 1.0f);

    }
}