package com.aaron.group.smartmeal.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aaron.group.smartmeal.R;
import com.aaron.group.smartmeal.bean.DishesBean;
import com.aaron.group.smartmeal.bean.OrderBean;
import com.aaron.group.smartmeal.listener.OnItemClickListener;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;



public class OrderAdapter extends BaseQuickAdapter<OrderBean, OrderAdapter.OrderViewHolder> {

    private Context context;
    private OnItemClickListener mOnItemClickListener;

    public OrderAdapter(Context context, List<OrderBean> datas)
    {
        super(datas);
        this.context = context;
    }

    @Override
    protected OrderAdapter.OrderViewHolder createBaseViewHolder(ViewGroup parent, int layoutResId) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_orders, parent, false);
        return new OrderAdapter.OrderViewHolder(view);
    }

    public void setmOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    @Override
    protected void convert(final OrderViewHolder helper, OrderBean item) {
        if(null!=item)
        {
            helper.orderId.setText("订单编号："+item.orderId);
            helper.orderTime.setText("下单时间："+item.orderTime);
            if(0==item.orderStatus)
            {
                helper.orderStatus.setText("未结算");
                helper.orderStatus.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
            }
            else if(1==item.orderStatus)
            {
                helper.orderStatus.setText("已结算");
                helper.orderStatus.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
            }
            helper.peoples.setText("用餐人数："+item.peoples+"人");
            if(null!=item.orderPrice)
            {
                helper.orderAmount.setText(item.orderPrice);
            }
            else
            {
                helper.orderAmount.setVisibility(View.GONE);
            }
            if(null!=item.payTime)
            {
                helper.payTime.setText("付款时间："+item.payTime);
            }
            else
            {
                helper.payTime.setVisibility(View.GONE);
            }
            if(null!=mOnItemClickListener)
            {
                helper.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnItemClickListener.onItemClick(v, helper.getLayoutPosition());
                    }
                });
            }
        }
    }

    public static class OrderViewHolder extends BaseViewHolder
    {
        @Bind(R.id.orderId)
        TextView orderId;
        @Bind(R.id.orderTime)
        TextView orderTime;
        @Bind(R.id.orderStatus)
        TextView orderStatus;
        @Bind(R.id.peoples)
        TextView peoples;
        @Bind(R.id.orderAmount)
        TextView orderAmount;
        @Bind(R.id.payTime)
        TextView payTime;

        public OrderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
