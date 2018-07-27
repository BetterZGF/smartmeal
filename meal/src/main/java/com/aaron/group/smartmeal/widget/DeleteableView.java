package com.aaron.group.smartmeal.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.HorizontalScrollView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aaron.group.smartmeal.R;

/**
 * 说明: 删除餐品信息组件

 */

public class DeleteableView extends HorizontalScrollView {

    private TextView mTextView_Delete;

    private int mScrollWidth;

    private IonSlidingButtonListener mIonSlidingButtonListener;

    private Boolean isOpen = false;
    private Boolean once = false;


    public DeleteableView(Context context) {
        this(context, null);
    }

    public DeleteableView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public DeleteableView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        this.setOverScrollMode(OVER_SCROLL_NEVER);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(!once){
            mTextView_Delete = (TextView) findViewById(R.id.deleteT);
            once = true;
        }
        final RelativeLayout parent = (RelativeLayout) mTextView_Delete.getParent();
        ViewTreeObserver vto2 = parent.getViewTreeObserver();
        vto2.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                parent.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                mTextView_Delete.setHeight(parent.getHeight());
            }
        });
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if(changed){
            this.scrollTo(0,0);
            mScrollWidth = mTextView_Delete.getWidth();
        }

    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                mIonSlidingButtonListener.onDownOrMove(this);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                changeScrollx();
                return true;
            default:
                break;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        mTextView_Delete.setTranslationX(l - mScrollWidth);
    }

    /**
     * °´¹ö¶¯Ìõ±»ÍÏ¶¯¾àÀëÅÐ¶Ï¹Ø±Õ»ò´ò¿ª²Ëµ¥
     */
    public void changeScrollx(){
        if(getScrollX() >= (mScrollWidth/2)){
            this.smoothScrollTo(mScrollWidth, 0);
            isOpen = true;
            mIonSlidingButtonListener.onMenuIsOpen(this);
        }else{
            this.smoothScrollTo(0, 0);
            isOpen = false;
        }
    }

    /**
     * ´ò¿ª²Ëµ¥
     */
    public void openMenu()
    {
        if (isOpen){
            return;
        }
        this.smoothScrollTo(mScrollWidth, 0);
        isOpen = true;
        mIonSlidingButtonListener.onMenuIsOpen(this);
    }

    /**
     * ¹Ø±Õ²Ëµ¥
     */
    public void closeMenu()
    {
        if (!isOpen){
            return;
        }
        this.smoothScrollTo(0, 0);
        isOpen = false;
    }




    public void setSlidingButtonListener(IonSlidingButtonListener listener){
        mIonSlidingButtonListener = listener;
    }

    public interface IonSlidingButtonListener{
        void onMenuIsOpen(View view);
        void onDownOrMove(DeleteableView deleteableView);
    }


}