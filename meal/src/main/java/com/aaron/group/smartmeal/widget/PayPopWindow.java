package com.aaron.group.smartmeal.widget;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aaron.group.smartmeal.R;
import com.aaron.group.smartmeal.eventbus.EventBusBundleMsg;
import com.aaron.group.smartmeal.eventbus.EventBusMsg;
import com.aaron.group.smartmeal.listener.OnCancelListener;
import com.aaron.group.smartmeal.listener.OnSureListener;
import com.aaron.group.smartmeal.listener.PoponDismissListener;
import com.aaron.group.smartmeal.utils.CommonUtils;

import org.greenrobot.eventbus.EventBus;

/**
 * 说明:

 */

public class PayPopWindow extends PopupWindow {

    /**
     * 取消按钮点击事件
     */
    private OnCancelListener onCancelListener;
    /**
     * context
     */
    private Context context;
    /**
     * activity
     */
    private Activity aty;

    private EventBus eventBus;

    public PayPopWindow(Context context, Activity aty, EventBus eventBus)
    {
        this.context = context;
        this.aty = aty;
        this.eventBus = eventBus;
    }

    public void showPay(final float AmountMoney, final float currentBalance)
    {
        View view = LayoutInflater.from ( context ).inflate (
                R.layout.layout_widget_pay,
                null
        );

        TextView payAmount = (TextView) view.findViewById(R.id.payAmount);
        payAmount.setText("您需要支付"+AmountMoney+"元");
        TextView currentAmount = (TextView) view.findViewById(R.id.currentAmount);
        currentAmount.setText("当前您的余额："+currentBalance+"元");
        final ImageView balanceChoice = (ImageView) view.findViewById(R.id.balanceChoice);
        balanceChoice.setTag(1);
        CommonUtils.loadBackground(balanceChoice, context.getResources().getDrawable(R.mipmap.diningtable_choice));
        final ImageView alipayChoice = (ImageView) view.findViewById(R.id.alipayChoice);
        alipayChoice.setTag(0);
        final ImageView wechatChoice = (ImageView) view.findViewById(R.id.wechatChoice);
        wechatChoice.setTag(0);
        RelativeLayout balanceL = (RelativeLayout) view.findViewById(R.id.balanceL);
        balanceL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if("0".equals(String.valueOf(balanceChoice.getTag())))
                {
                    //选中余额支付
                    balanceChoice.setTag(1);
                    CommonUtils.loadBackground(balanceChoice, context.getResources().getDrawable(R.mipmap.diningtable_choice));
                    //复位余额支付
                    alipayChoice.setTag(0);
                    CommonUtils.loadBackground(alipayChoice, context.getResources().getDrawable(R.mipmap.unchoice));
                    //复位微信支付
                    wechatChoice.setTag(0);
                    CommonUtils.loadBackground(wechatChoice, context.getResources().getDrawable(R.mipmap.unchoice));
                }
                else if("1".equals(String.valueOf(balanceChoice.getTag())))
                {
                    //复位余额支付
                    balanceChoice.setTag(0);
                    CommonUtils.loadBackground(balanceChoice, context.getResources().getDrawable(R.mipmap.unchoice));
                }
            }
        });
        RelativeLayout alipayL = (RelativeLayout) view.findViewById(R.id.alipayL);
        alipayL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if("0".equals(String.valueOf(alipayChoice.getTag())))
                {
                    //复位余额支付
                    balanceChoice.setTag(0);
                    CommonUtils.loadBackground(balanceChoice, context.getResources().getDrawable(R.mipmap.unchoice));
                    //选中余额支付
                    alipayChoice.setTag(1);
                    CommonUtils.loadBackground(alipayChoice, context.getResources().getDrawable(R.mipmap.diningtable_choice));
                    //复位微信支付
                    wechatChoice.setTag(0);
                    CommonUtils.loadBackground(wechatChoice, context.getResources().getDrawable(R.mipmap.unchoice));
                }
                else if("1".equals(String.valueOf(alipayChoice.getTag())))
                {
                    //复位支付宝支付
                    alipayChoice.setTag(0);
                    CommonUtils.loadBackground(alipayChoice, context.getResources().getDrawable(R.mipmap.unchoice));
                }
            }
        });
        RelativeLayout wechatL = (RelativeLayout) view.findViewById(R.id.wechatL);
        wechatL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if("0".equals(String.valueOf(wechatChoice.getTag())))
                {
                    //复位余额支付
                    balanceChoice.setTag(0);
                    CommonUtils.loadBackground(balanceChoice, context.getResources().getDrawable(R.mipmap.unchoice));
                    //选中余额支付
                    alipayChoice.setTag(0);
                    CommonUtils.loadBackground(alipayChoice, context.getResources().getDrawable(R.mipmap.unchoice));
                    //复位微信支付
                    wechatChoice.setTag(1);
                    CommonUtils.loadBackground(wechatChoice, context.getResources().getDrawable(R.mipmap.diningtable_choice));
                }
                else if("1".equals(String.valueOf(alipayChoice.getTag())))
                {
                    //复位支付宝支付
                    wechatChoice.setTag(0);
                    CommonUtils.loadBackground(wechatChoice, context.getResources().getDrawable(R.mipmap.unchoice));
                }
            }
        });
        RelativeLayout btnLeftL = (RelativeLayout) view.findViewById(R.id.btnLeftL);
        RelativeLayout btnRightL = (RelativeLayout) view.findViewById(R.id.btnRightL);
        btnRightL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if("1".equals(String.valueOf(balanceChoice.getTag())))
                {
                    //余额支付
                    if(currentBalance<AmountMoney)
                    {
                        eventBus.post(new EventBusMsg(EventBusMsg.EventBusType.PAY_BALANCE_ERROR));
                    }
                    else
                    {
                        Bundle bundle = new Bundle();
                        bundle.putInt("payType", 0);
                        bundle.putString("payAmount", String.valueOf(AmountMoney));
                        eventBus.post(new EventBusBundleMsg(EventBusBundleMsg.EventBusType.BUNDLE_PAY, bundle));
                        dismissView();
                    }
                }
                else if("1".equals(String.valueOf(wechatChoice.getTag())))
                {
                    //微信支付
                    Bundle bundle = new Bundle();
                    bundle.putInt("payType", 1);
                    bundle.putString("payAmount", String.valueOf(AmountMoney));
                    eventBus.post(new EventBusBundleMsg(EventBusBundleMsg.EventBusType.BUNDLE_PAY, bundle));
                    dismissView();
                }
                else if("1".equals(String.valueOf(alipayChoice.getTag())))
                {
                    //支付宝支付
                    Bundle bundle = new Bundle();
                    bundle.putInt("payType", 2);
                    bundle.putString("payAmount", String.valueOf(AmountMoney));
                    eventBus.post(new EventBusBundleMsg(EventBusBundleMsg.EventBusType.BUNDLE_PAY, bundle));
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
    }

    public void setOnCancelListener(OnCancelListener onCancelListener) {
        this.onCancelListener = onCancelListener;
    }

    public void dismissView()
    {
        dismiss ();
    }
}
