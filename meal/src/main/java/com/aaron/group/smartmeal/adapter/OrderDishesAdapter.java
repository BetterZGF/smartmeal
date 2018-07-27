package com.aaron.group.smartmeal.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aaron.group.smartmeal.R;
import com.aaron.group.smartmeal.bean.DishesBean;
import com.aaron.group.smartmeal.bean.OrderedDishes;
import com.aaron.group.smartmeal.listener.OnItemClickListener;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;




public class OrderDishesAdapter extends BaseQuickAdapter<OrderedDishes, OrderDishesAdapter.OrderDishesViewHolder> {

    private Context context;

    public OrderDishesAdapter(Context context, List<OrderedDishes> datas)
    {
        super(datas);
        this.context = context;
    }

    @Override
    protected OrderDishesAdapter.OrderDishesViewHolder createBaseViewHolder(ViewGroup parent, int layoutResId) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_home_order_dishes, parent, false);
        return new OrderDishesAdapter.OrderDishesViewHolder(view);
    }

    @Override
    protected void convert(OrderDishesViewHolder helper, OrderedDishes item) {
        if(null!=item)
        {
            helper.dishesName.setText(item.dishesName);
            helper.dishesNum.setText(String.valueOf(item.num));
            helper.dishesMoney.setText(String.valueOf(item.sunTotalMoney)+context.getResources().getString(R.string.money_unit));
        }
    }

    public static class OrderDishesViewHolder extends BaseViewHolder
    {
        @Bind(R.id.dishesName)
        TextView dishesName;
        @Bind(R.id.dishesNum)
        TextView dishesNum;
        @Bind(R.id.dishesMoney)
        TextView dishesMoney;

        public OrderDishesViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
