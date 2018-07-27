package com.aaron.group.smartmeal.widget;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aaron.group.smartmeal.R;
import com.aaron.group.smartmeal.listener.OnSureListener;
import com.aaron.group.smartmeal.listener.PoponDismissListener;
import com.aaron.group.smartmeal.utils.CommonUtils;

/**
 * 说明: 重要消息提示框

 */

public class MsgPopWindow extends PopupWindow {

    /**
     * Context
     */
    private Context context;
    /**
     * Activity
     */
    private Activity aty;
    /**
     * 确认按钮点击事件
     */
    private OnSureListener onSureListener;

    public void setOnSureListener(OnSureListener onSureListener) {
        this.onSureListener = onSureListener;
    }

    public
    MsgPopWindow ( Context context, Activity aty) {
        this.context = context;
        this.aty = aty;
    }

    public
    void showMessage ( String message) {
        if(!TextUtils.isEmpty(message))
        {
            /**
             * 实例化进度条组件UI布局
             */
            View view = LayoutInflater.from ( context ).inflate (
                    R.layout.layout_widget_message,
                    null
            );
            // 获取具体UI组件
            TextView messagetext = (TextView) view.findViewById(R.id.messagetext);
            RelativeLayout btnsureL = (RelativeLayout) view.findViewById(R.id.btnsureL);
            messagetext.setText(message);
            btnsureL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(null==onSureListener)
                    {
                        dismissView();
                    }
                    else
                    {
                        onSureListener.onSure();
                        dismissView();
                    }
                }
            });
            // 设置SelectPicPopupWindow的View
            this.setContentView ( view );
            // 设置SelectPicPopupWindow弹出窗体的宽
            this.setWidth (WindowManager.LayoutParams.MATCH_PARENT);
            // 设置SelectPicPopupWindow弹出窗体的高
            this.setHeight (WindowManager.LayoutParams.MATCH_PARENT);
            // 设置SelectPicPopupWindow弹出窗体可点击
            this.setFocusable(true);
            CommonUtils.backgroundAlpha(aty, 0.4f);
        }
    }

    public void dismissView()
    {
        setOnDismissListener ( new PoponDismissListener( aty ) );
        dismiss ();
    }
}
