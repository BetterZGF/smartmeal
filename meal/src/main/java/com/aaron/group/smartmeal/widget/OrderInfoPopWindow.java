package com.aaron.group.smartmeal.widget;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aaron.group.smartmeal.R;
import com.aaron.group.smartmeal.bean.DishesBean;
import com.aaron.group.smartmeal.eventbus.EventBusMsg;
import com.aaron.group.smartmeal.listener.PoponDismissListener;
import com.aaron.group.smartmeal.utils.CommonUtils;
import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * 说明: 菜品分类界面查看菜品详情

 */

public class OrderInfoPopWindow extends PopupWindow {

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

    public OrderInfoPopWindow(Context context, Activity aty, EventBus eventBus) {
        this.context = context;
        this.aty = aty;
        this.eventBus = eventBus;
    }

    public void showOrder(int dishesId) {
        //加载菜品数据
        final DishesBean dishes = loadDishes(dishesId);
        if (null != dishes) {
            /**
             * 实例化下单界面组件UI布局
             */
            View view = LayoutInflater.from(context).inflate(
                    R.layout.layout_widget_orderinfo,
                    null
            );
            // 获取具体UI组件
            ImageView dishesImg = (ImageView) view.findViewById(R.id.dishesImg);
            TextView dishesName = (TextView) view.findViewById(R.id.dishesName);
            TextView dishesPrice = (TextView) view.findViewById(R.id.dishesPrice);
            TextView dishesDesc = (TextView) view.findViewById(R.id.dishesDesc);
            RelativeLayout closeBtn = (RelativeLayout) view.findViewById(R.id.closeBtn);
            Glide.with(context).load(dishes.dishesImg).dontAnimate().centerCrop().placeholder(R.mipmap.ic_launcher).into(dishesImg);
            dishesName.setText(dishes.dishesName);
            dishesPrice.setText(dishes.dishesPrice + context.getResources().getString(R.string.money_unit));
            dishesDesc.setText(dishes.dishesDesc);
            closeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismissView();
                    eventBus.post(new EventBusMsg(EventBusMsg.EventBusType.POP_ORDERINFO_CANCEL));
                }
            });
            // 设置SelectPicPopupWindow的View
            this.setContentView(view);
            // 设置SelectPicPopupWindow弹出窗体的宽
            this.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
            // 设置SelectPicPopupWindow弹出窗体的高
            this.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
            // 设置SelectPicPopupWindow弹出窗体可点击
            this.setFocusable(true);
            //设置窗口弹出动画
            this.setAnimationStyle(R.style.animationPop01);
            CommonUtils.backgroundAlpha(aty, 0.4f);
        } else {
            eventBus.post(new EventBusMsg(EventBusMsg.EventBusType.POP_ORDER_NODATA));
        }
    }

    private DishesBean loadDishes(int dishesId) {
        List<DishesBean> dishesList = DishesBean.find(DishesBean.class, "dishesId = ? and dishesStatus = ?", String.valueOf(dishesId), "0");
        if (null != dishesList && !dishesList.isEmpty()) {
            return dishesList.get(0);
        } else {
            return null;
        }
    }

    public void dismissView() {
        setOnDismissListener(new PoponDismissListener(aty));
        dismiss();
    }
}