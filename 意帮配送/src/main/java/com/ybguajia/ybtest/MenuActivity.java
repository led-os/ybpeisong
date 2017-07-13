package com.ybguajia.ybtest;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ybguajia.ybtest.entity.OrderNum;
import com.ybguajia.ybtest.entity.UserMsg;
import com.ybguajia.ybtest.utils.DataUtils;
import com.ybguajia.ybtest.utils.GlobalConstant;
import com.ybguajia.ybtest.utils.PromptManager;
import com.ybguajia.ybtest.utils.SharePreUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;

import okhttp3.Call;
import okhttp3.Response;

public class MenuActivity extends Activity implements View.OnClickListener {

    public String URL = "";
    private boolean isFirstRun = true;

    private TextView tvWeiPeiHuoNum, tvPeiHuoZhongNum, tvLingQuZhongNum;
    private TextView tvPeiHuoWanBiNum, tvPeiSongZhongNum, tvWenTiDanNum;
    private TextView tvUnLogin;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private LinearLayout llWeipeihuo, llLingquzhong, llPeihuozhong;
    private LinearLayout llPeisongzhong, llPeisongwancheng, llWentidan;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        URL = GlobalConstant.SERVER_IP + "managecenter.aspx?key=" + DataUtils.md5ByDay() + "&uid=" + SharePreUtils
                .getDataString(GlobalConstant.USER_ID, "", MenuActivity.this);
        findView();
        setListener();
    }

    private void setListener() {
        tvUnLogin.setOnClickListener(this);
        llWeipeihuo.setOnClickListener(this);
        llLingquzhong.setOnClickListener(this);
        llPeihuozhong.setOnClickListener(this);
        llPeisongzhong.setOnClickListener(this);
        llPeisongwancheng.setOnClickListener(this);
        llWentidan.setOnClickListener(this);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isFirstRun = true;
                getDataByServer();
            }
        });
    }

    private void getDataByServer() {
        OkHttpUtils.get().url(URL).build().execute(new OrderNumCallback() {
            @Override
            public void onError(Call call, Exception e, int i) {
                mSwipeRefreshLayout.setRefreshing(false);
                PromptManager.showToast(MenuActivity.this, R.string.server_error);
            }

            @Override
            public void onResponse(OrderNum orderNum, int i) {
                mSwipeRefreshLayout.setRefreshing(false);
                if (orderNum != null) {
                    if ("1".equals(orderNum.result)) {
                        //填充页面数据
                        tvWeiPeiHuoNum.setText(orderNum.weipeihuo);
                        tvLingQuZhongNum.setText(orderNum.lingquzhong);
                        tvPeiHuoZhongNum.setText(orderNum.peihuozhong);
                        tvPeiHuoWanBiNum.setText(orderNum.peihuowan);
                        tvPeiSongZhongNum.setText(orderNum.peisongzhong);
                        tvWenTiDanNum.setText(orderNum.wentidan);
                    } else {
                        if (isFirstRun) {
                            PromptManager.showToast(MenuActivity.this, orderNum.message);
                        }
                    }
                } else {
                    PromptManager.showToast(MenuActivity.this, R.string.server_error);
                }

            }
        });
    }

    private void findView() {
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swip_menu);
        tvUnLogin = (TextView) findViewById(R.id.tv_unlogin);
        tvWeiPeiHuoNum = (TextView) findViewById(R.id.tv_weipeihuo_num);
        tvPeiHuoZhongNum = (TextView) findViewById(R.id.tv_peihuozhong_num);
        tvLingQuZhongNum = (TextView) findViewById(R.id.tv_lingquzhong_num);
        tvPeiHuoWanBiNum = (TextView) findViewById(R.id.tv_peihuowancheng_num);
        tvPeiSongZhongNum = (TextView) findViewById(R.id.tv_peisongzhong_num);
        tvWenTiDanNum = (TextView) findViewById(R.id.tv_wentidan_num);


        mSwipeRefreshLayout.setColorSchemeResources(R.color.main_text_color);
        mSwipeRefreshLayout.setDistanceToTriggerSync(300);// 设置手指在屏幕下拉多少距离会触发下拉刷新
        mSwipeRefreshLayout.setSize(SwipeRefreshLayout.DEFAULT);

        llWeipeihuo = (LinearLayout) findViewById(R.id.ll_weipeihuo);
        llLingquzhong = (LinearLayout) findViewById(R.id.ll_lingquzhong);
        llPeihuozhong = (LinearLayout) findViewById(R.id.ll_peihuozhong);
        llPeisongzhong = (LinearLayout) findViewById(R.id.ll_peisongzhong);
        llPeisongwancheng = (LinearLayout) findViewById(R.id.ll_peisongwancheng);
        llWentidan = (LinearLayout) findViewById(R.id.ll_wentidan);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_unlogin:
                //退出登录
                SharePreUtils.delete(GlobalConstant.USER_ID, this);
                inStartActivity(LoginActivity.class);
                MenuActivity.this.finish();
                break;
            case R.id.ll_weipeihuo:
                //未配货
                inStartActivity(WeipeihuoActivity.class);
                break;
            case R.id.ll_lingquzhong:
                //领取中
                inStartActivity(LingquzhongActivity.class);
                break;
            case R.id.ll_peihuozhong:
                //配货中
                inStartActivity(PeihuozhongActivity.class);
                break;
            case R.id.ll_peisongzhong:
                //配送中
                inStartActivity(PeisongzhongActivity.class);
                break;
            case R.id.ll_peisongwancheng:
                //配货完成
                inStartActivity(PeisongwanchengActivity.class);
                break;
            case R.id.ll_wentidan:
                //问题单
                inStartActivity(ErrorActivity.class);
                break;

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDataByServer();
        isFirstRun = false;
    }

    abstract class OrderNumCallback extends Callback<OrderNum> {
        @Override
        public OrderNum parseNetworkResponse(Response response, int i) throws Exception {
            String result = response.body().string();
            return new Gson().fromJson(result, OrderNum.class);
        }
    }

    public void inStartActivity(Class target) {
        Intent intent = new Intent(this, target);
        startActivity(intent);
    }
}
