package com.aaron.group.smartmeal.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.aaron.group.smartmeal.R;
import com.aaron.group.smartmeal.bean.DishesBean;
import com.aaron.group.smartmeal.bean.OrderedDishes;
import com.aaron.group.smartmeal.listener.OnCarListener;
import com.aaron.group.smartmeal.listener.OnItemClickListener;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;



public class CarAdapter extends BaseQuickAdapter<OrderedDishes, CarAdapter.CarViewHolder> {


    private Context context;
    private OnCarListener onCarListener;

    public CarAdapter(Context context, List<OrderedDishes> datas)
    {
        super(datas);
        this.context = context;
    }

    public void setOnCarListener(OnCarListener onCarListener) {
        this.onCarListener = onCarListener;
    }

    @Override
    protected CarAdapter.CarViewHolder createBaseViewHolder(ViewGroup parent, int layoutResId) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_pop_car, parent, false);
        return new CarAdapter.CarViewHolder(view);
    }

    @Override
    protected void convert(final CarViewHolder helper, OrderedDishes item) {
        if(null!=item)
        {
            helper.dishesName.setText(item.dishesName);
            helper.num.setText(String.valueOf(item.num));
            helper.dishesPrice.setText(item.sunTotalMoney+context.getResources().getString(R.string.money_unit));
            if(null!=onCarListener)
            {
                helper.plus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onCarListener.plus(v, helper.getLayoutPosition());
                    }
                });

                helper.less.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onCarListener.less(v, helper.getLayoutPosition());
                    }
                });
                helper.delBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onCarListener.del(v, helper.getLayoutPosition());
                    }
                });
            }
        }
    }

    public static class CarViewHolder extends BaseViewHolder
    {

        @Bind(R.id.dishesName)
        TextView dishesName;
        @Bind(R.id.less)
        TextView less;
        @Bind(R.id.num)
        EditText num;
        @Bind(R.id.plus)
        TextView plus;
        @Bind(R.id.dishesPrice)
        TextView dishesPrice;
        @Bind(R.id.delBtn)
        ImageView delBtn;

        public CarViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
