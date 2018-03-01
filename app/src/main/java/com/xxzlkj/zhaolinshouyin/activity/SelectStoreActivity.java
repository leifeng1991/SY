package com.xxzlkj.zhaolinshouyin.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.xxzlkj.zhaolinshare.base.net.OnMyActivityRequestListener;
import com.xxzlkj.zhaolinshare.base.net.RequestManager;
import com.xxzlkj.zhaolinshare.base.util.ToastManager;
import com.xxzlkj.zhaolinshouyin.R;
import com.xxzlkj.zhaolinshouyin.adapter.SelectStoreAdapter;
import com.xxzlkj.zhaolinshouyin.config.C;
import com.xxzlkj.zhaolinshouyin.config.ZLURLConstants;
import com.xxzlkj.zhaolinshouyin.db.Params;
import com.xxzlkj.zhaolinshouyin.event.InitStoreIdFinishReloadEvent;
import com.xxzlkj.zhaolinshouyin.model.InitBean;
import com.xxzlkj.zhaolinshouyin.model.StoreListBean;
import com.xxzlkj.zhaolinshouyin.utils.CountDownTimerUtils;
import com.xxzlkj.zhaolinshouyin.utils.DaoUtils;
import com.xxzlkj.zhaolinshouyin.utils.PhoneInfo;
import com.xxzlkj.zhaolinshouyin.utils.ZLLooper;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;


/**
 * 选择店铺
 *
 * @author zhangrq
 */
public class SelectStoreActivity extends ZLBaseActivity {

    public static final String PASSWORD = "password";
    private SelectStoreAdapter selectStoreAdapter;
    private String password;
    private TextView mTimeTextView;

    public static Intent newIntent(Context context, String password) {
        Intent intent = new Intent(context, SelectStoreActivity.class);
        intent.putExtra(PASSWORD, password);
        return intent;
    }

    @Override
    protected void loadViewLayout() {
        setContentView(R.layout.activity_select_store);
    }

    @Override
    protected void findViewById() {
        TextView mWelcomeTextView = getView(R.id.id_welcome);
        mTimeTextView = getView(R.id.id_time);
        mWelcomeTextView.setText("欢迎使用兆邻收银系统！" + "\n" + "本设备尚未初始化，请选择绑定门店！");
        RecyclerView recyclerView = getView(R.id.recyclerView);
        // 初始化
        recyclerView.setLayoutManager(new GridLayoutManager(mContext, 4));
        selectStoreAdapter = new SelectStoreAdapter(mContext, R.layout.item_select_store);
        recyclerView.setAdapter(selectStoreAdapter);
    }

    @Override
    protected void setListener() {
        // item点击监听
        selectStoreAdapter.setOnItemClickListener((position, item) -> {
            selectStoreAdapter.setSelectPosition(position);
            selectStoreAdapter.notifyDataSetChanged();
        });
        // 初始化
        getView(R.id.btn_init).setOnClickListener(v -> {
            int selectPosition = selectStoreAdapter.getSelectPosition();
            List<StoreListBean.DataBean> list = selectStoreAdapter.getList();
            if (list == null || list.size() == 0) {
                ToastManager.showShortToast(mContext, "请检查网络，或稍后再试");
            } else if (selectPosition >= list.size()) {
                ToastManager.showShortToast(mContext, "请选择小区");
            } else {
                // 获取选中的id，提交请求
                StoreListBean.DataBean dataBean = list.get(selectPosition);
                summitData(password, String.valueOf(dataBean.getId()), dataBean.getTitle());
            }
        });

    }


    @Override
    protected void processLogic() {
        password = getIntent().getStringExtra(PASSWORD);// 已经判断不为null了
        CountDownTimerUtils.addCountDownTimer(mTimeTextView);
        // 获取小区列表
        getStoreListData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CountDownTimerUtils.removeCountDownTimer(mTimeTextView);
    }

    /**
     * @param password    密码
     * @param store_id    店铺id
     * @param store_title 店铺名
     */
    private void summitData(String password, String store_id, String store_title) {
        HashMap<String, String> map = new HashMap<>();
        map.put(C.P.STORE_ID, store_id);
        map.put(C.P.PASSWORD, password);
        map.put(C.P.SYSTEM_CODE, PhoneInfo.getUId());
        RequestManager.createRequest(ZLURLConstants.CHECK_STORE_PASSWORD_URL, map, new OnMyActivityRequestListener<InitBean>(this) {
            @Override
            public void onSuccess(InitBean initBean) {
                // 验证成功，同步用户库
                showLoadDataAnim();
                ZLLooper.synUser(store_id, new DaoUtils.OnDaoResultListener<String>() {
                    @Override
                    public void onSuccess(String bean) {
                        // 添加用户表，成功，保存状态，跳到登录页面
                        Params params = DaoUtils.getParams();
                        params.setStore_id(store_id);
                        params.setStore_title(store_title);
                        params.setDevices_num(initBean.getData().getId());
                        params.setRong_token(initBean.getData().getToken());
                        DaoUtils.insertOrReplaceParams(params);

                        EventBus.getDefault().postSticky(new InitStoreIdFinishReloadEvent());// 通知副屏网页重新加载

                        startActivity(LoginActivity.newIntent(mContext));
                        hideLoadDataAnim();
                        // 销毁此页面
                        setResult(RESULT_OK);
                        finish();
                    }

                    @Override
                    public void onFailed() {
                        ToastManager.showShortToast(mContext, "同步用户表失败");
                        hideLoadDataAnim();
                    }
                });
            }
        });
    }

    /**
     * 获取小区列表
     */
    private void getStoreListData() {
        HashMap<String, String> map = new HashMap<>();
        RequestManager.createRequest(ZLURLConstants.GET_STORE_LIST_URL, map, new OnMyActivityRequestListener<StoreListBean>(this) {
            @Override
            public void onSuccess(StoreListBean bean) {
                // 网络成功获取，设置数据
                selectStoreAdapter.clearAndAddList(bean.getData());
            }
        });
    }


}
