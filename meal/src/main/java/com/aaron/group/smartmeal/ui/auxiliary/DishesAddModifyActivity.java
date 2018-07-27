package com.aaron.group.smartmeal.ui.auxiliary;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aaron.group.smartmeal.R;
import com.aaron.group.smartmeal.base.BaseActivity;
import com.aaron.group.smartmeal.bean.DishesBean;
import com.aaron.group.smartmeal.bean.DishesCategoryBean;
import com.aaron.group.smartmeal.utils.ActivityUtils;
import com.aaron.group.smartmeal.utils.CommonUtils;
import com.bigkoo.pickerview.OptionsPickerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 说明:

 */

public class DishesAddModifyActivity extends BaseActivity {

    @Bind(R.id.toolBar)
    Toolbar toolBar;
    @Bind(R.id.leftBarIcon)
    ImageView leftBarIcon;
    @Bind(R.id.barTitle)
    TextView barTitle;
    @Bind(R.id.rightBarIcon)
    ImageView rightBarIcon;
    @Bind(R.id.leftBarText)
    TextView leftBarText;
    @Bind(R.id.rightBarText)
    TextView rightBarText;
    @Bind(R.id.dishesName)
    EditText dishesName;
    @Bind(R.id.dishesCatagory)
    TextView dishesCatagory;
    @Bind(R.id.dishesStatus)
    TextView dishesStatus;
    @Bind(R.id.dishesPrice)
    EditText dishesPrice;
    @Bind(R.id.dishesDesc)
    EditText dishesDesc;
    @Bind(R.id.sureBtn)
    RelativeLayout sureBtn;

    private OptionsPickerView<String> statusOptions;
    private OptionsPickerView<String> dishesCategoryOptions;

    private DishesBean dishes;

    @OnClick(R.id.sureBtn)
    void sureBtn()
    {
        //提交
        if("0".equals(String.valueOf(sureBtn.getTag())))
        {
            //新增
            DishesBean lastDishes = DishesBean.last(DishesBean.class);
            dishes = new DishesBean();
            if(null!=lastDishes)
            {
                dishes.dishesId = lastDishes.dishesId+1;
            }
            else
            {
                dishes.dishesId = 10001;
            }
            dishes.dishesImg = "";
            dishes.dishesName = dishesName.getText().toString();
            if("启用".equals(dishesStatus.getText().toString()))
            {
                dishes.dishesStatus = 0;
            }
            else if("禁用".equals(dishesStatus.getText().toString()))
            {
                dishes.dishesStatus = 1;
            }
            dishes.categoryId = Integer.parseInt(String.valueOf(dishesCatagory.getTag()));
            dishes.dishesPrice = dishesPrice.getText().toString();
            dishes.dishesDesc = dishesDesc.getText().toString();
            dishes.save();
        }
        else if("1".equals(String.valueOf(sureBtn.getTag())))
        {
            //修改
            dishes.dishesName = dishesName.getText().toString();
            if("启用".equals(dishesStatus.getText().toString()))
            {
                dishes.dishesStatus = 0;
            }
            else if("禁用".equals(dishesStatus.getText().toString()))
            {
                dishes.dishesStatus = 1;
            }
            dishes.categoryId = Integer.parseInt(String.valueOf(dishesCatagory.getTag()));
            dishes.dishesPrice = dishesPrice.getText().toString();
            dishes.dishesDesc = dishesDesc.getText().toString();
            dishes.save();
        }


        ActivityUtils.getInstance().closeSelf(DishesAddModifyActivity.this);
    }

    @OnClick({R.id.dishesCatagoryL, R.id.dishesCatagory})
    void dishesCatagoryL()
    {
        //隐藏输入面板
        InputMethodManager imm = (InputMethodManager)DishesAddModifyActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm.isActive())
        {
            //isOpen若返回true，则表示输入法打开
            imm.hideSoftInputFromWindow(dishesName.getWindowToken(), 0);
        }
        if(null!=dishesCategoryOptions)
        {
            final List<DishesCategoryBean> dishesCategorys = DishesCategoryBean.find(DishesCategoryBean.class, "categoryStatus = ?", "0");
            final ArrayList<String> datas = new ArrayList<String>();
            if(null!=dishesCategorys&&!dishesCategorys.isEmpty())
            {
                for(DishesCategoryBean dishesCategory:dishesCategorys)
                {
                    datas.add(dishesCategory.categoryName);
                }
                dishesCategoryOptions.setPicker(datas);
                dishesCategoryOptions.setCyclic(false);
                dishesCategoryOptions.setCancelable(true);
                dishesCategoryOptions.setTitle("选择菜品类型");
                dishesCategoryOptions.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
                    @Override
                    public void onOptionsSelect(int options1, int option2, int options3) {
                        dishesCatagory.setText(dishesCategorys.get(options1).categoryName);
                        dishesCatagory.setTag(dishesCategorys.get(options1).categoryId);
                    }
                });
                if(dishesCategoryOptions.isShowing())
                {
                    dishesCategoryOptions.dismiss();
                }
                dishesCategoryOptions.show();
            }
        }
    }

    @OnClick({R.id.dishesStatusL, R.id.dishesStatus})
    void dishesStatusL()
    {

        //隐藏输入面板
        InputMethodManager imm = (InputMethodManager)DishesAddModifyActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm.isActive())
        {
            //isOpen若返回true，则表示输入法打开
            imm.hideSoftInputFromWindow(dishesName.getWindowToken(), 0);
        }
        if(null!=statusOptions)
        {
            final ArrayList<String> datas = new ArrayList<String>();
            datas.add("启用");
            datas.add("禁用");
            statusOptions.setPicker(datas);
            statusOptions.setCyclic(false);
            statusOptions.setCancelable(true);
            statusOptions.setTitle("选择菜品状态");
            statusOptions.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
                @Override
                public void onOptionsSelect(int options1, int option2, int options3) {
                    dishesStatus.setText(datas.get(options1));
                }
            });
            if(statusOptions.isShowing())
            {
                statusOptions.dismiss();
            }
            statusOptions.show();
        }
    }

    @OnClick(R.id.leftBarL)
    void leftBarL()
    {
        //关闭
        ActivityUtils.getInstance().closeSelf(DishesAddModifyActivity.this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.layout_dishes_add_modify);
        ButterKnife.bind(this);
        statusOptions = new OptionsPickerView<String>(DishesAddModifyActivity.this);
        dishesCategoryOptions = new OptionsPickerView<String>(DishesAddModifyActivity.this);
        initTitle();
        initContent();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //检测手机底部返回菜单，关闭界面
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN)
        {
            //关闭
            ActivityUtils.getInstance().closeSelf(DishesAddModifyActivity.this);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null!=statusOptions)
        {
            statusOptions.dismiss();
        }
        if(null!=dishesCategoryOptions)
        {
            dishesCategoryOptions.dismiss();
        }
        ButterKnife.unbind(this);
    }

    @Override
    public void initTitle() {
        //设置标题栏布局
        toolBar.setTitle("");
        barTitle.setText("菜品数据");
        CommonUtils.loadBackground(leftBarIcon, resources.getDrawable(R.mipmap.title_back));
        rightBarIcon.setVisibility(View.GONE);
        toolBar.setNavigationIcon(null);
    }

    @Override
    public void initContent() {
        Bundle bundle = this.getIntent().getExtras();
        if(null!=bundle)
        {
            sureBtn.setTag(1);
            //修改
            dishes = (DishesBean) bundle.getSerializable("dishes");
            List<DishesCategoryBean> dishesCategorys = DishesCategoryBean.find(DishesCategoryBean.class, "categoryId = ?", String.valueOf(dishes.categoryId));
            dishesName.setText(dishes.dishesName);
            if(null!=dishesCategorys&&!dishesCategorys.isEmpty())
            {
                dishesCatagory.setText(dishesCategorys.get(0).categoryName);
                dishesCatagory.setTag(dishesCategorys.get(0).categoryId);
            }
            dishesStatus.setText((0==dishes.dishesStatus)?"启用":"禁用");
            dishesPrice.setText(dishes.dishesPrice);
            dishesDesc.setText(dishes.dishesDesc);
        }
        else
        {
            //新增
            sureBtn.setTag(0);
        }
    }
}
