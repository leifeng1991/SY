package com.xxzlkj.zhaolinshare.base.base;

import android.os.Bundle;

/**
 * 描述:
 *
 * @author zhangrq
 *         2016/12/28 18:03
 */
public abstract class HintRefreshFragment extends ReuseViewFragment {

    protected boolean isViewInitiated;
    protected boolean isVisibleToUser;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // 执行顺序 onAttach...onCreate...onCreateView...onActivityCreated...onStart...onResume...
        isViewInitiated = true;
        prepareRefreshOnceData();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isVisibleToUser = isVisibleToUser;
        prepareRefreshOnceData();
    }

    public abstract void hintRefreshData();

    public void prepareRefreshOnceData() {
        if (isVisibleToUser && isViewInitiated  ) {
            hintRefreshData();
        }
    }

}
