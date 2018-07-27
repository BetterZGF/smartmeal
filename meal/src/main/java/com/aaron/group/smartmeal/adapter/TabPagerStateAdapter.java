package com.aaron.group.smartmeal.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * 说明: tab页切换管理适配器

 */

public class TabPagerStateAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> fragmentList;

    public TabPagerStateAdapter(FragmentManager fm, List<Fragment> fragmentList)
    {
        super(fm);
        this.fragmentList = fragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        return (fragmentList == null || fragmentList.size() == 0) ? null
                : fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList == null ? 0 : fragmentList.size();
    }
}
