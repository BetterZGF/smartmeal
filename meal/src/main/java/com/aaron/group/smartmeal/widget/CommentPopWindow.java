package com.aaron.group.smartmeal.widget;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.TextView;

import com.aaron.group.smartmeal.R;
import com.aaron.group.smartmeal.bean.OrderBean;
import com.aaron.group.smartmeal.eventbus.EventBusBundleMsg;
import com.aaron.group.smartmeal.eventbus.EventBusMsg;

import org.greenrobot.eventbus.EventBus;

/**
 * 说明:

 */

public class CommentPopWindow extends PopupWindow {

    /**
     * Context
     */
    private Context context;

    /**
     * eventbus实例，用以消息交互
     */
    private EventBus eventBus;
    /**
     * Activity
     */
    private Activity aty;

    public
    CommentPopWindow ( Context context, Activity aty, EventBus eventBus) {
        this.context = context;
        this.aty = aty;
        this.eventBus = eventBus;
    }

    public
    void showComment (final OrderBean order) {
        /**
         * 实例化下单界面组件UI布局
         */
        View view = LayoutInflater.from ( context ).inflate (
                R.layout.layout_widget_comment,
                null
        );
        // 获取具体UI组件
        final RatingBar commentRating = (RatingBar) view.findViewById(R.id.commentRating);
        final EditText comment = (EditText) view.findViewById(R.id.comment);
        TextView login = (TextView) view.findViewById(R.id.login);
        TextView cancel = (TextView) view.findViewById(R.id.cancel);
        commentRating.setRating(5f);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissView();
                //提交评论
                if(null!=order)
                {
                    order.rating = commentRating.getRating();
                    order.comment = comment.getText().toString();
                    order.save();
                    eventBus.post(new EventBusMsg(EventBusMsg.EventBusType.INFO_COMMENT_SUCCESS));
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissView();
                eventBus.post(new EventBusMsg(EventBusMsg.EventBusType.INFO_COMMENT_CANCEL));
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
        //设置窗口弹出动画
        this.setAnimationStyle(R.style.animationPop01);
    }

    public void dismissView()
    {
        dismiss ();
    }
}
