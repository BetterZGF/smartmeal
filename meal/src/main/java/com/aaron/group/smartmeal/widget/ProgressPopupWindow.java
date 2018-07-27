package com.aaron.group.smartmeal.widget;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.aaron.group.smartmeal.R;
import com.aaron.group.smartmeal.listener.PoponDismissListener;
import com.aaron.group.smartmeal.utils.CommonUtils;
import com.github.ybq.android.spinkit.SpinKitView;
import com.github.ybq.android.spinkit.SpriteFactory;
import com.github.ybq.android.spinkit.Style;
import com.github.ybq.android.spinkit.sprite.Sprite;

/**
 * 说明: 系统自定义进度条

 */

public class ProgressPopupWindow extends PopupWindow {

    /**
     * Context
     */
    private Context context;
    /**
     * Activity
     */
    private Activity aty;
    /**
     * SpinKitView组件，加载进度条动画
     */
    private SpinKitView spinKitView;

    public
    ProgressPopupWindow ( Context context, Activity aty) {
        this.context = context;
        this.aty = aty;
    }

    public
    void showProgress ( Style style) {
        /**
         * 实例化进度条组件UI布局
         */
        View view = LayoutInflater.from ( context ).inflate (
                R.layout.layout_widget_loadanim,
                null
        );
        // 获取具体UI组件
        spinKitView = (SpinKitView) view.findViewById ( R.id.spinKit );
        spinKitView.setColor(context.getResources().getColor(R.color.colorPrimary));
        Sprite drawable = SpriteFactory.create((null==style)?Style.WAVE:style);
        spinKitView.setIndeterminateDrawable(drawable);

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

    public void dismissView()
    {
        if(null!=spinKitView)
        {
            spinKitView.clearAnimation();
        }
        setOnDismissListener ( new PoponDismissListener( aty ) );
        dismiss ();
    }
}
