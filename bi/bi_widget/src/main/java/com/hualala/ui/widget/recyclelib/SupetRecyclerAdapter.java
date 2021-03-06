package com.hualala.ui.widget.recyclelib;

import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import java.util.ArrayList;
import java.util.List;


public abstract class SupetRecyclerAdapter<T> {

    public abstract int getItemViewType(int position);

    private List<T> mDatas = new ArrayList<>();

    private Context mContext;

    public Context getContext() {
        return mContext;
    }

    private SupetRecyclerView.SupetRecyclerViewAdapter mDRecyclerViewAdapter;

    private int itemHeight;

    public int getItemHeight() {
        return itemHeight;
    }

    public SupetRecyclerView.SupetRecyclerViewAdapter getmDRecyclerViewAdapter() {
        return mDRecyclerViewAdapter;
    }

    public void setRealRecyclerViewAdapter(SupetRecyclerView.SupetRecyclerViewAdapter mDRecyclerViewAdapter) {
        this.mDRecyclerViewAdapter = mDRecyclerViewAdapter;
    }

    public SupetRecyclerAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void addHomePage(List<T> data) {
        mDatas.clear();
        if (data != null) {
            this.mDatas.addAll(data);
        }
        notifyDataSetChanged();
    }

    public void insertHomePage(T data) {
        if (data != null) {
            this.mDatas.add(0, data);
        }
        notifyDataSetChanged();
    }

    public void addNextPage(List<T> data) {
        if (data != null) {
            int itemCount = data.size();
            int postionStart = getItemCount();
            this.mDatas.addAll(data);
            notifyItemRangeInserted(postionStart, itemCount);
        }
    }

    public void addNextPage(T data) {
        if (data != null) {
            int itemCount = 1;
            int postionStart = getItemCount();
            this.mDatas.add(data);
            notifyItemRangeInserted(postionStart, itemCount);
        }
    }

    public SupetRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final SupetRecyclerViewHolder holder = onCreateViewHolder(parent, viewType, this);
        holder.getWholeView().getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        itemHeight = holder.getWholeView().getMeasuredHeight();
                        return true;
                    }
                }
        );
        return holder;
    }


    public void onBindViewHolder(SupetRecyclerViewHolder holder, int position) {
        holder.setData(mDatas.get(position), position);
    }

    public int getItemCount() {
        return mDatas.size();
    }

    public boolean isFullSpan(int position) {
        return false;
    }


    public abstract SupetRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType, SupetRecyclerAdapter adapter);


    public void notifyDataSetChanged() {
        mDRecyclerViewAdapter.notifyDataSetChanged();
    }

    public void notifyItemChanged(int position) {
        position = mDRecyclerViewAdapter.getHeaderViewsCount() + position;
        mDRecyclerViewAdapter.notifyItemChanged(position);
    }

    public void notifyItemInserted(int position) {
        position = mDRecyclerViewAdapter.getHeaderViewsCount() + position;
        mDRecyclerViewAdapter.notifyItemInserted(position);
    }

    public void notifyItemRemoved(int position) {
        position = mDRecyclerViewAdapter.getHeaderViewsCount() + position;
        mDRecyclerViewAdapter.notifyItemRemoved(position);
    }

    public void notifyItemRangeChanged(int positionStart, int itemCount) {
        positionStart = mDRecyclerViewAdapter.getHeaderViewsCount() + positionStart;
        mDRecyclerViewAdapter.notifyItemRangeChanged(positionStart, itemCount);
    }

    public void notifyItemRangeInserted(int positionStart, int itemCount) {
        positionStart = mDRecyclerViewAdapter.getHeaderViewsCount() + positionStart;
        mDRecyclerViewAdapter.notifyItemRangeInserted(positionStart, itemCount);
    }

    public void notifyItemRangeRemoved(int positionStart, int itemCount) {
        positionStart = mDRecyclerViewAdapter.getHeaderViewsCount() + positionStart;
        mDRecyclerViewAdapter.notifyItemRangeRemoved(positionStart, itemCount);
    }

    public List<T> getData() {
        return mDatas;
    }

    public T getData(int position) {
        return mDatas.get(position);
    }

    public boolean isEmpty() {
        return mDatas.isEmpty();
    }


    public void insert(int position, T temp) {
        this.mDatas.add(position, temp);
        this.notifyItemInserted(position);
        this.notifyItemRangeChanged(position, 1);
    }

    public void insert(int position, List<T> temp) {
        this.mDatas.addAll(position, temp);
        this.notifyItemInserted(position);
        this.notifyItemRangeChanged(position, temp.size());
    }

    private boolean isDelete = false;

    public void removeAnim(int position) {
        if (!isDelete) {
            isDelete = true;
            //有动画快速删除，任意出现数组越界异常
            if (position < mDatas.size() && mDatas.size() > 0) {
                mDatas.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(0, getItemCount() - position);
            } else {
                Log.v(this.getClass().getSimpleName(),
                        "删除异常了");
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    isDelete = false;
                }
            }).start();
        } else {
            Log.v(this.getClass().getSimpleName(),
                    "兄弟，手速慢点");
        }
    }

}
