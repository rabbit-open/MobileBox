package com.hualala.ui.widget.recyclelib;

import android.support.v7.widget.GridLayoutManager;

public class SupetSpanSizeLookup extends GridLayoutManager.SpanSizeLookup {

    private SupetRecyclerView.SupetRecyclerViewAdapter adapter;
    private int mSpanSize = 1;

    public SupetSpanSizeLookup(SupetRecyclerView.SupetRecyclerViewAdapter adapter, int spanSize) {
        this.adapter = adapter;
        this.mSpanSize = spanSize;
    }

    @Override
    public int getSpanSize(int position) {
       boolean isHeaderOrFooter = adapter.isHeader(position) || adapter.isFooter(position);
        if (!isHeaderOrFooter){
            return adapter.isFullSpan(position-adapter.getHeaderViewsCount()) ? mSpanSize : 1;
        }
        return mSpanSize;
    }
}