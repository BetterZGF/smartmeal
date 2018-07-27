package com.aaron.group.smartmeal.listener;

import android.view.View;

/**
 * 说明:

 */

public interface OnCarListener {
    void plus(View view, int position);
    void less(View view, int position);
    void del(View view, int position);
}
