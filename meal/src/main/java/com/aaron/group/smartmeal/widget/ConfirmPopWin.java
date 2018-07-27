package com.aaron.group.smartmeal.widget;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aaron.group.smartmeal.R;
import com.aaron.group.smartmeal.listener.OnCancelListener;
import com.aaron.group.smartmeal.listener.OnSureListener;
import com.aaron.group.smartmeal.listener.PoponDismissListener;
import com.aaron.group.smartmeal.utils.CommonUtils;

/**
 * 说明: 确认弹出框

 */

public class ConfirmPopWin  extends PopupWindow {

    /**
     * 取消按钮点击事件
     */
    private OnCancelListener onCancelListener;
    /**
     * 确认按钮点击事件
     */
    private OnSureListener onSureListener;
    /**
     * context
     */
    private Context context;
    /**
     * activity
     */
    private Activity aty;

    public ConfirmPopWin(Context context, Activity aty)
    {
        this.context = context;
        this.aty = aty;
    }

    public void showUi(String msg)
    {
        View view = LayoutInflater.from ( context ).inflate (
                R.layout.layout_widget_confirm,
                null
        );

        TextView messagetext = (TextView) view.findViewById(R.id.messagetext);
        RelativeLayout btnLeftL = (RelativeLayout) view.findViewById(R.id.btnLeftL);
        RelativeLayout btnRightL = (RelativeLayout) view.findViewById(R.id.btnRightL);
        messagetext.setText(msg);
        btnRightL.setOnClickListener(new View.OnClickListener() {
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
        btnLeftL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null==onCancelListener)
                {
                    dismissView();
                }
                else
                {
                    onCancelListener.onCancel();
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
        //设置动画
        this.setAnimationStyle(R.style.animationPop01);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        CommonUtils.backgroundAlpha(aty, 0.4f);
    }

    public void setOnCancelListener(OnCancelListener onCancelListener) {
        this.onCancelListener = onCancelListener;
    }

    public void setOnSureListener(OnSureListener onSureListener) {
        this.onSureListener = onSureListener;
    }

    public void dismissView()
    {
        setOnDismissListener ( new PoponDismissListener( aty ) );
        dismiss ();
    }
}