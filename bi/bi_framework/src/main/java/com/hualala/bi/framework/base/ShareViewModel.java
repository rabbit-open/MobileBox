package com.hualala.bi.framework.base;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

/**
 * 只有相同生命周期管理共享
 */
public abstract class ShareViewModel<T> extends ViewModel {

    private final MutableLiveData<T> data = new MutableLiveData<>();

    public void select(T item) {
        data.setValue(item);
    }

    public LiveData<T> getData() {
        return data;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        Log.v("ShareViewModel", "不同页面会销毁");
    }
}