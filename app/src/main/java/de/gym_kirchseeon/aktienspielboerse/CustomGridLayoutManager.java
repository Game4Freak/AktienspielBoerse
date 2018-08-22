package de.gym_kirchseeon.aktienspielboerse;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;

/**
 * Customizing GridLayout to allow scrolling within
 */
public class CustomGridLayoutManager extends LinearLayoutManager {
    private boolean isScrollEnabled = true;

    public CustomGridLayoutManager(Context context) {
        super(context);
    }

    @Override
    public boolean canScrollVertically() {
        return isScrollEnabled && super.canScrollVertically();
    }
}