package com.aaron.group.smartmeal.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aaron.group.smartmeal.R;
import com.aaron.group.smartmeal.bean.DishesBean;
import com.aaron.group.smartmeal.bean.OrderBean;
import com.aaron.group.smartmeal.listener.OnDeleteClickListener;
import com.aaron.group.smartmeal.listener.OnItemClickListener;
import com.aaron.group.smartmeal.widget.DeleteableView;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;




public class DishesManagerAdapter extends BaseQuickAdapter<DishesBean, DishesManagerAdapter.DishesManagerViewHolder> implements DeleteableView.IonSlidingButtonListener {

    private Context context;
    private OnItemClickListener mOnItemClickListener;
    private OnDeleteClickListener mIDeleteBtnClickListener;
    private DeleteableView dView = null;

    public void setmOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public void setmIDeleteBtnClickListener(OnDeleteClickListener mIDeleteBtnClickListener) {
        this.mIDeleteBtnClickListener = mIDeleteBtnClickListener;
    }

    public DishesManagerAdapter(Context context, List<DishesBean> datas)
    {
        super(datas);
        this.context = context;
    }

    @Override
    protected DishesManagerAdapter.DishesManagerViewHolder createBaseViewHolder(ViewGroup parent, int layoutResId) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_disher_manager, parent, false);
        return new DishesManagerAdapter.DishesManagerViewHolder(view, context);
    }

    @Override
    protected void convert(final DishesManagerViewHolder helper, DishesBean item) {
        if(null!=item)
        {
            Glide.with(context).load(item.dishesImg).centerCrop().placeholder(R.mipmap.ic_launcher).into(helper.dishesIcon);
            helper.dishesName.setText(item.dishesName);
            helper.dishesPrice.setText("单价："+item.dishesPrice+"元");
            helper.dishesDesc.setText(item.dishesDesc);
            if(null!=mIDeleteBtnClickListener)
            {
                helper.deleteL.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mIDeleteBtnClickListener.onDeleteBtnCilck(v, helper.getLayoutPosition());
                    }
                });
            }
            if(null!=mOnItemClickListener)
            {
                helper.itemL.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnItemClickListener.onItemClick(v, helper.getLayoutPosition());
                    }
                });
            }
        }
    }

    @Override
    public void onMenuIsOpen(View view) {
        dView = (DeleteableView) view;
    }

    @Override
    public void onDownOrMove(DeleteableView deleteableView) {
        if(null!=dView)
        {
            if(dView!=deleteableView)
            {
                dView.closeMenu();
                dView = null;
            }
        }
    }

    public class DishesManagerViewHolder extends BaseViewHolder
    {
        @Bind(R.id.deleteT)
        TextView deleteT;
        @Bind(R.id.deleteL)
        RelativeLayout deleteL;
        @Bind(R.id.itemL)
        LinearLayout itemL;
        @Bind(R.id.dishesIcon)
        ImageView dishesIcon;
        @Bind(R.id.dishesName)
        TextView dishesName;
        @Bind(R.id.dishesPrice)
        TextView dishesPrice;
        @Bind(R.id.dishesDesc)
        TextView dishesDesc;

        public DishesManagerViewHolder(View itemView, Context context) {
            super(itemView);
            WindowManager windowManager = ((Activity)context).getWindowManager();
            ButterKnife.bind(this, itemView);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) itemL.getLayoutParams();
            params.width = windowManager.getDefaultDisplay().getWidth();
            ((DeleteableView) itemView).setSlidingButtonListener(DishesManagerAdapter.this);
            RelativeLayout.LayoutParams lp01 = (RelativeLayout.LayoutParams) deleteT.getLayoutParams();
            lp01.addRule(RelativeLayout.CENTER_IN_PARENT);
            deleteT.setLayoutParams(lp01);
        }
    }
}
