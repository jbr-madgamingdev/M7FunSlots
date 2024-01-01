package com.masala7.devjo.funslots.base;

import android.content.Context;

import androidx.recyclerview.widget.LinearLayoutManager;

public class GameManager extends LinearLayoutManager {
    private boolean isScrollEnabled = true;

    public GameManager(Context context) {
        super(context);
    }

    public void setScrollEnabled(boolean flag) {
        isScrollEnabled = flag;
    }

    @Override
    public boolean canScrollVertically() {
        return isScrollEnabled && super.canScrollVertically();
    }
}
