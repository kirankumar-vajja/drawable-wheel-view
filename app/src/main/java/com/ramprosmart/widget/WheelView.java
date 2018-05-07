package com.ramprosmart.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class WheelView extends ScrollView
{
    int selectedIndex;

    final String TAG = WheelView.class.getSimpleName();
    int VIEW_HEIGHT = 100;
    final int HIGHLIGHT_TEXT_COLOR = Color.BLACK;
    final int UNHIGHLIGHT_TEXT_COLOR = Color.LTGRAY;

    Context context;
    List<String> items;
    LinearLayout linearLayout;
    int prevHighlightedIndex = -1;
    int visibleCount = 3;

    public WheelView(Context context) {
        super(context);
        init(context);
    }

    public WheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public WheelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context)
    {
        this.context = context;

        linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        this.addView(linearLayout);
        this.setVerticalScrollBarEnabled(false);
    }

    public List<String> getItems() {
        return items;
    }

    public void setItems(List<String> items) {
        if(items != null && !items.isEmpty()) {
            this.items = items;
            if(items.size() < visibleCount) visibleCount = items.size();
            addTextViews();
        }
    }

    public void setItems(String... items) {
        setItems( Arrays.asList(items) );
    }

    private void addTextViews()
    {
        linearLayout.removeAllViews();

        for(String item : items)
        {
            TextView tv = new TextView(context);
            tv.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tv.setSingleLine(true);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            tv.setTextColor(UNHIGHLIGHT_TEXT_COLOR);
            tv.setGravity(Gravity.CENTER_HORIZONTAL);
            tv.setText(item);
            int padding = convertDpToPixel(10);
            tv.setPadding(padding, padding, padding, padding);

            linearLayout.addView(tv);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, convertDpToPixel(VIEW_HEIGHT) ));

        this.post(new Runnable() { // highlight initial item
            @Override
            public void run() {
                resize();
                highlightItem();
            }
        });
    }

    private void resize() {
        int height = 0;
        for(int i=0; i<visibleCount; i++) {
            height += linearLayout.getChildAt(i).getHeight();
        }

        VIEW_HEIGHT = height;
        this.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, convertDpToPixel(VIEW_HEIGHT) ));

        TextView firstChild = (TextView) linearLayout.getChildAt(0);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) firstChild.getLayoutParams();
        layoutParams.setMargins(0, convertDpToPixel(VIEW_HEIGHT/2), 0, 0);
        firstChild.setLayoutParams(layoutParams);

        TextView lastChild = (TextView) linearLayout.getChildAt(linearLayout.getChildCount()-1);
        layoutParams = (LinearLayout.LayoutParams) lastChild.getLayoutParams();
        layoutParams.setMargins(0, 0,0, convertDpToPixel(VIEW_HEIGHT/2));
        lastChild.setLayoutParams(layoutParams);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        highlightItem();
    }

    private void highlightItem() {
        int selectedItemIndex = inferSelectedItemIndex();

        highlightItem(selectedItemIndex);
        if(prevHighlightedIndex >= 0 && prevHighlightedIndex != selectedItemIndex) {
            unhighlightItem(prevHighlightedIndex);
        }

        prevHighlightedIndex = selectedItemIndex;
    }

    private int inferSelectedItemIndex()
    {
        int scrollY = (int) getY()+getHeight()/2;
        Integer selectedIndex = null;
        for(int i=0; i<linearLayout.getChildCount() && selectedIndex == null; i++) {
            View view = linearLayout.getChildAt(i);
            int viewLocationY = getViewLocationY(view);

            if(viewLocationY >= scrollY) {
                selectedIndex = i;
            }

            //Log.e(TAG, ((TextView)view).getText()+", viewLocationY: "+viewLocationY+", scrollY: "+scrollY+", selectedIndex: "+selectedIndex);
        }

        if(selectedIndex == null) selectedIndex = linearLayout.getChildCount()-1;
        return selectedIndex;
    }

    private int getViewLocationY(View view)
    {
        int[] location = {0, (int) getY()};
        view.getLocationOnScreen(location);
        int viewLocationY = location[1];
        return  viewLocationY;
    }

    private void highlightItem(int index)
    {
        TextView tv = (TextView) linearLayout.getChildAt(index);
        tv.setBackgroundColor(Color.LTGRAY);
        tv.setTextColor(HIGHLIGHT_TEXT_COLOR);

        selectedIndex = index;
    }

    private void unhighlightItem(int index)
    {
        TextView tv = (TextView) linearLayout.getChildAt(index);
        tv.setBackgroundColor(Color.TRANSPARENT);
        tv.setTextColor(UNHIGHLIGHT_TEXT_COLOR);
    }

    // https://gist.github.com/laaptu/7867851
    public static int convertDpToPixel(float dp){
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return (int) Math.round(px);
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public int getVisibleCount() {
        return visibleCount;
    }

    public void setVisibleCount(int visibleCount) {
        this.visibleCount = visibleCount;
    }
}
