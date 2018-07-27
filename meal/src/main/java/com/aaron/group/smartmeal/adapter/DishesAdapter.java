package com.aaron.group.smartmeal.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aaron.group.smartmeal.R;
import com.aaron.group.smartmeal.bean.DishesBean;
import com.aaron.group.smartmeal.listener.OnItemClickListener;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class DishesAdapter extends BaseQuickAdapter<DishesBean, DishesAdapter.DishesViewHolder> {

    private Context context;
    private OnItemClickListener mOnItemClickListener;

    public DishesAdapter(Context context, List<DishesBean> datas)
    {
        super(datas);
        this.context = context;
    }

    @Override
    protected DishesAdapter.DishesViewHolder createBaseViewHolder(ViewGroup parent, int layoutResId) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_home_dishes, parent, false);
        return new DishesAdapter.DishesViewHolder(view);
    }

    public void setmOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    @Override
    protected void convert(final DishesViewHolder helper, DishesBean item) {
        if(null!=item)
        {
            Glide.with(context).load(item.dishesImg).centerCrop().placeholder(R.mipmap.ic_launcher).into(helper.dishesImg);
            helper.dishesName.setText(item.dishesName);
            helper.dishesPrice.setText(item.dishesPrice+context.getResources().getString(R.string.money_unit));
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

    public static class DishesViewHolder extends BaseViewHolder
    {
        @Bind(R.id.dishesImg)
        ImageView dishesImg;
        @Bind(R.id.dishesName)
        TextView dishesName;
        @Bind(R.id.dishesPrice)
        TextView dishesPrice;

        public DishesViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
