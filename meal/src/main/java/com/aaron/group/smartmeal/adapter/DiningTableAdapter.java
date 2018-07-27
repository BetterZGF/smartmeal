package com.aaron.group.smartmeal.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aaron.group.smartmeal.R;
import com.aaron.group.smartmeal.bean.DiningTableBean;
import com.aaron.group.smartmeal.bean.DiningTableShowBean;
import com.aaron.group.smartmeal.listener.OnItemClickListener;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 */

public class DiningTableAdapter extends BaseQuickAdapter<DiningTableShowBean, DiningTableAdapter.DiningTableViewHolder> {

    private Context context;
    private OnItemClickListener mOnItemClickListener;

    public DiningTableAdapter(Context context, List<DiningTableShowBean> datas)
    {
        super(datas);
        this.context = context;
    }

    @Override
    protected DiningTableAdapter.DiningTableViewHolder createBaseViewHolder(ViewGroup parent, int layoutResId) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_dinindtable, parent, false);
        return new DiningTableAdapter.DiningTableViewHolder(view);
    }

    public void setmOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    @Override
    protected void convert(final DiningTableViewHolder helper, DiningTableShowBean item) {
        if(null!=item)
        {
            DiningTableBean diningTable = item.diningTable;
            boolean isChoice = item.isChoice;
            helper.diningTableNo.setText(diningTable.diningTableNo+context.getResources().getString(R.string.order_dingtable_tips));
            helper.diningTableToatalNums.setText(diningTable.diningTableToatalNums+context.getResources().getString(R.string.diningtable_toatal_nums));
            if(isChoice)
            {
                Glide.with(context).load("").centerCrop().placeholder(R.mipmap.dining_table_choice).into(helper.diningTableImg);
            }
            else
            {
                Glide.with(context).load("").centerCrop().placeholder(R.mipmap.dining_table_defluat).into(helper.diningTableImg);
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

    public static class DiningTableViewHolder extends BaseViewHolder
    {

        @Bind(R.id.diningTableImg)
        ImageView diningTableImg;
        @Bind(R.id.diningTableNo)
        TextView diningTableNo;
        @Bind(R.id.diningTableToatalNums)
        TextView diningTableToatalNums;

        public DiningTableViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
